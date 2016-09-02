import java.util.Comparator;

   class CompareUser implements Comparator<PairUser>{

	@Override
	public int compare(PairUser o1, PairUser o2) {
		// TODO Auto-generated method stub
		if(o1.similarity < o2.similarity)
		   return 1;
		if(o1.similarity == o2.similarity)
			return 0;
		return -1;
		
	}
	 
 }
  