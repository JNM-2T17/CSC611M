public class SpawnAction implements Action {
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
		String replyContent = "{\"id\":\"" + sheepId
							+ "\",\"map\":\"" + field.snapshot(sheepId,11) 
							+ "\",\"sheep\":" + field.sheep() 
							+ ",\"x\":\"" + s.x() 
							+ "\",\"y\":\"" + s.y() + "\"}";
		cm.sendMessage("server","REPLY " + id + " " 
					+ replyContent.length() + (char)30 
					+ replyContent + (char)4);
		cm.sendMessage("update","SPAWN " + id + " 0" + (char)30 
						+ (char)4);
	}
}