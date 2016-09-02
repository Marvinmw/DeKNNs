package item;
public class Configure {
	static int K = 25;
	static int maxiterations = 50;
	static int maxipath = (int) (Math.log10(K) / Math.log10(2));
	static int maxdepth = 5;
	static int minpath = 1;
	static int threshholdpath = 6;
	static double probability = 0.5;
	// ml10m
	static String prefix = "ml10mItems";
	static String splits = "::";

	static boolean test = false;
	static double intervalgroup = 60.0;
	static double interval = 1;
}
