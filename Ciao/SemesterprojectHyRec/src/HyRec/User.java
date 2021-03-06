package HyRec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;



public class User {
  

 public HashMap<Integer,Double> profile=new HashMap<Integer,Double>();
   private double length=0.0;

   public HashMap<Integer,PairUser> hashknn=new HashMap<Integer,PairUser>();
   public TreeSet<Integer>  knnid=new TreeSet<Integer>();
   public ArrayList<PairUser> knn=new ArrayList<PairUser>();
   
  
   public HashMap<Integer,PairUser> thashknn=new HashMap<Integer,PairUser>();
   public TreeSet<Integer>  tknnid=new TreeSet<Integer>();
   public ArrayList<PairUser> tknn=new ArrayList<PairUser>();
   public int uid;
   public int cid=-1;
   public User(){}
   
   public User(int uid){
	   this.uid=uid;
   }
   
   
   public double getSimiar(int id){
	  return hashknn.get(id).similarity;
   }
   
   public int getTopKNN(){
	   if(hashknn.isEmpty())
		   return -1;
	   
	   if(KNNOffLine.rnd.nextDouble()>0.5){
	         Collections.sort(knn, new CompareUser());
	         return knn.get(0).uid;
	       }
	   else
	       Collections.shuffle(knn,KNNOffLine.rnd);

	   return knn.get(knn.size()-1).uid;
   }
   
   //add new items to profile
   public void addProfile(int item,double rating){
	   if(profile.containsKey(item)){
		   this.length-=profile.get(item)*profile.get(item);
	   }
	   profile.put(item, rating);
	   this.length+=rating*rating;
   }
   
   //update KNN
   public int updateKNN(ArrayList<PairUser> canlist){
	   int num=Configure.K<canlist.size()?Configure.K:canlist.size();
	   this.hashknn.clear();
	   for(int i=0;i<num;i++){
		   this.hashknn.put(canlist.get(i).uid, canlist.get(i));
	   }
	   
	   TreeSet<Integer> all=new TreeSet<Integer>();
	   all.addAll(this.knnid);
	   all.addAll(this.hashknn.keySet());
	   int changed=this.knnid.size()-(this.knnid.size()+this.hashknn.size()-all.size());
	   
	   this.knnid.clear();
	   this.knnid.addAll(this.hashknn.keySet());
	   this.knn.clear();
	   this.knn.addAll(canlist.subList(0, num));
       return changed;
	   
   }
   

   public double avgRating(){
		Double[] r=profile.values().toArray(new Double[0]);
		double sum=0;
		for(Double d:r){
			sum+=d;
		}
		long l=r.length;
		if(l==0)
			return 0;
		else
			return sum/l*1.0;
	}
 
   
  
   public double getProfilelength(){
	   return Math.sqrt(this.length);
   }
	public static double consionSimilarity(HashMap<Integer,Double> uprofile,HashMap<Integer,Double> vprofile,double ulength, double vlength){
		Iterator<Integer> uitr=uprofile.keySet().iterator();
		double numerator=0;
		
		while(uitr.hasNext()){
			int key=uitr.next();
			if(vprofile.containsKey(key)){
				numerator+=uprofile.get(key)*vprofile.get(key);
			}
		}
		
		double denominator=vlength*ulength;
		if(denominator==0)
		    return 0.0;
		else
			return numerator/denominator;
	}
   
	public int intersectionKNN(){
		TreeSet<Integer> inter=new TreeSet<Integer>();
		inter.addAll(this.knnid);
		inter.addAll(this.tknnid);
		
		return this.knnid.size()+this.tknnid.size()-inter.size();
		
	}

	public void updateTrueKNN( ArrayList<PairUser> canlist) {

		   this.thashknn.clear();
		   this.tknn.clear();
		   this.tknnid.clear();
		   
		   
		   int num=Configure.K<canlist.size()?Configure.K:canlist.size();
		   for(int i=0;i<num;i++){
			   this.thashknn.put(canlist.get(i).uid, canlist.get(i));
		   }
		  
		   this.tknnid.addAll(this.thashknn.keySet());
		   this.tknn.clear();
		   this.tknn.addAll(canlist.subList(0, num));
	     
	}

	public double getTrueKsimilar(int k) {
		// TODO Auto-generated method stub
		  return thashknn.get(k).similarity;
	}

	
	public static double consionSimilarity(HashMap<Integer,Double> uprofile,HashMap<Integer,Double> vprofile){
		Iterator<Integer> uitr=uprofile.keySet().iterator();
		double numerator=0;
		double ulength=0;
		while(uitr.hasNext()){
			int key=uitr.next();
			if(vprofile.containsKey(key)){
				numerator+=uprofile.get(key)*vprofile.get(key);
			}
			ulength+=uprofile.get(key)*uprofile.get(key);
			
		}
		
		Iterator<Integer> vitr=vprofile.keySet().iterator();
		
		double vlength=0;
		while(vitr.hasNext()){
			int key=vitr.next();
			vlength+=vprofile.get(key)*vprofile.get(key);
			
		}
		
		double denominator=Math.sqrt(vlength)*Math.sqrt(ulength);
		if(denominator==0)
		    return 0.0;
		else
			return numerator/denominator;
	}
   
}

