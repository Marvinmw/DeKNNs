package item;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.TreeSet;


public class SplitData {
    //produce the test data
	public static void main(String[] args) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(
				"./ratings1.dat"));
		ArrayList<Record> records = new ArrayList<Record>();
		while (sc.hasNext()) {
			String line = sc.nextLine();

			String[] info = line.split(Configure.splits);

			Record record = new Record(Integer.valueOf(info[0]),
					Integer.valueOf(info[1]), Double.valueOf(info[2]),
					Integer.valueOf(info[3]));
			records.add(record);
		}
		sc.close();
		
		Collections.shuffle(records, KNNOffLine.rnd);
		Collections.shuffle(records, KNNOffLine.rnd);
		Collections.shuffle(records, KNNOffLine.rnd);
		Collections.shuffle(records, KNNOffLine.rnd);
		Collections.shuffle(records, KNNOffLine.rnd);
		Collections.shuffle(records, KNNOffLine.rnd);
		
		
//		PrintStream out12 = new PrintStream(new FileOutputStream(
//				"./ratings.dat"));
//		System.setOut(out12);
//		for(int i=0;i<records.size();i++){
//			System.out.println(records.get(i).toString());
//		}
		
		int totalsize=records.size();
		int tlindex=(int) (totalsize*0.2);
		ArrayList<Record> test=new ArrayList<Record>(records.subList(0, tlindex));
		records=new ArrayList<Record>(records.subList(tlindex, totalsize));
		
		PrintStream out10 = new PrintStream(new FileOutputStream(
				"./rtrain.dat"));
		System.setOut(out10);
		for(int i=0;i<records.size();i++){
			System.out.println(records.get(i).toString());
		}
		
		
		PrintStream out11 = new PrintStream(new FileOutputStream(
				"./rtest.dat"));
		System.setOut(out11);
		for(int i=0;i<test.size();i++){
			System.out.println(test.get(i).toString());
		}
		
		
	}

}
