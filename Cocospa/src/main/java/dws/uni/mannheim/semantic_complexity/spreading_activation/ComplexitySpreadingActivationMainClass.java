package dws.uni.mannheim.semantic_complexity.spreading_activation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
import dws.uni.mannheim.semantic_complexity.FeaturedDocument;
import dws.uni.mannheim.semantic_complexity.KanopyDocument;
import dws.uni.mannheim.semantic_complexity.KanopyDocumentJsonReader;
import dws.uni.mannheim.semantic_complexity.MentionExtractor;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import scala.Array;

public class ComplexitySpreadingActivationMainClass {

  

   //\alpha
 //  final static double GRAPH_DECAY_01  = 0.1;
   // final static double ACTIVATION_GRAPH_DECAY_015 = 0.15;
  // final static double GRAPH_DECAY_02  = 0.2;
     final static double GRAPH_DECAY_025 = 0.25;
 //  final static double GRAPH_DECAY_03  = 0.3;
 //  final static double GRAPH_DECAY_04  = 0.4;
   final static double GRAPH_DECAY_05  = 0.5;
   final static double GRAPH_DECAY_075  = 0.75;
  // final static double GRAPH_DECAY_09  = 0.9;
   //\betha
//   final static double FIRING_THRESHOLD_00005 = 0.0005;
//   final static double FIRING_THRESHOLD_0001  = 0.001;
//   final static double FIRING_THRESHOLD_00015  = 0.0015;
   final static double FIRING_THRESHOLD_00025  = 0.0025;
 //  final static double FIRING_THRESHOLD_0003  = 0.003;
   final static double FIRING_THRESHOLD_0005  = 0.005;
   final static double FIRING_THRESHOLD_00075  = 0.0075;
    final static double FIRING_THRESHOLD_001   = 0.01;
   //\gamma
   final static double TOKEN_DECAY_0995 = 0.995;
   final static double TOKEN_DECAY_085  = 0.85;
   final static double TOKEN_DECAY_075   = 0.75;

   final static double SENT_DECAY_09 = 0.9;
   final static double SENT_DECAY_07  = 0.7;
   final static double SENT_DECAY_05   = 0.5;

   final static double PAR_DECAY_08 = 0.8;
   final static double PAR_DECAY_05  = 0.5;
   final static double PAR_DECAY_025   = 0.25;

   
   
   volatile static Map<String, Map<Long, Map<Mode, Double>>> tidalActivationsBlackboard = new HashMap<>();
   
   
   
   //uri, "results/spreading_Activation/temp/" + uri.hashCode()+".tmp"

   private static Map<Integer, String> readBlackboardOnDiskMap(){
	   Map<Integer, String> result = new HashMap<Integer, String>();
	   File dir = new File("results/spreading_Activation/temp/");
	   for (String f: dir.list()){
		   result.put(Integer.parseInt(f.substring(0, f.length() -4)), "results/spreading_Activation/temp/"+f);
	   }
	   return result;
   }
   
   public static void main(String[] args) {
      
      
    System.out.println(0.0/0.0);
      
      String neo = args[0];
      String redis = args[1];
      int redis_port = Integer.valueOf(args[2]);
      String docsFile = args[3];
      int NR_THREADS = Integer.valueOf(args[4]);
      String resultsDir = args[5];
      boolean allOnes = Boolean.valueOf(args[6]);

      Set<Mode> modes = new HashSet<>();

      DecaySetting NO_DECAY =  new DecaySetting(1.0, 1.0, 1.0);
      DecaySetting NO_SENT_REMEMBER = new DecaySetting(1.0, 0.0, 0.0);
      DecaySetting NO_PAR_REMEMBER = new DecaySetting(1.0, 1.0, 0.0);
      

/*      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));    
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, NO_DECAY, false, true));    
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, NO_DECAY, false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, false, true));    
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, false, true));    
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, false, false));

*/
 /*     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, false, false)); 
     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, false, false)); 
      
   
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, false, false));       
      
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, false, false)); 
      
      
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, false, false)); 
      
      
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, false, false)); 

      
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_01, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, false, false)); 
      
      
   */   
      
      
 /*     
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));    
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false));

      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, NO_DECAY, false, true));    
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, NO_DECAY, false, false));

      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, false, true));    
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, false, false));

      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, false, true));    
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_03, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, false, false));

*/
      
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, false, false)); 
     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, false, false)); 
      
   
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, false, false));       
      
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, false, false)); 
      
      
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, false, false)); 
      
      
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, false, false)); 

      
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_025, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, false, false)); 

/////////////////////////
      
 /*     modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));    
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, NO_DECAY, false, true));    
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, NO_DECAY, false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, false, true));    
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, false, true));    
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, false, false));

*/
      
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, false, false)); 
     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, false, false)); 
      
   
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, false, false));       
      
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, false, false)); 
      
      
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, false, false)); 
      
      
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, false, false)); 

      
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_05, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, false, false)); 

///////////////////
      
  /*    
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));    
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false));

      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, NO_DECAY, false, true));    
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, NO_DECAY, false, false));

      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, false, true));    
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, false, false));

      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, false, true));    
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_07, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, false, false));

*/
      
/*      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, false, false)); 
     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, false, false)); 
 */     
   
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, false, false));       
      
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, false, false)); 
      
      
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, false, false)); 
      
      
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, false, false)); 

      
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_075, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, false, false)); 


