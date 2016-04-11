import java.net.ServerSocket;
import java.util.ArrayList;

public class ActionServer implements Updatable {
	private ConnectionManager cm;
	private Map field;
	private ProcessThread[] threads;
	private UpdateThread[] updateThreads;
	private int i;

	public static void main(String[] args) {
		ActionServer as = new ActionServer(args[0]);
	}

	public ActionServer(String update) {
		cm = ConnectionManager.instance("action");
		cm.connect("update",update);
		field = new Map(100);
		cm.setField(field);
		cm.setUpdatable(this);
		i = 0;

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
	}
	
	public void update(){
		for(UpdateThread thread : updateThreads){
			thread.notifyMethod();
		}
	}
	
	public void schedule(String key, String info){
		updateThreads[currThreadNo].schedule(key, tranId);
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