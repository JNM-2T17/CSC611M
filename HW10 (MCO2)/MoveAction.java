public class MoveAction implements Action {
	private Map field;
	private Sheep s;
	private String id;
	private String message;
	private String sheepId;
	private ConnectionManager cm;
	
	public MoveAction(Map field, Sheep s, final String id, final String message, String sheepId){
		this.field = field;
		this.s = s;
		this.id = id;
		this.message = message;
		this.sheepId = sheepId;
		cm = ConnectionManager.instance();
	}
	
	public void execute(){
		System.out.println("PUTANGINA "+s.id());
		String replyContent = "{\"map\":\"" + field.snapshot(sheepId,11) 
							+ "\",\"sheep\":" + field.sheep() 
							+ ",\"done\":\"" + field.done() 
							+ "\",\"x\":\"" + s.x() 
							+ "\",\"y\":\"" + s.y() 
							+ "\"}";
		System.out.println("PUTA2 "+s.id());
		cm.sendMessage("server","REPLY " + id + " " 
							+ replyContent.length() + (char)30 
							+ replyContent + (char)4);
		System.out.println("FINISHED SENDING "+s.id());
		// cm.sendMessage("update","MOVE " + id + " " 
		// 					+ message.length() + (char)30 + message 
		// 					+ (char)4);
	}
}