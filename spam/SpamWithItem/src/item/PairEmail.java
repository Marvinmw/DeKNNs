package item;
import java.io.Serializable;

 	/**
	 * unity class to store KNN neighbor
	 */ 
class PairEmail implements Serializable{
	

	private static final long serialVersionUID = -1448872468148422322L;
	public double similarity;
	   public int uid;
	   public PairEmail(int uid, double s){
		   this.uid=uid;
		   this.similarity=s;
	   }
   }