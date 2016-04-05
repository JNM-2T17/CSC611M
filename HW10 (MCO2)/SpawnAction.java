public class SpawnAction implements Action {
	private Map field;
	private Sheep s;
	private String id;
	private String sheepId;
	private ConnectionManager cm;
	
	public SpawnAction(Map field, Sheep s, String id, String sheepId){
		this.field = field;
		this.s = s;
		this.id = id;
		this.sheepId = sheepId;
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
		String str = s.x()+" "+s.y();
		cm.sendMessage("update","SPAWN " + id + " "+str.length()+ + (char)30+str 
						+ (char)4);
	}
}