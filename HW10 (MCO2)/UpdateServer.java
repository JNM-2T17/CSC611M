import java.util.HashMap;

public class UpdateServer implements Updatable {
	private ConnectionManager cm;
	private Map field;
	private UpdateThread[] updateThreads;
	private int currThreadNo = 0;

	public static void main(String[] args) {
		UpdateServer as = new UpdateServer();
	}

	public UpdateServer() {
		cm = ConnectionManager.instance("update");
		field = new Map(100);
		cm.setField(field);
		cm.setUpdatable(this);
		updateThreads = new UpdateThread[20];
		for(int i = 0; i < 20; i++){
			updateThreads[i] = new UpdateThread(field);
			updateThreads[i].start();
		}
	}

	public synchronized void update() {
		for(UpdateThread thread : updateThreads){
			thread.notifyMethod();
		}
	}

	public synchronized void schedule(String key,String tranId) {
		updateThreads[currThreadNo].schedule(key, tranId);
		currThreadNo = (currThreadNo + 1) % 20;
	}

	public synchronized void schedule(Action a) {}
}