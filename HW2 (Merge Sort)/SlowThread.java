public class SlowThread extends Thread {
	private boolean running;

	public SlowThread(Runnable r) {
		super(r);
		running = true;
	}

	public void start() {
		running = true;
		super.run();
		running = false;
	}

	public void join2() {
		while(running){
			yield();
		}
	}
}