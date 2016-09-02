package item;
import java.util.ArrayList;
import java.util.TreeSet;


public class Item {
	public Item(){
	}
	public Item(int iid){
		this.iid=iid;
	}
 public int iid=0;
 public ArrayList<Integer> users=new ArrayList<Integer>();
 public double allsum=0;
 public long allsize=0;
 public double getAverage(){
	 if(allsize==0)
		 return 0;
	 return (1.0*allsum)/(1.0*allsize);
 }

}
