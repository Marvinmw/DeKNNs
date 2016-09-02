package HyRec;


public class Record {
  public int uid;
  public int score;
  public int predicatescore=0;
  public double[] features=new double[57];
  public Record(int uid, int score,double[] feature){
	  this.uid=uid;
	  this.score=score;
	  this.features=feature;
  }

 
  
  
}
