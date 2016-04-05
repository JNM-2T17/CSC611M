public class ActionServer extends Thread {
	private ConnectionManager cm;
	private Map field;

	public static void main(String[] args) {
		ActionServer as = new ActionServer(args[0]);
	}

	public ActionServer(String update) {
		cm = ConnectionManager.instance("action");
		cm.connect("update",update);
		field = new Map(100);
		cm.setField(field);
		//create threads
	}

	public void run() {
		//start the process threads
	}
}