package dws.uni.mannheim.semantic_complexity.spreading_activation;

public class DecaySetting{



   double tokenDecay; double sentDecay; double parDecay;

   public DecaySetting (double tDec, double sDec, double pDec) {
      this.tokenDecay = tDec;
      this.sentDecay = sDec;
      this.parDecay = pDec;
   }
}