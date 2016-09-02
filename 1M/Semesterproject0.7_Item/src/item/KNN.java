package item;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
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

public class KNN {

	// store the users
	private HashMap<Integer, User> umap = new HashMap<Integer, User>();
	// store the items
	public HashMap<Integer, Item> itemlist = new HashMap<Integer, Item>();

	// read data to memory: ArrayList<Record>
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

	// sample users from the know users
	public ArrayList<User> randomUser(int num) {
		Set<Integer> keys = umap.keySet();
		ArrayList<Integer> list = new ArrayList<Integer>(keys);
		Collections.shuffle(list, KNNOffLine.rnd);
		ArrayList<User> ulist = new ArrayList<User>();

		int snum = num < list.size() ? num : list.size();
		for (int i = 0; i < snum; i++) {
			ulist.add(umap.get(list.get(i)));
		}

		return ulist;

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

	public ArrayList<Integer> getCandidates(int uid, int k) {
		Set<Integer> keys = umap.keySet();
		ArrayList<Integer> list = new ArrayList<Integer>(keys);
		// choose the whole data
		Collections.shuffle(list, KNNOffLine.rnd);
		ArrayList<Integer> ulist = new ArrayList<Integer>();
		int snum = k < list.size() ? k : list.size();
		for (int i = 0; i < snum; i++) {
			ulist.add(list.get(i));
		}
		// the maximum neighbor's K neighbors
		User user = umap.get(uid);
		// add current KNN
		ulist.addAll(user.knnid);

		int paths = user.paths;
		for (int p = 0; p < paths; p++) {
			int depth = KNNOffLine.rnd.nextInt(Configure.maxdepth);
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

		return ulist;

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

	public ArrayList<Integer> getCandidateItem(ArrayList<Integer> list) {
		int size = list.size();

		ArrayList<Integer> rlist = new ArrayList<Integer>();
		for (int i = 0; i < Configure.K; i++) {
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

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		System.out.println("Our algorithm online with items");
//        int[] karray={15,25,35,45,55,65,75,85,95,105};
//        for(int ki=0;ki<karray.length;ki++){
//        	Configure.K=karray[ki];
      
		KNN kal = new KNN();
		ArrayList<Record> records;

		if (Configure.test)
			records = kal.readFile(new File("./rtrain.dat"));
		else
			records = kal.readFile(new File("./ratings1.dat"));

		ArrayList<Record> test = kal.readFile(new File("./rtest.dat"));

		System.out.println("train " + records.size());

		int rsize = records.size();

		long updateprofiletime = 0;
		long choosecandidatetime = 0;
		long amountcandidate = 0;
		long similartime = 0;
		long amountsimilar = 0;
		long updateKNNtime = 0;
		long updateKNN = 0;

		long totalstart = System.currentTimeMillis();
		long clientstarttime = 0;
		long clientendtimestamp = 0;
		long clienttotaltime = 0;

		long candidatesizesum = 0;
		long candidatesizenum = 0;
		PrintStream out10 = new PrintStream(new FileOutputStream("./onlineknn/"
				+ Configure.prefix + "candidatesize.txt"+Configure.K));
		System.setOut(out10);
		for (int i = 0; i < rsize; i++) {
			Record r = records.get(i);

			int uid = r.uid;
			long st = System.currentTimeMillis();
			// update profile
			if (!kal.umap.containsKey(uid)) {
				kal.umap.put(uid, new User(uid));
			}
			kal.updateItemprofifle(r);
			kal.umap.get(uid).addProfile(r.mid, r.score);
			
			kal.umap.get(uid).profile.get(r.mid);
			Item idd = kal.itemlist.get(r.mid);
			idd.getAverage();

			long e = System.currentTimeMillis();
			updateprofiletime += (e - st) / 1.0;

			// candidates
			st = System.currentTimeMillis();
			ArrayList<Integer> canlist = kal.getCandidates(uid, Configure.K);
			Set<Integer> iset = kal.umap.get(uid).profile.keySet();
			ArrayList<Integer> canilist = kal
					.getCandidateItem(new ArrayList<Integer>(iset));
			canlist.addAll(canilist);

			TreeSet<Integer> tre = new TreeSet<Integer>();
			tre.addAll(canlist);
			tre.remove(uid);
			System.out.println(tre.size());
			candidatesizesum += tre.size();
			candidatesizenum++;

			e = System.currentTimeMillis();
			choosecandidatetime += (e - st) / 1.0;
			amountcandidate++;

			// compute the similarity among the users
			clientstarttime = System.currentTimeMillis();
			ArrayList<PairUser> candidatelist = new ArrayList<PairUser>();
			User user = kal.umap.get(uid);
			Iterator<Integer> itr = tre.iterator();
			int count = 0;
			while (itr.hasNext()) {
				int vid = itr.next();
				User v = kal.umap.get(vid);
				st = System.currentTimeMillis();
				double s = User.consionSimilarity(user.profile, v.profile,
						user.getProfilelength(), v.getProfilelength());
				e = System.currentTimeMillis();
				similartime += (e - st) / 1.0;
				amountsimilar++;
				PairUser upc = new PairUser(vid, s);
				candidatelist.add(upc);
			}
			// sort and select top K
			st = System.currentTimeMillis();
			Collections.sort(candidatelist, new CompareUser());
			count = user.updateKNN(candidatelist);
			e = System.currentTimeMillis();
			updateKNNtime += (e - st) / 1.0;
			updateKNN++;
			if (count < Configure.threshholdpath)
				user.increasePath();
			else
				user.decreasePath();
			clientendtimestamp = System.currentTimeMillis();
			clienttotaltime += (clientendtimestamp - clientstarttime);
		}

		long endtime = System.currentTimeMillis();
		PrintStream out2 = new PrintStream(new FileOutputStream("./onlineknn/"
				+ Configure.prefix + "KNNOnline.txt"));
		System.setOut(out2);
		System.out.println("similartime " + similartime / 60000.0 + " min  "
				+ similartime / 1000.0 * 1.0 / amountsimilar + " sec  ");
		// System.out.println("amountsimilar "+amountsimilar);
		System.out.println("updateKNNtime " + updateKNNtime / 60000.0
				+ " min  ");
		System.out.println("total time " + (endtime - totalstart) / 1000.0
				* 1.0 / 60.0 + " min  " + (endtime - totalstart) / 1000.0 * 1.0
				/ rsize + " sec");
		System.out.println("Client time " + clienttotaltime / 1000.0 * 1.0
				* 1.0 / rsize + " sec  ");
		System.out.println("avg candidates " + 1.0 * candidatesizesum
				/ candidatesizenum);
		// System.setOut(standard);
		System.out.println("start true KNN");

		boolean isrecover = false;
		if (!Configure.test)
			isrecover = kal.recoverTrueKNN(new File("./onlineknn/"
					+ Configure.prefix +Configure.K+ "trueknn.obj"));
		if (isrecover == false) {
			kal.trueKNN(records);
			FileOutputStream fos = new FileOutputStream("./onlineknn/"
					+ Configure.prefix +Configure.K+  "trueknn.obj");
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
		PrintStream out5 = new PrintStream(new FileOutputStream("./onlineknn/"
				+ Configure.prefix + "userdistribution.txt"));
		System.setOut(out5);
		for(Pair u:list) {
			System.out.println(u.key + " " + u.list.size() + " "
					+ kal.computerecall(u.list));
		}



		if (Configure.test) {
			kal.outputtest(test);
		}

       // }
		
	}

	// compute true KNN by force
	public void trueKNN(ArrayList<Record> records) throws FileNotFoundException {
		System.out
				.println("finish reading records in total  " + records.size());
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

	public void outputtest(ArrayList<Record> test) throws FileNotFoundException {
		this.testPredicate(test);
		PrintStream out4 = new PrintStream(new FileOutputStream("./onlineknn/"
				+ Configure.prefix + "distributionpredicate.txt"));
		System.setOut(out4);

		HashMap<Integer, ArrayList<Record>> rlist = new HashMap<Integer, ArrayList<Record>>();
		HashMap<Integer, Integer> count = new HashMap<Integer, Integer>();
		for (Record r : test) {
			int uid = r.uid;
			User u = this.umap.get(uid);
			int gid = (int) Math.ceil(1.0*u.profile.size()
					/ Configure.intervalgroup);
			if (!rlist.containsKey(gid))
				rlist.put(gid, new ArrayList<Record>());
			
			count.put(r.uid,u.profile.size());
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
			System.out.println(gid +" "+ Math.sqrt(error / rtest.size()));
		}

		
		PrintStream out = new PrintStream(new FileOutputStream(
				"./onlineknn/"+Configure.prefix+"userprofile.txt"));
				System.setOut(out);
				Iterator<Integer> itr11 = count.keySet().iterator();
				while (itr11.hasNext()) {
					int id = itr11.next();
					int user = count.get(id);
					System.out.println(id+" "+user);
					}
				
				
		
		
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

