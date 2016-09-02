package HyRec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class KNNOffLine {

	// store the global users data
	private HashMap<Integer, Email> umap = new HashMap<Integer, Email>();
	public static Random rnd = new Random(System.currentTimeMillis());

	// read the input file
	public ArrayList<Record> readFile(File filepath)
			throws FileNotFoundException {
		int id = 1;
		Scanner sc = new Scanner(filepath);
		ArrayList<Record> tr = new ArrayList<Record>();
		while (sc.hasNext()) {
			String line = sc.nextLine();
			String[] info = line.split(Configure.splits);
			double[] features = new double[57];
			for (int i = 0; i < features.length; i++) {
				features[i] = Double.parseDouble(info[i]);
			}

			Record record = new Record(id, Integer.valueOf(info[57]), features);
			tr.add(record);
			id++;
		}
		sc.close();
		return tr;
	}

	public void normalizeData() {
		Iterator<Integer> itr = this.umap.keySet().iterator();
		double[] avg = new double[57];
		while (itr.hasNext()) {
			int eid = itr.next();
			Email e = this.umap.get(eid);
			for (int i = 0; i < avg.length; i++) {
				avg[i] += e.profile[i];
			}
		}

		int size = this.umap.size();
		for (int i = 0; i < avg.length; i++) {
			avg[i] /= size;
		}

		Iterator<Integer> itr1 = this.umap.keySet().iterator();
		double[] var = new double[57];
		while (itr1.hasNext()) {
			int eid = itr1.next();
			Email e = this.umap.get(eid);
			for (int i = 0; i < var.length; i++) {
				var[i] += (e.profile[i] - avg[i]) * (e.profile[i] - avg[i]);
			}
		}

		for (int i = 0; i < avg.length; i++) {
			var[i] /= (size - 1);
		}

		Iterator<Integer> itr2 = this.umap.keySet().iterator();
		while (itr2.hasNext()) {
			int eid = itr2.next();
			Email e = this.umap.get(eid);
			for (int i = 0; i < avg.length; i++) {
				e.profile[i] = (e.profile[i] - avg[i]) / var[i];
			}
		}
	}

	// random the users from the whole data
	public ArrayList<Email> randomUser(int num) {
		Set<Integer> keys = umap.keySet();
		ArrayList<Integer> list = new ArrayList<Integer>(keys);
		Collections.shuffle(list, rnd);
		ArrayList<Email> ulist = new ArrayList<Email>();

		int snum = num < list.size() ? num : list.size();
		for (int i = 0; i < snum; i++) {
			ulist.add(umap.get(list.get(i)));
		}

		return ulist;

	}

	// firstly random the sample set and return its average profile of the
	// sample set
	public double[] getAverageProfile(int num) {
		ArrayList<Email> ulist = randomUser(num);
		double[] hm = new double[57];

		for (int j = 0; j < hm.length; j++) {
			hm[j] = 0;
		}

		int size = ulist.size();
		for (int i = 0; i < size; i++) {
			Email u = ulist.get(i);
			double[] up = u.profile;

			for (int j = 0; j < up.length; j++) {
				hm[j] += up[j];
			}

		}

		for (int j = 0; j < hm.length; j++) {
			hm[j] /= size;
		}

		return hm;
	}

	// use few parts of data to initialize the parameters
	public void initialize(ArrayList<Record> inirecords) {
		int times = inirecords.size();
		for (int i = 0; i < times; i++) {
			Record record = inirecords.get(i);
			int uid = record.uid;
			umap.put(uid, new Email(uid, record.features, record.score));
		}

		System.out.println("user size " + umap.size());

	}

	// choose the candidates
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
		Email user = umap.get(uid);
		// add current KNN
		ulist.addAll(user.knnid);
		Iterator<Integer> itr = user.knnid.iterator();
		while (itr.hasNext()) {
			int auid = itr.next();
			Email v = this.umap.get(auid);
			ulist.addAll(v.knnid);
		}
		return ulist;
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		System.out.println("our algorith Offline with K-Means");

		KNNOffLine kal = new KNNOffLine();
		ArrayList<Record> records = kal
				.readFile(new File(
						"./spambase/spambase.data"));
		// Collections.shuffle(records, KNNOffLine.rnd);
		int totalsize = records.size();
		int tlindex = (int) (totalsize * 0.2);
		HashMap<Integer, Integer> test = new HashMap<Integer, Integer>();
        boolean producetest=false;
		if (producetest) {
			PrintStream standard=System.out;
			PrintStream out11 = new PrintStream(new FileOutputStream(
					"./offlineknn/" + Configure.prefix + "test.txt"));
			System.setOut(out11);
			int stsize = 0;
			while (stsize < tlindex) {
				int index = KNNOffLine.rnd.nextInt(totalsize);
				if (index == 0)
					continue;
				test.put(index, index);
				stsize = test.size();
				System.out.println(index);
			}
			System.setOut(standard);
		} else {
			Scanner sc = new Scanner(new File("./offlineknn/"
					 +"emailItemtest.txt"));
			while (sc.hasNext()) {
				int index = Integer.valueOf(sc.nextLine());
				test.put(index, index);
			}
			sc.close();
		}
		
		System.out.println("test " + test.size());
		System.out.println("train " + records.size());
		kal.initialize(records);
		kal.normalizeData();
		boolean isrecover = false;
		// isrecover = kal.recoverTrueKNN(new File("./" + Configure.prefix
		// + "trueknn.obj"));

		if (isrecover == false) {
			System.out.println("start true KNN");
			kal.trueKNN();
			System.out.println("finish true KNN");
			FileOutputStream fos = new FileOutputStream("./" + Configure.prefix
					+ "trueknn.obj");
			ObjectOutputStream ofos = new ObjectOutputStream(fos);
			HashMap<Integer, ArrayList<PairEmail>> tknn = new HashMap<Integer, ArrayList<PairEmail>>();
			Iterator<Integer> itr5 = kal.umap.keySet().iterator();
			while (itr5.hasNext()) {
				int id = itr5.next();
				Email user = kal.umap.get(id);
				tknn.put(id, user.tknn);
			}
			ofos.writeObject(tknn);
			ofos.close();

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
		long clientstarttime = 0;
		long clientendtimestamp = 0;
		long clienttotaltime = 0;
		long clientst = 0;
		long clineted = 0;
		long candidatesizesum = 0;
		long candidatesizenum = 0;

		long startime = System.currentTimeMillis();
		PrintStream out2 = new PrintStream(new FileOutputStream("./offlineknn/"
				+ Configure.prefix + "SpamRecallAccuracy.txt"));
		System.setOut(out2);
		do {
			Iterator<Integer> outitr = kal.umap.keySet().iterator();
			while (outitr.hasNext()) {
				int uid = outitr.next();

				// update cluster
				// start time
				long ct = System.currentTimeMillis();
				// end time
				long cet = System.currentTimeMillis();
				updateclustertime += (cet - ct);
				updteclusternumber++;

				// choose candidates. TreeSet keeps the unique candidates id
				ct = System.currentTimeMillis();

				ArrayList<Integer> canlist = kal
						.getCandidates(uid, Configure.K);
				TreeSet<Integer> tre = new TreeSet<Integer>();
				tre.addAll(canlist);
				tre.remove(uid);

				cet = System.currentTimeMillis();
				candidatetime += (cet - ct);
				ctnumber++;

				candidatesizesum += tre.size();
				candidatesizenum++;

				Email user = kal.umap.get(uid);
				Iterator<Integer> itr = tre.iterator();
				int recount = 0;
				ArrayList<PairEmail> candidatelist = new ArrayList<PairEmail>();
				// compute K nearest neighbors
				while (itr.hasNext()) {
					int vid = itr.next();
					Email v = kal.umap.get(vid);

					ct = System.currentTimeMillis();

					double s = Email.consionSimilarity(user.profile, v.profile);

					cet = System.currentTimeMillis();
					similartime += (cet - ct);
					similarnumber++;

					PairEmail upc = new PairEmail(vid, s);
					candidatelist.add(upc);
				}

				ct = System.currentTimeMillis();
				// sort and select top K users
				Collections.sort(candidatelist, new CompareEmail());
				recount = user.updateKNN(candidatelist);

				cet = System.currentTimeMillis();
				updatetime += (cet - ct);
				updatenumber++;

			}
			kal.output(iterations, test);
			iterations++;
		} while (iterations < Configure.maxiterations);
		// System.setOut(console);
		long endtime = System.currentTimeMillis();
		PrintStream out3 = new PrintStream(new FileOutputStream("./offlineknn/"
				+ Configure.prefix + "KNNOffline.txt"));
		System.setOut(out3);
		System.out.println("similartime " + similartime / 60000.0 + " min "
				+ similartime / 1000.0 * 1.0 / similarnumber + " sec  ");
		// System.out.println("similarnumber "+similarnumber);
		System.out.println("updatetime " + updatetime / 60000.0 + " min ");
		// System.out.println("updatenumber "+updatenumber);
		System.out.println("updateclustertime " + updateclustertime / 60000.0
				+ " min ");
		// System.out.println("updteclusternumber "+updteclusternumber);
		System.out.println("total time  " + (endtime - startime) / 60000.0
				+ " " + (endtime - startime) / iterations);
		System.out.println("avg candidates " + 1.0 * candidatesizesum
				/ candidatesizenum);

		// compute recall
		double recall = 0.0;
		Iterator<Integer> outlist = kal.umap.keySet().iterator();
		int totaluser = kal.umap.size();
		while (outlist.hasNext()) {
			int uid = outlist.next();
			Email u = kal.umap.get(uid);
			int commonuser = u.intersectionKNN();
			recall += commonuser / (Configure.K * 1.0);

		}

		recall = recall / totaluser;
		System.out.println("recall " + recall + "  " + Configure.K + "  ");

	}

	public void output(int iteration, HashMap<Integer, Integer> test) {
		this.testPredicate(test);
		int r1 = 0;
		int f1 = 0;
		int r0 = 0;
		int f0 = 0;
		Iterator<Integer> itr4 = test.keySet().iterator();
		while (itr4.hasNext()) {
			int eid = itr4.next();
			Email e = this.umap.get(eid);
			if (e.predicate == 1 && e.spam == 1)
				r1++;
			if (e.predicate == 1 && e.spam == 0)
				f0++;

			if (e.predicate == 0 && e.spam == 1)
				f1++;

			if (e.predicate == 0 && e.spam == 0)
				r0++;
		}

		// System.out.println("r1 " + r1+" f1 "+f1+" r0 "+r0+" f0 "+f0+" ");
		double recall = this.recall();
		System.out.println(iteration + " " + r1 + " " + f1 + " " + r0 + " "
				+ f0 + " " + 1.0 * (r1 + r0) / (r1 + f1 + r0 + f0) * 1.0 + " "
				+ recall);
	}

	private double recall() {
		// compute recall
		double recall = 0.0;
		Iterator<Integer> outlist = this.umap.keySet().iterator();
		int totaluser = this.umap.size();
		while (outlist.hasNext()) {
			int uid = outlist.next();
			Email u = this.umap.get(uid);
			int commonuser = u.intersectionKNN();
			recall += commonuser / (Configure.K * 1.0);

		}

		recall = recall / totaluser;
		return recall;
	}

	// compute true KNN by force
	public void trueKNN() throws FileNotFoundException {

		System.out.println("trueKNN ");

		Iterator<Integer> outitr = this.umap.keySet().iterator();
		while (outitr.hasNext()) {
			int id = outitr.next();
			Email user = this.umap.get(id);
			ArrayList<PairEmail> canlist = new ArrayList<PairEmail>();
			Iterator<Integer> initr = this.umap.keySet().iterator();
			while (initr.hasNext()) {
				int vid = initr.next();
				if (vid == id)
					continue;
				Email vuser = this.umap.get(vid);
				double simility = Email.consionSimilarity(user.profile,
						vuser.profile);
				canlist.add(new PairEmail(vid, simility));

			}
			Collections.sort(canlist, new CompareEmail());
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
		HashMap<Integer, ArrayList<PairEmail>> tknnmap = (HashMap<Integer, ArrayList<PairEmail>>) ois
				.readObject();

		Iterator<Integer> itr5 = this.umap.keySet().iterator();
		while (itr5.hasNext()) {
			int id = itr5.next();
			Email user = this.umap.get(id);
			user.tknn = tknnmap.get(id);
			for (PairEmail p : user.tknn) {
				user.thashknn.put(p.uid, p);
			}
			user.tknnid = new TreeSet<Integer>(user.thashknn.keySet());
		}

		return true;

	}

	// predicate
	public int predicateBysum(Email u, HashMap<Integer, Integer> list) {
		TreeSet<Integer> knnid = u.getTopNKNN(2);
		Iterator<Integer> itr = knnid.iterator();
		double v1 = 0;
		double v0 = 0;
		while (itr.hasNext()) {
			int eid = itr.next();
			if (list.containsKey(eid))
				continue;

			Email v = this.umap.get(eid);

			if (v.spam == 0)
				v0 += u.getSimiar(eid);

			else
				v1 += u.getSimiar(eid);

		}

		if (v1 >= v0)
			return 1;
		else
			return 0;

	}

	// predicate
	public int predicateByAvg(Email u, HashMap<Integer, Integer> list) {
		TreeSet<Integer> knnid = u.knnid;
		Iterator<Integer> itr = knnid.iterator();
		double v1 = 0;
		double v0 = 0;
		int countv1 = 0;
		int countv0 = 0;

		while (itr.hasNext()) {
			int eid = itr.next();
			if (list.containsKey(eid))
				continue;

			Email v = this.umap.get(eid);

			if (v.spam == 0) {
				v0 += u.getSimiar(eid);
				countv0++;
			} else {
				v1 += u.getSimiar(eid);
				countv1++;
			}
		}
		double avg1 = v1 / (countv1 * 1.0);
		double avg0 = v0 / (countv0 * 1.0);
		if (avg1 >= avg0)
			return 1;
		else
			return 0;

	}

	// predicate
	public int predicateByMin(Email u, HashMap<Integer, Integer> list) {
		TreeSet<Integer> knnid = u.knnid;
		Iterator<Integer> itr = knnid.iterator();
		double v1 = 1;
		double v0 = 1;

		while (itr.hasNext()) {
			int eid = itr.next();
			if (list.containsKey(eid))
				continue;

			Email v = this.umap.get(eid);

			if (v.spam == 0)
				v0 = Math.min(u.getSimiar(eid), v0);
			else
				v1 = Math.min(u.getSimiar(eid), v1);

		}

		if (v1 >= v0)
			return 1;
		else
			return 0;

	}

	// predicate
	public int predicateByMax(Email u, HashMap<Integer, Integer> list) {
		TreeSet<Integer> knnid = u.knnid;
		Iterator<Integer> itr = knnid.iterator();
		double v1 = 0;
		double v0 = 0;

		while (itr.hasNext()) {
			int eid = itr.next();
			if (list.containsKey(eid))
				continue;

			Email v = this.umap.get(eid);

			if (v.spam == 0)
				v0 = Math.max(u.getSimiar(eid), v0);
			else
				v1 = Math.max(u.getSimiar(eid), v1);

		}

		if (v1 >= v0)
			return 1;
		else
			return 0;

	}

	// test function
	public void testPredicate(HashMap<Integer, Integer> list) {
		Iterator<Integer> itr = list.keySet().iterator();
		while (itr.hasNext()) {
			int eid = itr.next();
			Email e = this.umap.get(eid);
			int cv1 = 0;
			int cv0 = 0;

			if (this.predicateByMax(e, list) == 1)
				cv1 += 2;
			else
				cv0 += 2;

			if (this.predicateByAvg(e, list) == 1)
				cv1++;
			else
				cv0++;

			if (this.predicateBysum(e, list) == 1)
				cv1++;
			else
				cv0++;
			if (this.predicateByMin(e, list) == 1)
				cv1++;
			else
				cv0++;

			if (cv1 > cv0)
				e.predicate = 1;
			else
				e.predicate = 0;

		}
	}

}
