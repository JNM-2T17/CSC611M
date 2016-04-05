import java.util.ArrayList;

public class Map {
	private Tile[][] map;
	private ArrayList<Sheep> sheep;
	private boolean statusChanged;

	public Map(int dimension) {
		map = new Tile[dimension][dimension];
		for(int i = 0; i < dimension; i++) {
			for( int j = 0; j < dimension; j++ ) {
				map[i][j] = new Tile();
			}
		}
		sheep = new ArrayList<Sheep>();
		statusChanged = false;
	}

	public synchronized String spawnSheep() {
		int id = sheep.size();
		int x = (int)(Math.random() * map.length);
		int y = (int)(Math.random() * map.length);
		Sheep s = new Sheep(id,x,y);
		map[y][x].addSheep(s);
		sheep.add(s);
		statusChanged = true;
		// System.out.println("NOTIFY SPAWN");
		notifyAll();
		return s.id() + "";
	}
	
	public synchronized String spawnSheep(int x, int y) {
		int id = sheep.size();
		Sheep s = new Sheep(id,x,y);
		map[y][x].addSheep(s);
		sheep.add(s);
		statusChanged = true;
		// System.out.println("NOTIFY SPAWN");
		notifyAll();
		return s.id() + "";
	}

	public synchronized Sheep sheep(String index) {
		int ind = Integer.parseInt(index);
		return sheep.get(ind);
	}

	public synchronized void moveSheep(String id, String dir) {
		Sheep s = sheep.get(Integer.parseInt(id));
		switch(dir) {
			case "U":
				if( s.y() > 0 ) {
					map[s.y()][s.x()].removeSheep(s);
					s.move(dir);
					map[s.y()][s.x()].addSheep(s);
				}
				break;
			case "L":
				if( s.x() > 0 ) {
					map[s.y()][s.x()].removeSheep(s);
					s.move(dir);
					map[s.y()][s.x()].addSheep(s);
				}
				break;
			case "D":
				if( s.y() < map.length - 1 ) {
					map[s.y()][s.x()].removeSheep(s);
					s.move(dir);
					map[s.y()][s.x()].addSheep(s);
				}
				break;
			case "R":
				if( s.x() < map.length - 1 ) {
					map[s.y()][s.x()].removeSheep(s);
					s.move(dir);
					map[s.y()][s.x()].addSheep(s);
				}
				break;
			default:	
		}
		statusChanged = true;
		// System.out.println("NOTIFY MOVE");
		notifyAll();
	}

	public synchronized void waitForChanges() {
		try {
			if( !statusChanged ) {
				wait();
			}
			statusChanged = false;statusChanged = false;
		} catch( InterruptedException ie) {
			ie.printStackTrace();
		}
	}

	public synchronized void update() {
		statusChanged = true;
		// System.out.println("NOTIFY PERIODIC");
		notifyAll();
	}

	public synchronized boolean done() {
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map.length; j++ ) {
				if( map[i][j].hasGrass() ) {
					return false;
				}
			}
		}
		return true;
	}

	public synchronized boolean eat(String id) {
		Sheep s = sheep.get(Integer.parseInt(id));
		if( map[s.y()][s.x()].eat() ) {
			s.eat();
		}
		statusChanged = true;
		// System.out.println("NOTIFY EAT");
		notifyAll();
		return done();
	}

	public synchronized String sheep() {
		String ret = "[";
		for(int i = 0; i < sheep.size(); i++) {
			if( i > 0 ) {
				ret += ",";
			}
			ret += sheep.get(i).toString();
		}
		return ret + "]";
	}

	public String toString() {
		String ret = "";
		for(int i = 0; i < map.length; i++) {
			if( i > 0 ) {
				ret += "\\n";
			}
			for(int j = 0; j < map.length; j++ ) {
				ret += map[i][j].toString();
			}
		}
		return ret;
	}

	public String toString(String index) {
		Sheep s = sheep.get(Integer.parseInt(index));
		String ret = "";
		for(int i = 0; i < map.length; i++) {
			if( i > 0 ) {
				ret += "\\n";
			}
			for(int j = 0; j < map.length; j++ ) {
				if( i == s.y() && j == s.x() ) {
					ret += map[i][j].hasGrass() ? "X" : "x";
				} else {
					ret += map[i][j].toString();
				}
			}
		}
		return ret;
	}

	public String snapshot(String index,int width) {
		Sheep s = sheep.get(Integer.parseInt(index));
		int half = width / 2;
		String ret = "";
		for(int i = s.y() - half; i <= s.y() + half; i++) {
			if( i > s.y() - half ) {
				ret += "\\n";
			}
			for(int j = s.x() - half; j <= s.x() + half; j++ ) {
				if( i == s.y() && j == s.x() ) {
					ret += map[i][j].hasGrass() ? "X" : "x";
				} else if( i < 0 || i >= map.length 
							|| j < 0 || j >= map.length ) {
					ret += "B";
				} else {
					ret += map[i][j].toString();
				}
			}
		}
		return ret;
	}
}