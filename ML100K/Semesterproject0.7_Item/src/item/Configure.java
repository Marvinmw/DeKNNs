package item;
public class Configure {
	static int K = 25;
	static int maxiterations = 30;
	static int maxipath = (int) (Math.log10(K) / Math.log10(2));
	static int maxdepth = 5;
	static int minpath = 1;
	static int threshholdpath = 6;
	static double probability = 0.5;
	// ml100k
	static String prefix = "ml100kItems";
	static String splits = "\t";

	static boolean test = true;
	static double intervalgroup = 60.0;
	static double interval = 0.1;
}
