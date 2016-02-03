import java.util.ArrayList;

public class CompareThread extends Thread {
	private ArrayList<int[]> comparators;

	public CompareThread() {
		comparators = new ArrayList<int[]>();
	}

	public CompareThread(ArrayList<int[]> comps) {
		comparators = comps;
	}

	public void add(int s, int e) {
		comparators.add(new int[] { s, e });
	}

	public void run() {
		for(int[] comp: comparators) {
			if(Driver2.nums[comp[0]] > Driver2.nums[comp[1]]) {
				int temp = Driver2.nums[comp[0]];
				Driver2.nums[comp[0]] = Driver2.nums[comp[1]];
				Driver2.nums[comp[1]] = temp;
			}
		}
	}

	public CompareThread copy() {
		return new CompareThread(comparators);
	}
}