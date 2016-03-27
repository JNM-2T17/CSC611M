public class ThreadPool {
	private static ThreadPool instance = null;
	private volatile int resources;
	private int resourceCount;

	private ThreadPool(int resources) {
		resourceCount = this.resources = resources;
	}

	public static synchronized ThreadPool init(int resources) throws Exception {
		if( instance == null ) {
			instance = new ThreadPool(resources);
			return instance;
		} else {
			throw new Exception("Thread pool already initialized.");
		}
	}

	public static synchronized ThreadPool instance() throws Exception {
		if(instance == null) {
			throw new Exception("Thread pool not initialized.");
		} else {
			return instance;
		}
	}

	public synchronized void reset() {
		resources = resourceCount;
	}		

	public synchronized MergeSorter getMergeSorter(int start, int end) {
		if( resources >= 2 ) {
			resources -= 2;
			return new ParallelSorter(start,end);
		} else {
			return new LinearSorter(start,end);
		}
	}
}