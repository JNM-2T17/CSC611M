import java.util.ArrayList;

public class Tile {
	private boolean hasGrass;
	private ArrayList<Sheep> sheep;

	public Tile() {
		hasGrass = true;
		sheep = new ArrayList<Sheep>();
	}

	public boolean hasGrass() {
		return hasGrass;
	}

	public synchronized boolean eat() {
		boolean temp = hasGrass;
		hasGrass = false;
		return temp;
	}

	public synchronized void addSheep(Sheep s) {
		sheep.add(s);
	}

	public synchronized void removeSheep(Sheep s) {
		sheep.remove(s);
	}

	public String toString() {
		if( sheep.size() == 0 ) {
			return hasGrass ? "T" : "t";
		} else {
			return hasGrass ? "S" : "s";
		}
	}
}