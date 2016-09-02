package HyRec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

public class Email {

    //email's profile
	public double[] profile = new double[57];
	public int spam=-1;
	public int predicate=-1;
	//store appropriate KNN
	public HashMap<Integer, PairEmail> hashknn = new HashMap<Integer, PairEmail>();
	public TreeSet<Integer> knnid = new TreeSet<Integer>();
	public ArrayList<PairEmail> knn = new ArrayList<PairEmail>();
	//store true KNN
	public HashMap<Integer, PairEmail> thashknn = new HashMap<Integer, PairEmail>();
	public TreeSet<Integer> tknnid = new TreeSet<Integer>();
	public ArrayList<PairEmail> tknn = new ArrayList<PairEmail>();
	public int uid;
	public int cid = -1;
	public int paths = 1;

	public Email() {
	}

	public Email(int uid,double[] features,int spam) {
		this.uid = uid;
		this.profile=features;
		this.spam=spam;
	}

	public double avgRating(){
		double[] r=profile;
		double sum=0;
		for(double d:r){
			sum+=d;
		}
		long l=r.length;
		if(l==0)
			return 0;
		else
			return sum/l*1.0;
	}
	
	public double getSimiar(int id) {
		return hashknn.get(id).similarity;
	}

	public int getTopKNN() {
		
			Collections.sort(knn, new CompareEmail());
			return knn.get(0).uid;
	
	}

	
	public TreeSet<Integer> getTopNKNN(int n){
		  Collections.sort(this.knn, new CompareEmail());
		  TreeSet<Integer> tr=new TreeSet<Integer>();
		  for(int i=0;i<n;i++)
			  tr.add(this.knn.get(i).uid);
		  return tr;
	}
	// update KNN
	public int updateKNN(ArrayList<PairEmail> canlist) {
		int num = Configure.K < canlist.size() ? Configure.K : canlist.size();
		this.hashknn.clear();
		for (int i = 0; i < num; i++) {
			this.hashknn.put(canlist.get(i).uid, canlist.get(i));
		}

		TreeSet<Integer> all = new TreeSet<Integer>();
		all.addAll(this.knnid);
		all.addAll(this.hashknn.keySet());
		int changed = this.knnid.size()
				- (this.knnid.size() + this.hashknn.size() - all.size());

		this.knnid.clear();
		this.knnid.addAll(this.hashknn.keySet());
		this.knn.clear();
		this.knn.addAll(canlist.subList(0, num));
		return changed;

	}

	/**
	 * compute simility function Pearson correlation
	 * */
	// public static double pearsonsimility(HashMap<Integer,Double>
	// uprofile,HashMap<Integer,Double> vprofile){
	//
	// double uavg=0.0;
	// double vavg=0.0;
	// double part1=0.0;
	// double part2=0.0;
	// double part3=0.0;
	// int intersaction=0;
	//
	// ArrayList<Integer> comitems=new ArrayList<Integer>();
	//
	// Set<Integer> uks=uprofile.keySet();
	// Iterator<Integer> it=uks.iterator();
	// while(it.hasNext()){
	// int uiid=it.next();
	// if(vprofile.containsKey(uiid)){
	// uavg+=uprofile.get(uiid);
	// vavg+=vprofile.get(uiid);
	// comitems.add(uiid);
	// intersaction++;
	// }
	// }
	//
	// uavg=uavg/(comitems.size()*1.0);
	// vavg=vavg/(comitems.size()*1.0);
	// //System.out.println("Debug");
	// for(int i=0;i<comitems.size();i++){
	// int iid=comitems.get(i);
	// double ur=uprofile.get(iid);
	// double vr=vprofile.get(iid);
	//
	// part1=part1+(ur-uavg)*(vr-vavg)*1.0;
	//
	// part2+=(ur-uavg)*(ur-uavg)*1.0;
	//
	// part3+=(vr-vavg)*(vr-vavg)*1.0;
	//
	// }
	//
	// if(Math.sqrt(part2*part3*1.0)==0)
	// return 0.0;
	//
	// //if(intersaction==0 || part1 == 0 || part2*part3==0)
	// // System.out.println("intersaction === "+intersaction);
	// //System.out.println(totalsize+"======="+intersaction);(intersaction<20?
	// intersaction:20)/20*
	// return (intersaction<20?
	// intersaction:20)/20.0*part1/Math.sqrt(part2*part3);
	//
	// }
	//
	//
	
	public int intersectionKNN() {
		TreeSet<Integer> inter = new TreeSet<Integer>();
		inter.addAll(this.knnid);
		inter.addAll(this.tknnid);

		return this.knnid.size() + this.tknnid.size() - inter.size();

	}

	public void updateTrueKNN(ArrayList<PairEmail> canlist) {

		this.thashknn.clear();
		this.tknn.clear();
		this.tknnid.clear();

		int num = Configure.K < canlist.size() ? Configure.K : canlist.size();
		for (int i = 0; i < num; i++) {
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

	

	public static double consionSimilarity(double[] uprofile,
			double[] vprofile) {
		double ul=0;
		double vl=0;
//		for(int i=0;i<uprofile.length;i++){
//			ul+=uprofile[i]*uprofile[i];
//			vl+=vprofile[i]*vprofile[i];
//		}
//		
//		for(int i=0;i<uprofile.length;i++){
//			uprofile[i]/=Math.sqrt(ul);
//			vprofile[i]/=Math.sqrt(vl);
//		}
		
		double sum=0;
		for(int i=0;i<uprofile.length;i++){
			ul+=uprofile[i]*uprofile[i];
			vl+=vprofile[i]*vprofile[i];
			sum+=uprofile[i]*vprofile[i];
		}
		
		
		if(ul*vl==0)
			return 0;
		else{
			double r=sum/(1.0*Math.sqrt((ul))*Math.sqrt((vl)));
			return r;
			}
	}
	  

}
