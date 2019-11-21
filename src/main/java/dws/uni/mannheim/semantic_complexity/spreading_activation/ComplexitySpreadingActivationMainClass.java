package dws.uni.mannheim.semantic_complexity.spreading_activation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;

import au.com.bytecode.opencsv.CSVWriter;
import dws.uni.mannheim.relatedness.EntityLinker;
import dws.uni.mannheim.relatedness.SettingsReader;
import dws.uni.mannheim.semantic_complexity.FeaturedDocument;
import dws.uni.mannheim.semantic_complexity.KanopyDocument;
import dws.uni.mannheim.semantic_complexity.KanopyDocumentJsonReader;
import dws.uni.mannheim.semantic_complexity.LinkedDocument;
import dws.uni.mannheim.semantic_complexity.MentionExtractor;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import scala.Array;


public class ComplexitySpreadingActivationMainClass
{

    volatile static Map<String, Map<Long, Map<Mode, Double>>> tidalActivationsBlackboard = new HashMap<>();

    // uri, "results/spreading_Activation/temp/" + uri.hashCode()+".tmp"

    private static Map<Integer, String> readBlackboardOnDiskMap()
    {
        Map<Integer, String> result = new HashMap<Integer, String>();
        File dir = new File("results/spreading_Activation/temp/");
        for (String f : dir.list())
        {
            result.put(Integer.parseInt(f.substring(0, f.length() - 4)),
                    "results/spreading_Activation/temp/" + f);
        }
        return result;
    }

    public static void main(String[] args) throws Exception
    {

        System.out.println(0.0 / 0.0);

        int arg_idx = 0;
        String neo = args[arg_idx++];
        String hdtWikipedia = args[arg_idx++];
        String redis = args[arg_idx++];
        int redis_port = Integer.valueOf(args[arg_idx++]);
        String docsFile = args[arg_idx++];
        String settingsFilePath = args[arg_idx++];
        String resultsDir = args[arg_idx++];
        boolean allOnes = Boolean.valueOf(args[arg_idx++]);

        Set<Mode> modes = new HashSet<>();
        
        //EntityLinker.Link("", "");

        SettingsReader settings = new SettingsReader(settingsFilePath);

        modes.add(new Mode(settings.GRAPH_DECAY, settings.FIRING_THRESH,
                new DecaySetting(settings.TOKEN_DECAY, settings.SENT_DECAY,
                        settings.PAR_DECAY), settings.USE_IMPORTANCE,
                settings.USE_EXCLUSIVITY));
        Map<String, Mode> modesIndex = new HashMap<>();
        for (Mode m : modes)
        {
            System.out.println(m.getModeName());
            modesIndex.put(m.getModeName(), m);
        }
        System.out.println("Modes set size: " + modes.size());
        System.out.println("Modes index size: " + modesIndex.size());

        GraphDatabaseService db = new GraphDatabaseFactory()
                .newEmbeddedDatabase(new File(neo));

        try
        {
            HDT dbpedia = HDTManager
                    .mapIndexedHDT(hdtWikipedia, null);
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize, ssplit");
            StanfordCoreNLP nlpPipeline = new StanfordCoreNLP(props);

            //read equivalent text file
            String originaldocName = docsFile.replace(".kanopy.json", "");
            FileInputStream in = new FileInputStream(new File("./micro_b003.txt"));
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
            String docText = sb.toString();

            
            try
            {
                ExecutorService pool = Executors
                        .newFixedThreadPool(settings.NR_THREADS);
                JedisPool jedisPool = new JedisPool(new JedisPoolConfig(),
                        redis, redis_port, 3600000);

                    try
                    {

                        
                        SpreadingActivationRunnable thread = new SpreadingActivationRunnable(
                                db, dbpedia, nlpPipeline, docText,  modesIndex,
                                jedisPool, resultsDir, allOnes);
                        pool.execute(thread);
                    } catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }

                
                pool.shutdown();
                pool.awaitTermination(100, TimeUnit.DAYS);
                jedisPool.close();
                System.exit(0);
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }

        } catch (IOException ioex)
        {
            ioex.printStackTrace();
        }
    }

}
