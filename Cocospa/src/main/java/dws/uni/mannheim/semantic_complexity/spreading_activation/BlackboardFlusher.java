package dws.uni.mannheim.semantic_complexity.spreading_activation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Map.Entry;

public class BlackboardFlusher implements Runnable{
   
   
   Map<String, Map<Long, Map<Mode, Double>>> blackboard;
   Map<String, String> uriToTempFile;
   int max_size;
   
   public BlackboardFlusher (int size, Map<String, Map<Long, Map<Mode, Double>>> blackboard,  Map<String, String> uriToTempFile) {
      this.blackboard = blackboard;  
      this.uriToTempFile  = uriToTempFile;    
      this.max_size = size;
   }
   
   
   private  void writeTidalActivationBlackboardToDisk () throws Exception {
      
      for (Entry<String, Map<Long, Map<Mode, Double>>> e: this.blackboard.entrySet()) {         
         FileOutputStream fos = new FileOutputStream(new File ("results/spreading_Activation/temp", e.getKey().hashCode()+".tmp"));
         ObjectOutputStream oos = new ObjectOutputStream(fos);
         oos.writeObject(e.getValue());
         oos.close();
         uriToTempFile.put(e.getKey(), "results/spreading_Activation/temp/" + e.getKey().hashCode()+".tmp");
      }
      
   }

   @Override
   public void run() {
     
      while (true) {
         if (blackboard.size() > max_size) 
            try {
               //not safe! do not use, must make sure that blackboard was not updated in between by other threads.
               this.writeTidalActivationBlackboardToDisk();
               this.blackboard.clear();
            } catch (Exception e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }            
      }
      
   }

}