////////////
      
 /*     
      
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));    
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, NO_DECAY, false, true));    
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, NO_DECAY, false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, false, true));    
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, NO_SENT_REMEMBER, false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, false, true));    
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0001, NO_PAR_REMEMBER, false, false));
*/

      
 /*     modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, NO_SENT_REMEMBER, false, false)); 
     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00025, NO_PAR_REMEMBER, false, false)); 
      
   
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, NO_SENT_REMEMBER, false, false));       
      
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_0005, NO_PAR_REMEMBER, false, false)); 
      
      
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, NO_SENT_REMEMBER, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_00075, NO_PAR_REMEMBER, false, false)); 
      
      
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_0995, SENT_DECAY_09, PAR_DECAY_08), false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_085, SENT_DECAY_07, PAR_DECAY_05), false, false));

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, new DecaySetting(TOKEN_DECAY_075, SENT_DECAY_05, PAR_DECAY_025), false, false)); 

      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, NO_DECAY, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, NO_DECAY, false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, NO_DECAY, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, NO_DECAY, false, false)); 
      
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, NO_SENT_REMEMBER, false, false)); 

      
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, true, true));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, false, true));     
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, true, false));
      modes.add(new Mode(GRAPH_DECAY_09, FIRING_THRESHOLD_001, NO_PAR_REMEMBER, false, false)); 
  */    
      
      Map<String, Mode> modesIndex = new HashMap<>();
      for (Mode m: modes) {
         System.out.println(m.getModeName());
         modesIndex.put(m.getModeName(), m);
      }
      System.out.println("Modes set size: " + modes.size());
      System.out.println("Modes index size: " + modesIndex.size());




      // A05_B0005_C0995_W,  //too expensive
      // A05_B0005_C0995_NW,
      // A05_B0005_C0998_W,
      // A05_B0005_C0998_NW,
      // A05_B0005_C1_W ,
      // A05_B0005_C1_NW,

      //     GraphDatabaseService db  = new GraphDatabaseFactory().newEmbeddedDatabase(new File("D:\\kanopydata\\graph_noduplicates_costs_noliterals_nostopUris.db"));

  //    GraphDatabaseService db  = new GraphDatabaseFactory().newEmbeddedDatabase(new File("/home/ihulpus/data/KB/graph_noduplicates_costs_noliterals_nostopUris_new_copy.db"));

      GraphDatabaseService db  = new GraphDatabaseFactory().newEmbeddedDatabase(new File(neo));

       
    
         
        
         try {
            //    HDT dbpedia = HDTManager.mapIndexedHDT("D:\\kanopydata\\DBpedia2014Selected.hdt", null);
            HDT dbpedia = HDTManager.mapIndexedHDT("/home/ihulpus/data/KB/DBpedia2014Selected.hdt", null);
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize, ssplit");
            StanfordCoreNLP nlpPipeline = new StanfordCoreNLP(props);

            Set<String> docsForNonGraphFeatures = new HashSet<String>();
            try{
               File file = new File(docsFile);
               FileReader fileReader = new FileReader(file);
               BufferedReader bufferedReader = new BufferedReader(fileReader);
               String line;
               while ((line = bufferedReader.readLine()) != null) {
                  docsForNonGraphFeatures.add(line);
               }
               fileReader.close();
            }catch(Exception ex){
               ex.printStackTrace();
            }

            List<String> docsToProcess = new ArrayList<String>(docsForNonGraphFeatures);
            Collections.shuffle(docsToProcess);

         
               
      
              
               
             //Map<Integer, String> blackBoardOnDisk = readBlackboardOnDiskMap();
               
               List<File> filesToProcess = new ArrayList<File>();
               for (String filenamestart:docsToProcess){
                  File directoryNewsellaLinked = new File ("/home/ihulpus/data/Kanopy/Output/english/");
                  if (filenamestart.startsWith("a"))
                     directoryNewsellaLinked = new File(directoryNewsellaLinked, "ad/a");
                  else if (filenamestart.startsWith("b"))
                     directoryNewsellaLinked = new File(directoryNewsellaLinked, "ad/b");
                  else if (filenamestart.startsWith("d"))
                     directoryNewsellaLinked = new File(directoryNewsellaLinked, "ad/d");
                  else if (filenamestart.startsWith("c"))
                     directoryNewsellaLinked = new File(directoryNewsellaLinked, "ad/c");
                  else 
                     directoryNewsellaLinked = new File(directoryNewsellaLinked, "09");

                  System.out.println("filenamestart: "+ filenamestart);
                  System.out.println("linked directory: " + directoryNewsellaLinked.getAbsolutePath());

                 
                  List<File> linkedFiles = Arrays.asList(directoryNewsellaLinked.listFiles());
                  Collections.shuffle(linkedFiles);
                  for (File f: linkedFiles ) {  
                     if (f.getName().startsWith(filenamestart)) {
				filesToProcess.add(f);
				}
			}
		}

		Collections.shuffle(filesToProcess);
		
   			
            		try{
               		ExecutorService pool = Executors.newFixedThreadPool(NR_THREADS);
               		JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), redis, redis_port, 3600000 );
               		
               		
               		
               		for (File f: filesToProcess) {	

                       
                        try {
                           SpreadingActivationRunnable thread = new SpreadingActivationRunnable(db, dbpedia, nlpPipeline, f, modesIndex, jedisPool, resultsDir, allOnes);
                           pool.execute(thread);
                        } 
                        catch(Exception ex){
                           ex.printStackTrace();
                        }


                   }
               pool.shutdown();
              	pool.awaitTermination(100, TimeUnit.DAYS);
               jedisPool.close();
               System.exit(0);
            }
            catch(Exception ex) {
               ex.printStackTrace();
            }
       
      }
         catch(IOException ioex) {
            ioex.printStackTrace();
         }
   }


 
   }
