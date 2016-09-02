package KMeans;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;

public class K_Means {

	public HashMap<Integer, Cluster> clusters = new HashMap<Integer, Cluster>();

	// initialize the cluster
	public void initializeKCluster(int cnum) {
		for (int i = 0; i < cnum; i++)
			clusters.put(i, new Cluster());
	}

	// assign the initial profile
	public void initializeProfile(int cluster, HashMap<Integer, Double> profile) {
		clusters.get(cluster).profile = profile;
		ArrayList<Double> list = new ArrayList<Double>(profile.values());
		for (double i : list) {
			clusters.get(cluster).length += i * i;
		}
	}

	// update cluster
	public void updateCluster(int cluster, int uid,
			HashMap<Integer, Double> uprofile, HashMap<Integer, Item> itemlist) {
		Cluster c = clusters.get(cluster);
		Iterator<Integer> itr = uprofile.keySet().iterator();

		double learnignrate = c.memembers.size();
		while (itr.hasNext()) {
			int item = itr.next();
			double rating = uprofile.get(item);
			// if the item exists, then it update directly
			// else the item doesn't exists, get the average rating whose
			// profiles have it
			if (c.profile.containsKey(item)) {
				c.length -= c.profile.get(item) * c.profile.get(item);
				double r = c.profile.get(item) + (rating - c.profile.get(item))
						/ learnignrate * 1.0;
				c.length += r * r;
				c.profile.put(item, r);
			} else {
				Item it = itemlist.get(item);

				double avgr = it.getAverage();
				double r = avgr + (rating - avgr) / learnignrate * 1.0;
				c.length += r * r;
				c.profile.put(item, r);
			}

		}
	}

	private class Cluster {
		public Cluster() {
		}

		ArrayList<Integer> memembers = new ArrayList<Integer>();
		HashMap<Integer, Double> profile = new HashMap<Integer, Double>();
		private double length = 0.0;

	}

	// find the matched cluster
	public int stickCluster(int uid, int ucid,
			HashMap<Integer, Double> profile, double ulength,
			HashMap<Integer, Item> itemlist) {

		Iterator<Integer> itr = this.clusters.keySet().iterator();
		int id = 0;
		double si = -1;
		// find the most similar one
		while (itr.hasNext()) {
			int cid = itr.next();
			Cluster c = this.clusters.get(cid);
			double s = User.consionSimilarity(profile, c.profile,
					Math.sqrt(c.length), ulength);
			if (si < s) {
				id = cid;
				si = s;
			}
		}
		if (ucid != -1)
			this.clusters.get(ucid).memembers.remove((Integer)uid);

		this.clusters.get(id).memembers.add(uid);
		this.updateCluster(id, uid, profile, itemlist);
		return id;
	}

	// choose the candidates
	public ArrayList<Integer> getcandidates(int ucid, int k) {
		// TODO Auto-generated method stub
		ArrayList<Integer> cl = new ArrayList<Integer>();

		Cluster cluster = this.clusters.get(ucid);
		ArrayList<Integer> list = cluster.memembers;
		if(list.size()==0)
			return cl;
		for (int i = 0; i < k; i++) {
			int index=KNNOffLine.rnd.nextInt(list.size());
			int id=list.get(index);
			cl.add(id);
			// System.out.print(" "+list.get(i));
		}
		

		return cl;
	}

	public void printClusters(HashMap<Integer, User> userlist)
			throws FileNotFoundException {

		ArrayList<Double> simiwitcluster = new ArrayList<Double>();
		ArrayList<Double> avgrecords = new ArrayList<Double>();

		Iterator<Integer> itr1 = this.clusters.keySet().iterator();
		while (itr1.hasNext()) {
			int cid = itr1.next();

			PrintStream out = new PrintStream(new FileOutputStream(cid
					+ "offcluster.txt"));
			System.setOut(out);

			Cluster c = this.clusters.get(cid);
			ArrayList<Integer> members = c.memembers;
			Iterator<Integer> mitr = members.iterator();
			double sum = 0;
			double csum = 0.0;
			while (mitr.hasNext()) {
				int uid = mitr.next();
				User user = userlist.get(uid);

				Iterator<Integer> inmitr = members.iterator();
				while (inmitr.hasNext()) {
					int vid = inmitr.next();
					if (vid == uid)
						continue;
					User vser = userlist.get(vid);
					double s = User.consionSimilarity(user.profile,
							vser.profile);
					System.out.println(" (" + uid + " " + s + ") ");
					sum += s;
				}

				double s1 = User.consionSimilarity(user.profile, c.profile);
				csum += s1;
			}
			if (c.memembers.size() != 0)
				avgrecords.add(sum
						/ (c.memembers.size() * (c.memembers.size() - 1)));
			else
				avgrecords.add(0.0);

			if (c.memembers.size() != 0)
				simiwitcluster.add(csum / c.memembers.size());
			else
				simiwitcluster.add(0.0);

		}

		// PrintStream out1 = new PrintStream(new
		// FileOutputStream("offcluster.txt"));
		// System.setOut(out1);
		// Iterator<Integer> itr=this.clusters.keySet().iterator();
		// while(itr.hasNext()){
		// int cid=itr.next();
		// Cluster c=this.clusters.get(cid);
		// //
		// System.out.println(cid+" "+c.memembers.size()+"  "+avgrecords.get(cid)+" "+simiwitcluster.get(cid));
		// }
	}

}
