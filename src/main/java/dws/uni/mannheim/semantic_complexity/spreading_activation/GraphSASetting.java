package dws.uni.mannheim.semantic_complexity.spreading_activation;

public class GraphSASetting {
   
   double graphDecay; 
   double firingThreshold; 
   boolean usesImportance; 
   boolean usesExclusivity;
   
   public GraphSASetting (double graphDecay, double firingthreshold, boolean usesImportance, boolean usesExclusivity) {
      this.graphDecay = graphDecay;
      this.firingThreshold = firingthreshold;
      this.usesImportance=usesImportance;
      this.usesExclusivity = usesExclusivity;
   }
   
   
   public String getSettingName() {
      return "GRAPHDECAY"+graphDecay+"FIRE"+firingThreshold+"I"+usesImportance+"E"+usesExclusivity;
   }

}
