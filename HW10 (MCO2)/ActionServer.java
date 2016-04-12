import java.net.ServerSocket;
import java.util.ArrayList;

public class ActionServer implements Updatable {
	private ConnectionManager cm;
	private Map field;
	private ProcessThread[] threads;
	private UpdateThread[] updateThreads;
	private int i;
	private int currThreadNo;
	private Updater updater;

	public static void main(String[] args) {
		ActionServer as = new ActionServer();
	}

	public ActionServer() {
		cm = ConnectionManager.instance("action");
		field = new Map(100);
		cm.setField(field);
		cm.setUpdatable(this);
		i = 0;

		currThreadNo = 0;

		// create action threads
		threads = new ProcessThread[15];
		for(int x = 0; x < 15; x++){
			threads[x] = new ProcessThread();
			new Thread(threads[x]).start();
		}

		// create update threads
		updateThreads = new UpdateThread[5];
		for(int x = 0; x < 5; x++){
			updateThreads[x] = new UpdateThread(field);
			updateThreads[x].start();
		}

		updater = new Updater();
		updater.start();
	}
	
	public void update(){
		updater.wakeUp();
	}

	class Updater extends Thread {
		private boolean update;

		public Updater() {
			update = false;
		}

		public synchronized void run() {
			while(true) {
				try {
					while(!update) {
						wait();
					}
					update = false;
					for(UpdateThread thread : updateThreads){
						thread.notifyMethod();
					}
					Thread.sleep(100);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}	
		}

		public synchronized void wakeUp() {
			update = true;
			notifyAll();
		}
	}
	
	public void schedule(String key, String info){
		updateThreads[currThreadNo].schedule(key, info);
		currThreadNo = (currThreadNo + 1) % 5;
	}
	
	public void schedule(Action a){
		threads[i].addAction(a);
		i = (i + 1) % threads.length;
	}
	
	class ProcessThread implements Runnable {
		private ArrayList<Action> actions;
		
		public ProcessThread(){
			actions = new ArrayList<>();
		}
		
		public synchronized void addAction(Action a){
			actions.add(a);
			notifyAll();
		}
		
		public void run(){
			while(true){
				tryProcess();
			}
		}
		
		public synchronized void tryProcess(){
			while(actions.size() == 0 ) {
				try {
					wait();
				} catch( Exception e) {
					e.printStackTrace();
				}
			}
			Action a = actions.get(0);
			actions.remove(0);
			a.execute();
		}
	}
	
}