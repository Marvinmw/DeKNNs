import java.io.Serializable;

   class PairUser   implements Serializable{
	   public double similarity;
	   public int uid;
	   public PairUser(int uid, double s){
		   this.uid=uid;
		   this.similarity=s;
	   }
   }