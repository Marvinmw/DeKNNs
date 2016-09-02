package item;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;

public class KNNOffLine {

	// store the global users data
	private HashMap<Integer, User> umap = new HashMap<Integer, User>();
	public static Random rnd = new Random(100000);
	public HashMap<Integer, Item> itemlist = new HashMap<Integer, Item>();

	public ArrayList<Record> readFile(File filepath) throws FileNotFoundException{
	Scanner sc = new Scanner(filepath);
	ArrayList<Record> tr = new ArrayList<Record>();
	while (sc.hasNext()) {
		String line = sc.nextLine();
		line=line.trim();
		String[] info = line.split(Configure.splits);
		
		String suid = info[0];
		BigDecimal db1 = new BigDecimal(suid);
		String uid = db1.toPlainString();
		
		String smid = info[1];
		BigDecimal db2 = new BigDecimal(smid);
		String mid = db2.toPlainString();
		
		String sscore = info[3];
		BigDecimal db3 = new BigDecimal(sscore);
		String score = db3.toPlainString();
		
		String stime = info[5];
		BigDecimal db4 = new BigDecimal(stime);
		String time = db4.toPlainString();
		
		Record record = new Record(Double.valueOf(uid),
				Double.valueOf(mid), Double.valueOf(score),
				Double.valueOf(time));
		tr.add(record);
	}
	sc.close();
	return tr;
	}

	// random the users from the whole data
	public ArrayList<User> randomUser(int num) {
		Set<Integer> keys = umap.keySet();
		ArrayList<Integer> list = new ArrayList<Integer>(keys);
		Collections.shuffle(list, rnd);
		ArrayList<User> ulist = new ArrayList<User>();

		int snum = num < list.size() ? num : list.size();
		for (int i = 0; i < snum; i++) {
			ulist.add(umap.get(list.get(i)));
		}
		return ulist;
	}
  //choose K candidates from items' profile
	public ArrayList<Integer> getCandidateItem(ArrayList<Integer> list) {
		int size = list.size();
		
		ArrayList<Integer> rlist=new ArrayList<Integer>();
		for(int i=0;i<Configure.K;i++){
			int index = KNNOffLine.rnd.nextInt(size);
			ArrayList<Integer> clist;
			clist=this.itemlist.get(list.get(index)).users;
			
			if(clist.size()==0)
				continue;
			int kid=clist.get(KNNOffLine.rnd.nextInt(clist.size()));
			rlist.add(kid);
		}
		return rlist;

	}

	public void updateItemprofifle(Record record) {
		int mid = record.mid;
		if (!this.itemlist.containsKey(mid)) {
			this.itemlist.put(mid, new Item(mid));
		}
		Item item = this.itemlist.get(mid);
		User u=this.umap.get(record.uid);
		if(u.profile.containsKey(mid))
			return;
		item.users.add(record.uid);
		item.allsum += record.score;
		item.allsize += 1;
	}


	// use few parts of data to initialize the parameters
	public void initialize(ArrayList<Record> inirecords) {
		int times = inirecords.size();
		for (int i = 0; i < times; i++) {
			Record record = inirecords.get(i);
			
			int uid = record.uid;
			if (umap.containsKey(uid)) {
				this.updateItemprofifle(record);
				umap.get(uid).addProfile(record.mid, record.score);
			} else {
				umap.put(uid, new User(uid));
				this.updateItemprofifle(record);
				umap.get(uid).addProfile(record.mid, record.score);
			}
		}
		System.out.println("user size " + umap.size());

	}

	// choose the candidates
	public ArrayList<Integer> getCandidates(int uid, int k) {
		Set<Integer> keys = umap.keySet();
		ArrayList<Integer> list = new ArrayList<Integer>(keys);
		// choose the whole data
		//Collections.shuffle(list, rnd);
		int wsize=list.size();
		TreeSet<Integer> ulist = new TreeSet<Integer>();
		int snum = k < list.size() ? k : list.size();
		for (int i = 0; i < snum; i++) {
			int index=rnd.nextInt(wsize);
			ulist.add(list.get(index));
		}
		// the maximum neighbor's K neighbors
		User user = umap.get(uid);
		// add current knn
		ulist.addAll(user.knnid);
		int paths = user.paths;
		for (int p = 0; p < paths; p++) {
			int depth = rnd.nextInt(Configure.maxdepth);
			if (user != null) {
				int i = 1;
				int topid = user.getTopKNN();
				while (i < depth && topid != -1) {
					User cuser = umap.get(topid);
					topid = cuser.getTopKNN();
					i++;
				}
				if (topid != -1) {
					User tuser = umap.get(topid);
					ulist.addAll(tuser.knnid);
				}

			}
		}

		return new ArrayList<Integer>(ulist);

	}

