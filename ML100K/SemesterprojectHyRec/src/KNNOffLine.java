import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
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

	public ArrayList<Record> readFile(File filepath)
			throws FileNotFoundException {
		Scanner sc = new Scanner(filepath);
		ArrayList<Record> tr = new ArrayList<Record>();
		while (sc.hasNext()) {
			String line = sc.nextLine();

			String[] info = line.split("\t");

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

	// use few part of data to initialize the parameters
	public void initialize(ArrayList<Record> inirecords) {
		int times = inirecords.size();
		for (int i = 0; i < times; i++) {
			Record record = inirecords.get(i);
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
		// Collections.shuffle(list, KNNOffLine.rnd);
		int wsize = list.size();
		ArrayList<Integer> ulist = new ArrayList<Integer>();
		int snum = k < list.size() ? k : list.size();
		for (int i = 0; i < snum; i++) {
			int index = rnd.nextInt(wsize);
			ulist.add(list.get(index));
		}

		// the maximum neighbor's K neighbors
		User user = umap.get(uid);
		// add current KNN
		ulist.addAll(user.knnid);
		Iterator<Integer> itr = user.knnid.iterator();
		while (itr.hasNext()) {
			int auid = itr.next();
			User v = this.umap.get(auid);
			ulist.addAll(v.knnid);
		}
		return ulist;
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Hyrec Offline");
		KNNOffLine kal = new KNNOffLine();
		
		ArrayList<Record> records;

		if (Configure.test)
			records = kal.readFile(new File("./utrain.data"));
		else
			records = kal.readFile(new File("./u.data"));

		ArrayList<Record> test = kal.readFile(new File("./utest.data"));
		

		kal.initialize(records);
		
		System.out.println("start true KNN");
		kal.trueKNN(records);
		System.out.println("finish true KNN");

		// iteratively compute KNN
		int iterations = 0;
		// PrintStream console=System.out;
		double candidatetime = 0.0;
		double ctnumber = 0.0;
		double similartime = 0.0;
		double similarnumber = 0.0;
		double updatetime = 0.0;
		double updatenumber = 0.0;
		double updateclustertime = 0.0;
		double updteclusternumber = 0.0;
		double startime = System.currentTimeMillis();
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
				long cstarter=System.currentTimeMillis();
				if((cstarter-starter)/1000.0>=counter){
					double re=kal.recall();
					fw.write(counter+" "+re+"\n");
					counter=(cstarter-starter)/1000.0+Configure.interval;
				}
				int uid = outitr.next();
				// candidates
				double ct = System.currentTimeMillis();
				ArrayList<Integer> canlist = kal
						.getCandidates(uid, Configure.K);

				TreeSet<Integer> tre = new TreeSet<Integer>();
				tre.addAll(canlist);
				tre.remove(uid);
				double cet = System.currentTimeMillis();
				candidatetime += (cet - ct);
				ctnumber++;

				User user = kal.umap.get(uid);
				Iterator<Integer> itr = tre.iterator();

				int recount = 0;
				// System.out.println("update  process");
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
				updatetime += (cet - ct);
				updatenumber++;

			}
			System.out.println(iterations + " " + kal.recall());
			iterations++;
		} while (iterations < Configure.maxiterations);
		fw.close();
		double endtime = System.currentTimeMillis();
		PrintStream out2 = new PrintStream(new FileOutputStream("./offlineknn/"
				+ Configure.prefix + "KNNOffline.txt"));
		System.setOut(out2);
		System.out.println("candidatetime " + candidatetime);
		System.out.println("ctnumber " + ctnumber);
		System.out.println("similartime " + similartime / 1000.0 + " "
				+ similartime / 1000.0 * 1.0 / similarnumber);
		System.out.println("similarnumber " + similarnumber);
		System.out.println("updatetime " + updatetime);
		System.out.println("updatenumber " + updatenumber);
		System.out.println("updateclustertime " + updateclustertime);
		System.out.println("updteclusternumber " + updteclusternumber);
		System.out.println("total time  " + (endtime - startime) / 60000.0
				+ " min ");

		// compute recall
		double recall = 0.0;
		Iterator<Integer> outlist = kal.umap.keySet().iterator();
		int totaluser = kal.umap.size();
		while (outlist.hasNext()) {
			int uid = outlist.next();
			User u = kal.umap.get(uid);
			int commonuser = u.intersectionKNN();
			recall += commonuser / (Configure.K * 1.0);

		}

		recall = recall / totaluser;
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

		
	}

	// compute true KNN by force
	public void trueKNN(ArrayList<Record> records) throws FileNotFoundException {

		System.out.println("finish reading" + records.size());
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
						vuser.profile);
				canlist.add(new PairUser(vid, simility));

			}
			Collections.sort(canlist, new CompareUser());
			user.updateTrueKNN(canlist);
		}
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

	public void outputtest(ArrayList<Record> test) throws FileNotFoundException {
		this.testPredicate(test);
		PrintStream out4 = new PrintStream(new FileOutputStream("./offlineknn/"
				+ Configure.prefix + "distributionpredicate.txt"));
		System.setOut(out4);

		HashMap<Integer, ArrayList<Record>> rlist = new HashMap<Integer, ArrayList<Record>>();
		for (Record r : test) {
			int uid = r.uid;
			User u = this.umap.get(uid);
			int gid = (int) Math.ceil(u.profile.size()
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
			System.out.println(gid + " " + Math.sqrt(error / rtest.size())+" "+rtest.size());
		}

	}

}
