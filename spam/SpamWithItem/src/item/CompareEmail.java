package item;
import java.util.Comparator;

   class CompareEmail implements Comparator<PairEmail>{

	@Override
	public int compare(PairEmail o1, PairEmail o2) {
		// TODO Auto-generated method stub
		if(o1.similarity < o2.similarity)
		   return 1;
		if(o1.similarity == o2.similarity)
			return 0;
		return -1;
		
	}
	 
 }
  