import java.net.ServerSocket;
import java.util.ArrayList;

public class ActionServer implements Updatable {
	private ConnectionManager cm;
	private Map field;
	private ProcessThread[] threads;
	private ServerSocket ss;
	private int i;

	public static void main(String[] args) {
		ActionServer as = new ActionServer(args[0]);
	}

	public ActionServer(String update) {
		ss = new ServerSocket(8082);
		cm = ConnectionManager.instance("action");
		cm.connect("update",update);
		field = new Map(100);
		cm.setField(field);
		cm.setUpdatable(this);
		i = 0;
		//create threads
		threads = new ProcessThread[20];
		for(int x=0; x<20; x++){
			threads[x] = new ProcessThread();
			new Thread(threads[x]).start();
		}
	}
	
	public void update(){}
	
	public void schedule(String key, String info){}
	
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
		
		public void tryProcess(){
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