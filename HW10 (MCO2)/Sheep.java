public class Sheep {
	private int id;
	private int grass;
	private int x;
	private int y;

	public Sheep(int id, int x, int y) {
		this.id = id;
		grass = 0;
		this.x = x;
		this.y = y;
	}

	public int id() {
		return id;
	}

	public int grass() {
		return grass;
	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}

	public void eat() {
		grass++;
	}

	public void move(String dir) {
		switch(dir) {
			case "U":
				y--;
				break;
			case "L":
				x--;
				break;
			case "D":
				y++;
				break;
			case "R":
				x++;
				break;
			default:	
		}
	}

	public String toString() {
		return "{\"id\":\"" + id + "\",\"grass\" : " + grass + "}";
	}

}