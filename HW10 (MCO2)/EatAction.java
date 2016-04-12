public class EatAction implements Action {
	private Map field;
	private Sheep s;
	private String id;
	private String message;
	private ConnectionManager cm;
	
	public EatAction(Map field, Sheep s, final String id, final String message){
		this.field = field;
		this.s = s;
		this.id = id;
		this.message = message;
		cm = ConnectionManager.instance();
	}
	
	public void execute(){
		String replyContent = "{\"map\":\"" + field.snapshot(message,11) 
							+ "\",\"sheep\":" + field.sheep() 
							+ ",\"done\":\"" + field.done() 
							+ "\",\"x\":\"" + s.x() 
							+ "\",\"y\":\"" + s.y() 
							+ "\"}";
		cm.sendMessage("server","REPLY " + id + " " 
					+ replyContent.length() + (char)30 
					+ replyContent + (char)4);
		// cm.sendMessage("update","EAT " + id + " " 
		// 			+ message.length() + (char)30 + message 
		// 			+ (char)4);
	}
}