	// read true KNN object
	public boolean recoverTrueKNN(File file) throws IOException,
			ClassNotFoundException {
		if (false == file.exists())
			return false;
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		HashMap<Integer, ArrayList<PairUser>> tknnmap = (HashMap<Integer, ArrayList<PairUser>>) ois
				.readObject();
		ois.close();
		Iterator<Integer> itr5 = this.umap.keySet().iterator();
		while (itr5.hasNext()) {
			int id = itr5.next();
			User user = this.umap.get(id);
			user.tknn = tknnmap.get(id);
			for (PairUser p : user.tknn) {
				user.thashknn.put(p.uid, p);
			}
			user.tknnid = new TreeSet<Integer>(user.thashknn.keySet());
		}
		return true;

	}

	

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		System.out.println("Our algorithm offline with items");
		
		KNNOffLine kal = new KNNOffLine();
		ArrayList<Record> records;
		
		if(Configure.test)
			records = kal
			.readFile(new File(
					"./rtrain.txt"));
		else
			records = kal
			.readFile(new File(
					"./rating1.txt"));
	
		ArrayList<Record> test=kal
				.readFile(new File(
						"./rtest.txt"));;

		System.out.println("test "+ test.size());
	
		kal.initialize(records);
		
		boolean isrecoverKNN=false;
		if(!Configure.test)
			isrecoverKNN = kal.recoverTrueKNN(new File(
				"./offlineknn/"+Configure.prefix+Configure.K+"trueknn.obj"));
		if (isrecoverKNN == false) {
			System.out.println("start true KNN");
			kal.trueKNN();
			System.out.println("finish true KNN");

			FileOutputStream fos = new FileOutputStream(
					"./offlineknn/"+Configure.prefix+Configure.K+"trueknn.obj");
			ObjectOutputStream ofos = new ObjectOutputStream(fos);
			HashMap<Integer, ArrayList<PairUser>> tknn = new HashMap<Integer, ArrayList<PairUser>>();
			Iterator<Integer> itr5 = kal.umap.keySet().iterator();
			while (itr5.hasNext()) {
				int id = itr5.next();
				User user = kal.umap.get(id);
				tknn.put(id, user.tknn);
			}
			ofos.writeObject(tknn);
			ofos.close();
		}
		// iteratively compute KNN
		int iterations = 0;
		// record time
		long similartime = 0;
		long similarnumber = 0;
		long candidatesizesum = 0;
		long candidatesizenum = 0;

		long startime = System.currentTimeMillis();
		
