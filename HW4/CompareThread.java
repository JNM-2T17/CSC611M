import java.util.ArrayList;

public class CompareThread extends Thread {
	private ArrayList<int[]> comparators;

	public CompareThread() {
		comparators = new ArrayList<int[]>();
	}

	public void add(int s, int e) {
		comparators.add(new int[] { s, e });
	}

	public void run() {
		for(int[] comp: comparators) {
			if(Driver.nums[comp[0]] > Driver.nums[comp[1]]) {
				int temp = comp[0];
				comp[0] = comp[1];
				comp[1] = temp;
			}
		}
	}
}