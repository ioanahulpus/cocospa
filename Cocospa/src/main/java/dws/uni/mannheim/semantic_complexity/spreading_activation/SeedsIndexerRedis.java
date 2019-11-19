package dws.uni.mannheim.semantic_complexity.spreading_activation;

import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class SeedsIndexerRedis {
   
   public static void main(String[] args) {
      Jedis jedis = new Jedis("localhost", 6379, 60000 );
      Set<String> allKeys  =  jedis.keys("*");
   
      Set<String> allSeeds = new HashSet<>();
      for (String key: allKeys) {
         if (key.contains("->")) {
            int connectorIndex = key.indexOf("->");
            String seed = key.substring(0, connectorIndex);
            allSeeds.add(seed);
           // System.out.println(seed);
         }
         else System.out.println(key);
        
      }
      
      System.out.println(allSeeds.size() + "seeds");
      for(String seed : allSeeds) {
      jedis.sadd("seeds", seed);
     }
      jedis.close();      

   }

}
