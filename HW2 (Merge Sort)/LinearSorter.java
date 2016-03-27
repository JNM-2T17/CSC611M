public class LinearSorter extends MergeSorter {
    
	public LinearSorter(int start, int end) {
		super(start,end);
	}

	protected void sort() {
        sort( start, mid() );
		sort( mid() + 1, end );
	}
	
	protected void sort(int start, int end) {
        if( start < end ) {
			int m = start + ( end - start ) / 2;
			sort( start, m );
			sort( m + 1, end );
			merge( start, m, end );
		}	
	}
}