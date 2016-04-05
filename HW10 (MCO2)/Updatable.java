public interface Updatable {
	public void update();
	public void schedule(String key,String info);
	public void schedule(Action a);
}