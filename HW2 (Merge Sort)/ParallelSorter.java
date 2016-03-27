public class ParallelSorter extends MergeSorter{
	private SlowThread left;
	private SlowThread right;

	public ParallelSorter(int start, int end) {
		super(start,end);
	}

	protected void sort() {
		try {
			left = new SlowThread(ThreadPool.instance()
								.getMergeSorter(start, mid()));
	        right = new SlowThread(ThreadPool.instance()
	        					.getMergeSorter(mid() + 1, end));
	        left.start();
	        right.start();
			left.join2();
			right.join2();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}