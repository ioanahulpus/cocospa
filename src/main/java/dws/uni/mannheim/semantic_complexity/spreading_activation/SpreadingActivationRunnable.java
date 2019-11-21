package dws.uni.mannheim.semantic_complexity.spreading_activation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.rdfhdt.hdt.hdt.HDT;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import au.com.bytecode.opencsv.CSVWriter;
import dws.uni.mannheim.relatedness.EntityLinker;
import dws.uni.mannheim.semantic_complexity.FeaturedDocument;
import dws.uni.mannheim.semantic_complexity.KanopyDocument;
import dws.uni.mannheim.semantic_complexity.KanopyDocumentJsonReader;
import dws.uni.mannheim.semantic_complexity.LinkedDocument;
import dws.uni.mannheim.semantic_complexity.MentionExtractor;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class SpreadingActivationRunnable implements Runnable
{

    GraphDatabaseService db;
    HDT dbpedia;
    StanfordCoreNLP nlpPipeline;
    File doc;
    Map<String, Mode> modesIndex;
    Map<Integer, String> blackboardOnDisk;
    JedisPool jedisPool;
    String resultsDir;
    boolean allOnes;
    String docText;
    String docLinks;
    LinkedDocument ldoc;
    
    public SpreadingActivationRunnable(GraphDatabaseService db, HDT dbpedia,
            StanfordCoreNLP nlpPipeline, String docText,
            Map<String, Mode> modesIndex, JedisPool jedisPool,
            String resultsDir, boolean allOnes) throws FileNotFoundException
    {
        this.db = db;
        this.dbpedia = dbpedia;
        this.nlpPipeline = nlpPipeline;
        this.docText = docText;
        //this.docLinks = docLinks;
        this.modesIndex = modesIndex;
        this.jedisPool = jedisPool;
        this.resultsDir = resultsDir;
        this.allOnes = allOnes;

    }

    private LinkedDocument readLinkedDocFromFile(File f)
    {
        LinkedDocument doc = null;

        FileInputStream fin = null;
        ObjectInputStream ois = null;

        try
        {

            fin = new FileInputStream(f);
            ois = new ObjectInputStream(fin);
            doc = (LinkedDocument) ois.readObject();
            doc.extractParagraphs();
            doc.extractSentences(this.nlpPipeline);
            doc.mapMentionsToSentencesAndParagraphs();

        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return doc;
    }

    private void writeLinkedDocumentToDisk(LinkedDocument doc, File f)
            throws Exception
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(doc);
            oos.close();
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public void run()
    {
        try (Transaction tx = db.beginTx())
        {

            SAComplexityModes samodes = new SAComplexityModes(db);
            
            /*
            FileInputStream in = new FileInputStream(new File("./micro_b003.txt.kanopy.json"));
            StringBuilder sb = new StringBuilder(512);
            try
            {
                Reader r = new InputStreamReader(in, "UTF-8");
                int c = 0;
                while ((c = r.read()) != -1)
                {
                    sb.append((char) c);
                }
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            String docLinks = sb.toString();
            Gson gson = new GsonBuilder().create();
            KanopyDocument kdoc = gson.fromJson(docLinks, KanopyDocument.class);
            */
            KanopyDocument kdoc = EntityLinker.Link(docText, "", 0.35);

            MentionExtractor extr = new MentionExtractor(db, dbpedia, kdoc);
            
            System.out.println("removing outliers");
            extr.removeLikelyWrongLinks();
            System.out.println("removed");
            FeaturedDocument fdoc = new FeaturedDocument(kdoc, docText, nlpPipeline, db,
                    dbpedia, null, null, null);
            this.ldoc = new LinkedDocument(fdoc, docText);
            
            Map<String, Double> activationsAtEncounter = new HashMap<>();
            Map<String, Double> activationAtEOS = new HashMap<>();
            Map<String, Double> activationAtEOP = new HashMap<>();
            Map<String, Double> activationAtEODoc = new HashMap<>();
            try (Jedis jedis = this.jedisPool.getResource())
            {
                samodes.computeComplexityWithSpreadingActivationOncePerEntity(
                        this.ldoc, modesIndex, allOnes, activationsAtEncounter,
                        activationAtEOS, activationAtEOP, activationAtEODoc,
                        jedis);
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }

            File dir = new File(
                    "/media/er/Data/simplification/java_proj/src/results/"
                            + this.resultsDir);
            if (!dir.exists())
                dir.mkdir();
            CSVWriter writer = new CSVWriter(new FileWriter(new File(dir,
                    "result_out" + "_sa.csv")));
            String[] docmodeline = new String[14];
            for (Entry<String, Mode> me : modesIndex.entrySet())
            {
                docmodeline[0] = ldoc.getPath();
                docmodeline[1] = String.valueOf(me.getValue().graphDecay);
                docmodeline[2] = String.valueOf(me.getValue().firingThreshold);
                docmodeline[3] = String
                        .valueOf(me.getValue().readingTokenDecay);
                docmodeline[4] = String
                        .valueOf(me.getValue().readingSentenceDecay);
                docmodeline[5] = String
                        .valueOf(me.getValue().readingParagraphDecay);
                docmodeline[6] = String.valueOf(me.getValue().usesExclusivity);
                docmodeline[7] = String.valueOf(me.getValue().usesImportance);
                docmodeline[8] = String.valueOf(activationsAtEncounter.get(me
                        .getKey()));
                docmodeline[9] = String
                        .valueOf(activationAtEOS.get(me.getKey()));
                docmodeline[10] = String.valueOf(activationAtEOP.get(me
                        .getKey()));
                docmodeline[11] = String.valueOf(activationAtEODoc.get(me
                        .getKey()));
                docmodeline[12] = me.getKey();
                docmodeline[13] = String.valueOf(ldoc.getComplexityLevel());
                System.out.println("Activations: " + docmodeline[8] + " " + docmodeline[9] + " " + docmodeline[10] + " " + docmodeline[11]);
                writer.writeNext(docmodeline);
            }

            writer.close();
        }

        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

}
