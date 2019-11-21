package dws.uni.mannheim.semantic_complexity.spreading_activation;

import java.io.Serializable;

public class Mode implements Serializable{
   /**
    * 
    */
   private static final long serialVersionUID = 1L;
   double graphDecay; 
   double firingThreshold; 
   boolean usesImportance; 
   boolean usesExclusivity;
   double readingTokenDecay;
   double readingSentenceDecay;
   double readingParagraphDecay;
   
   GraphSASetting graphSetting;
   DecaySetting decaySetting;
   
  
   Mode(GraphSASetting graphSettings, DecaySetting decay) {
      this.graphDecay = graphSettings.graphDecay;
      this.firingThreshold = graphSettings.firingThreshold;
      this.usesExclusivity = graphSettings.usesExclusivity;
      this.usesImportance = graphSettings.usesImportance;
      this.graphSetting= graphSettings;
      this.decaySetting = decay;
      
   }
 
   
   public Mode(double graphDecay, double firingThreshold, double readingTokenDecay, double readingSentDecay, double readingParDecay, boolean usesImportance, boolean usesExclusivity){
      this.graphDecay = graphDecay;
      this.firingThreshold = firingThreshold;
      this.readingTokenDecay = readingTokenDecay;
      this.usesImportance = usesImportance;
      this.usesExclusivity = usesExclusivity;
      this.readingSentenceDecay = readingSentDecay;
      this.readingParagraphDecay = readingParDecay;
      this.graphSetting = new GraphSASetting(this.graphDecay, this.firingThreshold, this.usesImportance, this.usesExclusivity);
      this.decaySetting = new DecaySetting(this.readingTokenDecay, this.readingSentenceDecay, this.readingParagraphDecay);
   }
   
   
   public Mode(double graphDecay, double firingThreshold, DecaySetting decay, boolean usesImportance, boolean usesExclusivity){
      this.graphDecay = graphDecay;
      this.firingThreshold = firingThreshold;
      this.readingTokenDecay = decay.tokenDecay;
      this.readingSentenceDecay= decay.sentDecay;
      this.readingParagraphDecay = decay.parDecay;
      this.usesExclusivity = usesExclusivity;
      this.usesImportance = usesImportance;
      this.decaySetting = decay;
      this.graphSetting = new GraphSASetting(this.graphDecay, this.firingThreshold, this.usesImportance, this.usesExclusivity);
    
   }
   
   public double getFiringThreshold() {
      return firingThreshold;
   }
   public double getGraphDecay() {
      return graphDecay;
   }
   
  public double getReadingParagraphDecay() {
   return readingParagraphDecay;
}
  
  public double getReadingSentenceDecay() {
   return readingSentenceDecay;
}
  
  public double getReadingTokenDecay() {
   return readingTokenDecay;
}
   public boolean usesImportance() {
      return usesImportance;
   }
   
   public boolean usesExclusivity() {
      return usesExclusivity;
   }
   
   public String getModeName() {
      return "GRAPHDECAY"+graphDecay+"FIRE"+firingThreshold+"READINGDECAYTOKEN"+readingTokenDecay+"SENT"+readingSentenceDecay+"PAR"+readingParagraphDecay+"I"+usesImportance+"E"+usesExclusivity;
   }
}


