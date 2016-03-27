public abstract class MergeSorter implements Runnable{
	protected int start;
	protected int end;

	public MergeSorter(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public void mergeSort() {
		if(start < end) {
			sort();
			merge();
		}
	}

	protected abstract void sort();

	protected int mid() {
		return start + (end - start) / 2;
	}

	private void merge() {
		merge(start, mid(), end);
	}
    
    protected void merge( int s, int m, int e ) {
		
		int size1 = m - s + 1;
		int size2 = e - m;
		int[] left = new int[size1];
		int[] right = new int[size2];
		int i;
		int j;
		int k;
		
		for( i = 0; i < size1; i++ ) {
			left[i] = Driver.arr[ s + i ];
		}
		
		for( j = 0; j < size2; j++ ) {
			right[j] = Driver.arr[ m + 1 + j ];		
		}
		
		i = j = 0;
		
		for( k = s; k <= e; k++ ) {
			if( j == size2 || ( i != size1 && left[i] < right[j]  ) ){
				Driver.arr[k] = left[i];
				i++;
			} else {
				Driver.arr[k] = right[j];
				j++;
			}
		}
	}

	public void run() {
		mergeSort();
	}
}