		PrintStream out10 = new PrintStream(new FileOutputStream(
				"./offlineknn/"+Configure.prefix+"recallWithIterations.txt"));
		System.setOut(out10);
		FileWriter fw=new FileWriter("./offlineknn/"+Configure.prefix+"recallWithTime.txt");
		long starter=System.currentTimeMillis();
		double counter=1;
		do {
			Iterator<Integer> outitr = kal.umap.keySet().iterator();
			while (outitr.hasNext()) {
				long cstarter=System.currentTimeMillis();
				if((cstarter-starter)/1000.0>=counter){
					double re=kal.recall();
					fw.write(counter+" "+re+"\n");
					counter=(cstarter-starter)/1000.0+Configure.interval;
				}
				int uid = outitr.next();
				// choose candidates
				long ct = System.currentTimeMillis();
				ArrayList<Integer> canlist = kal.getCandidates(uid, Configure.K);
				Set<Integer> iset = kal.umap.get(uid).profile.keySet();
				ArrayList<Integer> canilist = kal
						.getCandidateItem(new ArrayList<Integer>(iset));
				canlist.addAll(canilist);
				TreeSet<Integer> tre = new TreeSet<Integer>();
				tre.addAll(canlist);
				tre.remove(uid);
				long cet = System.currentTimeMillis();
			
				candidatesizesum += tre.size();
				candidatesizenum++;

				User user = kal.umap.get(uid);
				Iterator<Integer> itr = tre.iterator();
				int recount = 0;
				ArrayList<PairUser> candidatelist = new ArrayList<PairUser>();

				while (itr.hasNext()) {
					int vid = itr.next();
					User v = kal.umap.get(vid);

					ct = System.currentTimeMillis();
					double s = User.consionSimilarity(user.profile, v.profile,
							user.getProfilelength(), v.getProfilelength());
					cet = System.currentTimeMillis();
					similartime += (cet - ct);
					similarnumber++;

					PairUser upc = new PairUser(vid, s);
					candidatelist.add(upc);
				}

				ct = System.currentTimeMillis();
				Collections.sort(candidatelist, new CompareUser());
				recount = user.updateKNN(candidatelist);
				cet = System.currentTimeMillis();
				
				if (recount < Configure.threshholdpath) {
					user.increasePath();
				} else {
					user.decreasePath();
				}
			}
			System.out.println(iterations+" "+kal.recall());
			iterations++;
		} while (iterations < Configure.maxiterations);
		long endtime = System.currentTimeMillis();
		fw.close();
		PrintStream out2 = new PrintStream(new FileOutputStream(
				"./offlineknn/"+ Configure.prefix +Configure.K+ "KNNOffline.txt"));
		System.setOut(out2);
		System.out.println("similartime " + similartime / 60000.0 + " min in total"
				+ similartime / 1000.0 * 1.0 / similarnumber + " sec  per one time");
		// System.out.println("updteclusternumber "+updteclusternumber);
		System.out.println("total time  " + (endtime - startime) / 60000.0
				+ " " + (endtime - startime) / iterations+" mills per one iteration");
		System.out.println("avg candidates " + 1.0 * candidatesizesum
				/ candidatesizenum);

		// compute recall
		double recall =kal.recall();
		System.out.println("recall " + recall + "  " + Configure.K + "  ");

		//groups gid usersize recall
		Iterator<Integer> itr=kal.umap.keySet().iterator();
		HashMap<Integer,ArrayList<User>> sulist=new HashMap<Integer,ArrayList<User>>();
		while(itr.hasNext()){
			int id = itr.next();
			User u = kal.umap.get(id);
			int upsize = u.profile.size();
			int gid = (int) Math.ceil((1.0*upsize / Configure.intervalgroup));
			if(gid>15)
				gid=15;
			if (!sulist.containsKey(gid))
				sulist.put(gid, new ArrayList<User>());
			sulist.get(gid).add(u);
		}
		
		ArrayList<Pair> list=new ArrayList<Pair>();
		Iterator<Integer> itrr1= sulist.keySet().iterator();
		while (itrr1.hasNext()) {
			int key = itrr1.next();
			ArrayList<User> ulist = sulist.get(key);
			list.add(new Pair(ulist,key));
		}
		Collections.sort(list, new Comparator<Pair>(){
			@Override
			public int compare(Pair o1, Pair o2) {
				// TODO Auto-generated method stub
				if(o1.key>o2.key)
					return 1;
				else if(o1.key<o2.key)
					return -1;
				else
					return 0;
				
			}
			
		});
		PrintStream out5 = new PrintStream(new FileOutputStream("./offlineknn/"
				+ Configure.prefix + "userdistribution.txt"));
		System.setOut(out5);
		for(Pair u:list) {
			System.out.println(u.key + " " + u.list.size() + " "
					+ kal.computerecall(u.list));
		}
		

