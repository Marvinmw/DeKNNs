package KMeans;
import java.util.TreeSet;


public class Item {
	public Item(){
	}
	public Item(int iid){
		this.iid=iid;
	}
 public int iid=0;
 public TreeSet<Integer> users=new TreeSet<Integer>();
 public double allsum=0;
 public long allsize=0;
 public double getAverage(){
	 if(allsize==0)
		 return 0;
	 return (1.0*allsum)/(1.0*allsize);
 }

}
