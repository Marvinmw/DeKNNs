package KMeans;
import java.io.Serializable;

 	/**
	 * unity class to store KNN neighbor
	 */ 
class PairUser implements Serializable{
	

	private static final long serialVersionUID = -1448872468148422322L;
	public double similarity;
	   public int uid;
	   public PairUser(int uid, double s){
		   this.uid=uid;
		   this.similarity=s;
	   }
   }