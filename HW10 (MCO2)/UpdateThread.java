public class UpdateThread extends Thread {
	private Map field;
	private HashMap<String,String> schedule;

	public UpdateThread(Map field) {
		this.field = field;
		schedule = new HashMap<String,String>();
	}

	public synchronized void update() {
		while(schedule.isEmpty()){
			wait();
		}
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

	public synchronized void notifyMethod(){
		notifyAll();
	}

	public void run() {
		while(true){
			update();
		}
	}
}