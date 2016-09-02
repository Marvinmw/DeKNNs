package KMeans;


public class Record {
  public int uid;
  public int mid;
  public double score;
  public long timestamp;
  public double predicatescore=0;
  public Record(double uid, double mid, double score,double timestamp){
	  this.uid=(int)uid;
	  this.mid=(int)mid;
	  this.score=score;
	  this.timestamp=(long)timestamp;
  }

 
  public String toString(){
	  return uid+"\t"+mid+"\t"+score+"\t"+timestamp;
  }
  
  
  
}
