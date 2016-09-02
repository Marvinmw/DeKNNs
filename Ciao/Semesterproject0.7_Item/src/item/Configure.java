package item;

public class Configure {
	 static  int K=25;
	 static   int maxiterations=30;
	 //MP
	 static   int maxipath=(int)(Math.log10(K)/Math.log10(2));
	 static   int maxdepth=5;
	 static   int minpath=1;
 static   int threshholdpath=6;
 static   double probability=0.5;
 //Ciao
 static String prefix="CiaoItems";
 //read file format
 static String splits="  ";
 //test or not
 static boolean test=false;
 //user groups
 static double intervalgroup=4.0;
 //period to compute KNN
 static double interval=0.1;
}
