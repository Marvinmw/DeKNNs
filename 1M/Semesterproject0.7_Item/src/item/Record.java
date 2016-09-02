package item;


public class Record {
  public int uid;
  public int mid;
  public double score;
  public long timestamp;
  public double predicatescore=0;
  public Record(int uid, int mid, double score,long timestamp){
	  this.uid=uid;
	  this.mid=mid;
	  this.score=score;
	  this.timestamp=timestamp;
  }

 
  public String toString(){
	  return uid+Configure.splits+mid+Configure.splits+score+Configure.splits+timestamp;
  }
  
}
