package KMeans;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class KNNOffLine {

	// store the global users data
	private HashMap<Integer, User> umap = new HashMap<Integer, User>();
	public static Random rnd = new Random(System.currentTimeMillis());
	// store the glaobal items profile
	public HashMap<Integer, Item> itemlist = new HashMap<Integer, Item>();

	// read the input file
	public ArrayList<Record> readFile(File filepath)
			throws FileNotFoundException {
		Scanner sc = new Scanner(filepath);
		ArrayList<Record> tr = new ArrayList<Record>();
		while (sc.hasNext()) {
			String line = sc.nextLine();
			String[] info = line.split(Configure.splits);
			Record record = new Record(Integer.valueOf(info[0]),
					Integer.valueOf(info[1]), Double.valueOf(info[2]),
					Integer.valueOf(info[3]));
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

	// firstly random the sample set and return its average profile of the
	// sample set
	public HashMap<Integer, Double> getAverageProfile(int num) {
		ArrayList<User> ulist = randomUser(num);
		HashMap<Integer, Double> hm = new HashMap<Integer, Double>();
		HashMap<Integer, Integer> counter = new HashMap<Integer, Integer>();
		int size = ulist.size();
		for (int i = 0; i < size; i++) {
			User u = ulist.get(i);
			HashMap<Integer, Double> up = u.profile;
			Iterator<Integer> itr = up.keySet().iterator();
			while (itr.hasNext()) {
				int item = itr.next();
				double rating = up.get(item);
				if (hm.containsKey(item)) {
					double r = hm.get(item) + rating;
					hm.put(item, r);
					counter.put(item, counter.get(item) + 1);
				} else {
					hm.put(item, rating);
					counter.put(item, 1);
				}
			}

		}

		Iterator<Integer> keyset = hm.keySet().iterator();
		while (keyset.hasNext()) {
			int key = keyset.next();
			int numcuount = counter.get(key);
			double ratings = hm.get(key);
			double avg = (ratings) / (numcuount * 1.0);
			hm.put(key, avg);
		}

		return hm;
	}

	// use few parts of data to initialize the parameters
	public void initialize(ArrayList<Record> inirecords) {
		int times = inirecords.size();
		for (int i = 0; i < times; i++) {
			Record record = inirecords.get(i);
			this.updateItemprofifle(record);
			int uid = record.uid;
			if (umap.containsKey(uid)) {
				umap.get(uid).addProfile(record.mid, record.score);
			} else {
				umap.put(uid, new User(uid));
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

		int wsize = list.size();
		TreeSet<Integer> ulist = new TreeSet<Integer>();
		int snum = k < list.size() ? k : list.size();
		for (int i = 0; i < snum; i++) {
			int index = rnd.nextInt(wsize);
			ulist.add(list.get(index));
		}

		// Collections.shuffle(list, rnd);
		// TreeSet<Integer> ulist = new TreeSet<Integer>();
		//
		// int snum = k < list.size() ? k : list.size();
		// for (int i = 0; i < snum; i++) {
		// ulist.add(list.get(i));
		// }

		// the maximum neighbor's K neighbors
		User user = umap.get(uid);
		// add current knn
		ulist.addAll(user.knnid);
		// add candidates from the paths
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

	// update the item profile
	public void updateItemprofifle(Record record) {
		int mid = record.mid;

		if (!this.itemlist.containsKey(mid)) {
			this.itemlist.put(mid, new Item(mid));
		}

		Item item = this.itemlist.get(mid);
		item.users.add(record.uid);
		item.allsum += record.score;
		item.allsize += 1;
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		System.out.println("our algorith Offline with K-Means");

		KNNOffLine kal = new KNNOffLine();

     ArrayList<Record> records;
		
		if(Configure.test)
			records = kal
			.readFile(new File(
					"./utrain.data"));
		else
			records = kal
			.readFile(new File(
					"./u.data"));
	
		ArrayList<Record> test=kal
				.readFile(new File(
						"./utest.data"));
		

		System.out.println("train " + records.size());
		
		kal.initialize(records);
		boolean isrecover = false;
		if (!Configure.test)
			isrecover = kal.recoverTrueKNN(new File("./offlineknn/" + Configure.prefix+Configure.K + "trueknn.obj"));
		if (isrecover == false) {
			System.out.println("start true KNN");
			kal.trueKNN();
			System.out.println("finish true KNN");
			FileOutputStream fos = new FileOutputStream("./offlineknn/" + Configure.prefix+Configure.K +"trueknn.obj");
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

		// initialize K_Means
		K_Means kmeans = new K_Means();
		kmeans.initializeKCluster(Configure.clusternum);
		for (int i = 0; i < Configure.clusternum; i++) {
			kmeans.initializeProfile(i, kal.getAverageProfile(500));
		}
		// iteratively compute KNN
		int iterations = 0;

		// variables that record the time
		long candidatetime = 0;
		long ctnumber = 0;
		long similartime = 0;
		long similarnumber = 0;
		long updatetime = 0;
		long updatenumber = 0;
		long updateclustertime = 0;
		long updteclusternumber = 0;
	

		long candidatesizesum = 0;
		long candidatesizenum = 0;

		long startime = System.currentTimeMillis();
		PrintStream out10 = new PrintStream(
				new FileOutputStream("./offlineknn/" + Configure.prefix
						+ "recallWithIterations.txt"));
		System.setOut(out10);
		FileWriter fw=new FileWriter("./offlineknn/"+Configure.prefix+"recallWithTime.txt");
		long starter=System.currentTimeMillis();
		double counter=1;
		do {
			Iterator<Integer> outitr = kal.umap.keySet().iterator();
			while (outitr.hasNext()) {
				int uid = outitr.next();
				long cstarter=System.currentTimeMillis();
				if((cstarter-starter)/1000.0>=counter){
					double re=kal.recall();
					fw.write(counter+" "+re+"\n");
					counter=(cstarter-starter)/1000.0+Configure.interval;
				}
			
				// update cluster
				// start time
				long ct = System.currentTimeMillis();

				int ucid = kal.umap.get(uid).cid;
				ucid = kmeans.stickCluster(uid, ucid,
						kal.umap.get(uid).profile, kal.umap.get(uid)
								.getProfilelength(), kal.itemlist);
				kal.umap.get(uid).cid = ucid;
				// end time
				long cet = System.currentTimeMillis();
				updateclustertime += (cet - ct);
				updteclusternumber++;

				// choose candidates. TreeSet keeps the unique candidates id
				ct = System.currentTimeMillis();

				ArrayList<Integer> canlist = kal
						.getCandidates(uid, Configure.K);
				ArrayList<Integer> clu = kmeans
						.getcandidates(ucid, Configure.K);
				canlist.addAll(clu);
				TreeSet<Integer> tre = new TreeSet<Integer>();
				tre.addAll(canlist);
				tre.remove(uid);

				cet = System.currentTimeMillis();
				candidatetime += (cet - ct);
				ctnumber++;

				candidatesizesum += tre.size();
				candidatesizenum++;

				User user = kal.umap.get(uid);
				Iterator<Integer> itr = tre.iterator();
				int recount = 0;
				ArrayList<PairUser> candidatelist = new ArrayList<PairUser>();
				// compute K nearest neighbors
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
				// sort and select top K users
				Collections.sort(candidatelist, new CompareUser());
				recount = user.updateKNN(candidatelist);

				cet = System.currentTimeMillis();
				updatetime += (cet - ct);
				updatenumber++;
				// update the paths
				if (recount < Configure.threshholdpath) {
					user.increasePath();
				} else {
					user.decreasePath();
				}
			}
			System.out.println(iterations + " " + kal.recall());
			iterations++;
		} while (iterations < Configure.maxiterations);
		// System.setOut(console);
		fw.close();
		long endtime = System.currentTimeMillis();
		PrintStream out2 = new PrintStream(new FileOutputStream("./offlineknn/"
				+ Configure.prefix +Configure.K+ "KNNOffline.txt"));
		System.setOut(out2);
		System.out.println("similartime " + similartime / 60000.0 + " min "
				+ similartime / 1000.0 * 1.0 / similarnumber + " sec  ");
		System.out.println("updatetime " + updatetime / 60000.0 + " min ");
		System.out.println("updateclustertime " + updateclustertime / 60000.0
				+ " min ");
		System.out.println("total time  " + (endtime - startime) / 60000.0
				+ " " + (endtime - startime) / iterations + " millions");
		System.out.println("avg candidates " + 1.0 * candidatesizesum
				/ candidatesizenum);
//		System.out.println("Item storage "
//				+ MemoryMeasurer.measureBytes(kal.itemlist));
//		System.out.println("User storage "
//				+ MemoryMeasurer.measureBytes(kal.umap));
		// compute recall
		double recall = kal.recall();
		System.out.println("recall " + recall + "  " + Configure.K + "  ");

		// groups gid usersize recall
		Iterator<Integer> itr = kal.umap.keySet().iterator();
		HashMap<Integer, ArrayList<User>> sulist = new HashMap<Integer, ArrayList<User>>();
		while (itr.hasNext()) {
			int id = itr.next();
			User u = kal.umap.get(id);
			int upsize = u.profile.size();
			int gid = (int) Math.ceil((1.0*upsize / Configure.intervalgroup));
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

		if (Configure.test) {
			kal.outputtest(test);
		}

		//
		// PrintStream out = new PrintStream(new FileOutputStream(
		// "./offlineknn/"+Configure.prefix+"offknn.txt"));
		// System.setOut(out);
		// Iterator<Integer> itr = kal.umap.keySet().iterator();
		// while (itr.hasNext()) {
		// int id = itr.next();
		// User user = kal.umap.get(id);
		// System.out.println("++++++++++++++++");
		// System.out.println("User" + user.uid);
		// Iterator<Integer> keys = user.knnid.iterator();
		// while (keys.hasNext()) {
		// int k = keys.next();
		// System.out.println(k + "  " + user.getSimiar(k));
		// }
		// System.out.println("++++++++++++++++");
		// }
		//
		// PrintStream out1 = new PrintStream(new FileOutputStream(
		// "./offlineknn/"+Configure.prefix+"offtrueknn.txt"));
		// System.setOut(out1);
		// Iterator<Integer> itr1 = kal.umap.keySet().iterator();
		// while (itr1.hasNext()) {
		// int id = itr1.next();
		// User user = kal.umap.get(id);
		// System.out.println("++++++++++++++++");
		// System.out
		// .println("User" + user.uid + " " + user.intersectionKNN());
		// Iterator<Integer> keys = user.tknnid.iterator();
		// while (keys.hasNext()) {
		// int k = keys.next();
		// System.out.println(k + "  " + user.getTrueKsimilar(k));
		// }
		// System.out.println("++++++++++++++++");
		// }
		//
		// PrintStream out3 = new PrintStream(new FileOutputStream(
		// "./offlineknn/"+Configure.prefix+"intersection.txt"));
		// System.setOut(out3);
		// Iterator<Integer> itr3 = kal.umap.keySet().iterator();
		// while (itr3.hasNext()) {
		// int id = itr3.next();
		// User user = kal.umap.get(id);
		// System.out
		// .println("User" + user.uid + " " + user.intersectionKNN());
		// }
		// kmeans.printClusters(kal.umap);

	}

	// compute true KNN by force
	public void trueKNN() throws FileNotFoundException {

		System.out.println("trueKNN ");

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

	// read true KNN object
	public boolean recoverTrueKNN(File file) throws IOException,
			ClassNotFoundException {
		if (false == file.exists())
			return false;
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		HashMap<Integer, ArrayList<PairUser>> tknnmap = (HashMap<Integer, ArrayList<PairUser>>) ois
				.readObject();

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

	// predicate
	public double predicate(int iid, User u) {
		double uravg = u.avgRating();
		Iterator<Integer> itr = u.knnid.iterator();
		double sum = 0;
		int C = 200;
		double sums = 0;
		while (itr.hasNext()) {
			int vid = itr.next();
			User v = this.umap.get(vid);
			if (!v.profile.containsKey(iid))
				continue;
			double vri = v.profile.get(iid);
			double s = u.hashknn.get(vid).similarity;

			TreeSet<Integer> tr = new TreeSet<Integer>();
			tr.addAll(u.knnid);
			tr.addAll(v.knnid);
			int commonitem = u.knnid.size() + v.knnid.size() - tr.size();

			sums += 1.0 * s * commonitem / (C + commonitem);
			sum = sum + s * commonitem / (C + commonitem)
					* (vri - v.avgRating());

		}
		if (sums == 0)
			return uravg;
		double r = uravg + sum / sums;
		if (r > 5)
			return 5;
		else if (r < 0)
			return 0;
		else
			return r;

	}

	// test function
	public void testPredicate(ArrayList<Record> list) {
		int le = list.size();
		for (int i = 0; i < le; i++) {
			Record r = list.get(i);
			User u = this.umap.get(r.uid);
			if (u != null) {
				double avgr = this.predicate(r.mid, u);
				r.predicatescore = avgr;
			} else
				r.predicatescore = 2.5;
		}
	}

	

	public void outputtest(ArrayList<Record> test) throws FileNotFoundException {
		this.testPredicate(test);
		PrintStream out4 = new PrintStream(new FileOutputStream("./offlineknn/"
				+ Configure.prefix + "distributionpredicate.txt"));
		System.setOut(out4);

		HashMap<Integer, ArrayList<Record>> rlist = new HashMap<Integer, ArrayList<Record>>();
		for (Record r : test) {
			int uid = r.uid;
			User u = this.umap.get(uid);
			int gid = (int) Math.ceil(1.0*u.profile.size()
					/ Configure.intervalgroup);
			if (!rlist.containsKey(gid))
				rlist.put(gid, new ArrayList<Record>());
			rlist.get(gid).add(r);
		}

		ArrayList<Double> err = new ArrayList<Double>();
		Iterator<Integer> itr = rlist.keySet().iterator();
		while (itr.hasNext()) {
			int gid = itr.next();
			double error = 0;
			ArrayList<Record> rtest = rlist.get(gid);
			for (Record r : rtest) {
				error += Math.pow(Math.abs(r.score - r.predicatescore), 2);
			}
			err.add(error / rtest.size());
			System.out.println(gid + " " + Math.sqrt(error / rtest.size()));
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

	public double computerecall(ArrayList<User> list) {
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
}
