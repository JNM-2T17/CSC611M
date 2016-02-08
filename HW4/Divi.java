public class Divi {
	public static void main(String[] args) {
		for(int i: getDivisions(Integer.parseInt(args[0])
									,Integer.parseInt(args[1]))) {
			System.out.print(i + " ");
		}
	}

	public static int[] getDivisions(int blocks,int divisions) {
		int[] answers = new int[divisions];
		int total = blocks * (blocks + 1) / 2;
		int partition = total / divisions;
		int runningPartition = partition;
		int runningTotal = 0;
		for(int i = 1,j = 0; i <= blocks; i++) {
			runningTotal += i;
			if(runningTotal >= runningPartition) {
				runningPartition += partition;
				answers[j] = i;
				j++;
			}
		}
		return answers;
	}
}