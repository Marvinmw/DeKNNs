package item;
import java.io.Serializable;

   class PairUser  implements Serializable{
	   /**
	 * 
	 */
	private static final long serialVersionUID = -1448872468148422322L;
	public double similarity;
	   public int uid;
	   public PairUser(int uid, double s){
		   this.uid=uid;
		   this.similarity=s;
	   }
	   
	   
   }