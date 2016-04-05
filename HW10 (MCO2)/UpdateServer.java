import java.util.HashMap;

public class UpdateServer extends Thread implements Updatable {
	private ConnectionManager cm;
	private Map field;
	private HashMap<String,String> schedule;

	public static void main(String[] args) {
		UpdateServer as = new UpdateServer();
	}

	public UpdateServer() {
		cm = ConnectionManager.instance("update");
		field = new Map(100);
		cm.setField(field);
		schedule = new HashMap<String,String>();
		cm.setUpdatable(this);
		//create threads
	}

	public synchronized void update() {
		String[] keys = schedule.keySet().toArray(new String[0]);

		for(String str: keys) {
			String info = schedule.get(str);
			if( info != null ) {
				Sheep s = field.sheep(info);
				String replyContent = "{\"map\":\"" + field.snapshot(info,11) 
							+ "\",\"sheep\":" + field.sheep() 
							+ ",\"done\":\"" + field.done() 
							+ "\",\"x\":\"" + s.x() 
							+ "\",\"y\":\"" + s.y() 
							+ "\"}";
				cm.sendMessage("server","REPLY " + str + " " 
						+ replyContent.length() + (char)30 + replyContent);
			}
			schedule.remove(str);
		}
	}

	public synchronized void schedule(String key,String tranId) {
		schedule.put(key,tranId);
	}

	public void run() {
		//start the process threads
	}
}