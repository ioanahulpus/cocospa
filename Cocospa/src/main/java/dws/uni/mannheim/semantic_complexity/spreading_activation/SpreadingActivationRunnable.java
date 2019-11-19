package dws.uni.mannheim.semantic_complexity.spreading_activation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.rdfhdt.hdt.hdt.HDT;

import au.com.bytecode.opencsv.CSVWriter;
import dws.uni.mannheim.semantic_complexity.FeaturedDocument;
import dws.uni.mannheim.semantic_complexity.KanopyDocument;
import dws.uni.mannheim.semantic_complexity.KanopyDocumentJsonReader;
import dws.uni.mannheim.semantic_complexity.LinkedDocument;
import dws.uni.mannheim.semantic_complexity.MentionExtractor;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class SpreadingActivationRunnable implements Runnable {

   
   GraphDatabaseService db;
   HDT dbpedia;
   StanfordCoreNLP nlpPipeline;
   File doc;
   Map<String, Mode> modesIndex;
   Map<Integer, String> blackboardOnDisk;
   JedisPool jedisPool;
   String resultsDir ;
   boolean allOnes;
   
   
   public SpreadingActivationRunnable (GraphDatabaseService db , HDT dbpedia, StanfordCoreNLP nlpPipeline , File doc,  Map<String, Mode> modesIndex , 
       JedisPool jedisPool, String resultsDir, boolean allOnes) 
   {
      this.db=db;
      this.dbpedia = dbpedia;
      this.nlpPipeline = nlpPipeline;
      this.doc= doc;
      this.modesIndex = modesIndex;
      this.jedisPool  = jedisPool;
      this.resultsDir = resultsDir;
      this.allOnes = allOnes;
   }
   
   
   private LinkedDocument readLinkedDocFromFile (File f) {
      LinkedDocument doc = null;

      FileInputStream fin = null;
      ObjectInputStream ois = null;

      try {

         fin = new FileInputStream(f);
         ois = new ObjectInputStream(fin);
         doc = (LinkedDocument) ois.readObject();
         doc.extractParagraphs();
         doc.extractSentences(this.nlpPipeline);
         doc.mapMentionsToSentencesAndParagraphs();

      } catch (Exception ex) {
         ex.printStackTrace();
      } 
      return doc;
   }
   
 private  void writeLinkedDocumentToDisk (LinkedDocument doc, File f) throws Exception {
       try{
        FileOutputStream fos = new FileOutputStream(f);
         ObjectOutputStream oos = new ObjectOutputStream(fos);
         oos.writeObject(doc);
         oos.close();
       }catch(Exception ex) {
          ex.printStackTrace();
       }
      
   }
   
   @Override
   public void run() {
      try (Transaction tx = db.beginTx()) {

         SAComplexityModes samodes =  new SAComplexityModes(db);
         KanopyDocumentJsonReader reader = new KanopyDocumentJsonReader();
         KanopyDocument kdoc= reader.readDocument(this.doc.getAbsolutePath());
         kdoc.docPath = "/home/ihulpus/data/"+kdoc.docPath.substring(12);
         String originaldocName = this.doc.getName().replace(".kanopy.json", "");
         System.out.println("TO SOLVE NOW: "+originaldocName);
         String linkedDocPath = "/home/ihulpus/data/LinkedNewsela/LinkedDocumentObjects/"+originaldocName + ".object";
         
         LinkedDocument ldoc;
         File linkedDocSerialized =new File(linkedDocPath);
         if (linkedDocSerialized.exists()) {
            System.out.println("reading linked doc from file");
            ldoc = readLinkedDocFromFile(linkedDocSerialized);
         }
         else {
            MentionExtractor extr = new MentionExtractor(db, dbpedia, kdoc);
            System.out.println("removing outliers");
            extr.removeLikelyWrongLinks();            
            System.out.println("removed");
            FeaturedDocument fdoc = new FeaturedDocument(kdoc, nlpPipeline, db, dbpedia,null, null, null);
            ldoc = new LinkedDocument(fdoc);
            this.writeLinkedDocumentToDisk(ldoc, linkedDocSerialized);
         }

       //  System.out.println("Linked Document :" +ldoc.toString());
         Map<String, Double> activationsAtEncounter = new HashMap<>();
         Map<String, Double> activationAtEOS = new HashMap<>();
         Map<String, Double> activationAtEOP = new HashMap<>();
         Map<String, Double> activationAtEODoc = new HashMap<>();
         try(Jedis jedis = this.jedisPool.getResource()) {
            samodes.computeComplexityWithSpreadingActivationOncePerEntity(ldoc, modesIndex, allOnes, activationsAtEncounter, activationAtEOS, activationAtEOP, activationAtEODoc, jedis);
         }
         catch(Exception ex) {
            ex.printStackTrace();
         }

         File dir = new File("results/spreading_Activation/"+this.resultsDir);
         if (!dir.exists()) dir.mkdir();
         CSVWriter writer = new CSVWriter(new FileWriter(new File(dir, originaldocName+"_sa.csv")));
         String[] docmodeline =  new String[14];
         for (Entry<String, Mode> me: modesIndex.entrySet()) {
            docmodeline[0] = kdoc.docPath;
            docmodeline[1] = String.valueOf(me.getValue().graphDecay);
            docmodeline[2] = String.valueOf(me.getValue().firingThreshold);
            docmodeline[3] = String.valueOf(me.getValue().readingTokenDecay);
            docmodeline[4] = String.valueOf(me.getValue().readingSentenceDecay);
            docmodeline[5] = String.valueOf(me.getValue().readingParagraphDecay);
            docmodeline[6] = String.valueOf(me.getValue().usesExclusivity);
            docmodeline[7] = String.valueOf(me.getValue().usesImportance);
            docmodeline[8] = String.valueOf(activationsAtEncounter.get(me.getKey()));
            docmodeline[9] = String.valueOf(activationAtEOS.get(me.getKey()));
            docmodeline[10]= String.valueOf(activationAtEOP.get(me.getKey()));
            docmodeline[11]= String.valueOf(activationAtEODoc.get(me.getKey()));
            docmodeline[12]= me.getKey();
            docmodeline[13]= String.valueOf(ldoc.getComplexityLevel());
            
            writer.writeNext(docmodeline);
         }

         writer.close();
      }

      catch(Exception ex){
         ex.printStackTrace();
      }
      
   }

}