		if(Configure.test){
		    kal.outputtest(test);
		}
		
		
		PrintStream out = new PrintStream(new FileOutputStream(
		"./offlineknn/"+Configure.prefix+"userprofile.txt"));
		System.setOut(out);
		Iterator<Integer> itr11 = kal.umap.keySet().iterator();
		while (itr11.hasNext()) {
			int id = itr11.next();
			User user = kal.umap.get(id);
			System.out.println(user.profile.size());
			}
		
		

	}

	private double recall() {
		// compute recall
				double recall = 0.0;
				Iterator<Integer> outlist = this.umap.keySet().iterator();
				int totaluser = this.umap.size();
				while (outlist.hasNext()) {
					int uid = outlist.next();
					User u = this.umap.get(uid);
					int commonuser = u.intersectionKNN();
					recall += commonuser / (Configure.K * 1.0);

				}

				recall = recall / totaluser;
			return recall;
	}

	public double computerecall(ArrayList<User> list){
		double recall = 0.0;
		Iterator<User> outlist = list.iterator();
		int totaluser = list.size();
		while (outlist.hasNext()) {
			User u = outlist.next();
			int commonuser = u.intersectionKNN();
			recall += commonuser / (Configure.K * 1.0);

		}
		recall = recall / totaluser;
		return recall;
	}
	
	public void outputtest(ArrayList<Record> test) throws FileNotFoundException{
		this.testPredicate(test);
		PrintStream out4 = new PrintStream(new FileOutputStream(
			"./offlineknn/"+Configure.prefix+"distributionpredicate.txt"));
		System.setOut(out4);
		
		HashMap<Integer, ArrayList<Record>> rlist=new HashMap<Integer, ArrayList<Record>>();
		for(Record r:test){
			int uid=r.uid;
			User u=this.umap.get(uid);
			int gid=(int)Math.ceil(1.0*u.profile.size()/Configure.intervalgroup);
			if(!rlist.containsKey(gid))
				rlist.put(gid, new ArrayList<Record>());
			rlist.get(gid).add(r);
		}
		
		
		ArrayList<Double> err=new ArrayList<Double>();
		Iterator<Integer> itr=rlist.keySet().iterator();
		while(itr.hasNext()){
			int gid=itr.next();
			double error=0;
			ArrayList<Record> rtest=rlist.get(gid);
		for(Record r:rtest){
			error+=Math.pow(Math.abs(r.score-r.predicatescore),2);
		}
		err.add(error/rtest.size());
		System.out.println(gid+" "+Math.sqrt(error/rtest.size())+" "+rtest.size()); 
	}
		
		
		
	}
	
	// compute true KNN by force
	public void trueKNN() throws FileNotFoundException {
		Iterator<Integer> outitr = this.umap.keySet().iterator();
		while (outitr.hasNext()) {
			int id = outitr.next();
			User user = this.umap.get(id);
			ArrayList<PairUser> canlist = new ArrayList<PairUser>();
			Iterator<Integer> initr = this.umap.keySet().iterator();
			while (initr.hasNext()) {
				int vid = initr.next();
				if (vid == id)
					continue;
				User vuser = this.umap.get(vid);
				double simility = User.consionSimilarity(user.profile,
						vuser.profile, user.getProfilelength(),
						vuser.getProfilelength());
				canlist.add(new PairUser(vid, simility));

			}
			Collections.sort(canlist, new CompareUser());
			user.updateTrueKNN(canlist);
		}
	}

	
	//predicate 
	public double predicate(int iid,User u){
		double uravg=u.avgRating();
		Iterator<Integer> itr=u.knnid.iterator();
		double sum=0;
		double C=100;
		double sums=0;
		while(itr.hasNext()){
			int vid=itr.next();
			User v=this.umap.get(vid);
			if(!v.profile.containsKey(iid))
				continue;
			double vri=v.profile.get(iid);
			double s=u.hashknn.get(vid).similarity;
			
			TreeSet<Integer> tr=new TreeSet<Integer>();
			tr.addAll(u.knnid);
			tr.addAll(v.knnid);
			double commonitem=u.knnid.size()+v.knnid.size()-tr.size();
			
			sums+=1.0*s*commonitem/(C+commonitem);
			sum=sum+s*commonitem/(C+commonitem)*(vri-v.avgRating());
		}
		if(sums==0)
			return uravg;
		double r=uravg+sum/sums;
		if(r>5)
			return 5;
		else if(r<0)
			return 0;
		else
			return r;
		
	}
	
	//test function
		public void testPredicate(ArrayList<Record> list){
			int le=list.size();
			for(int i=0;i<le;i++){
				Record r=list.get(i);
				User u=this.umap.get(r.uid);
				//u.addProfile(r.mid, r.score);
				if(u!=null){
					double avgr=this.predicate(r.mid, u);
					r.predicatescore=avgr;
				}else
					r.predicatescore=2.5;
			}
		}
		
}		

	

