public class SortThread implements Runnable {
	private MergeSorter ms;

	public SortThread(MergeSorter ms) {
		this.ms = ms;
	}

	public void run() {
		ms.mergeSort();
	}
}