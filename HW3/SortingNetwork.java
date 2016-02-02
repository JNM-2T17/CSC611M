import java.util.ArrayList;
import java.io.*;

public class SortingNetwork {
	private static ArrayList<ArrayList<String>> comparators;
	private static ArrayList<Operation> ops;

	public static void main(String[] args) throws Exception {
		int n = (int)Math.pow(2,Integer.parseInt(args[0]));
		int depth = lg(n);
		depth = depth * (depth + 1) / 2;
		comparators = new ArrayList<ArrayList<String>>();
		ops = new ArrayList<Operation>();
		ops.add(new Operation(Operation.SORTER,0,n -1,depth - 1));

		for(int i = 0; i < depth; i++ ) {
			comparators.add(new ArrayList<String>());
		}

		while(ops.size() > 0) {
			Operation o = ops.get(ops.size() - 1);
			ops.remove(ops.size() - 1);
			o.operate();
			System.out.println(ops.size());
		}

		PrintWriter pw = new PrintWriter(     new BufferedWriter( new
		FileWriter(     new File(args[1]))));

		for(int i = 0; i < depth; i++ ) {
			for(String s: comparators.get(i) ) {
				pw.print(s);
			}
			pw.println();
		}

		pw.close();
	}

	public static int lg(int n) {
		return (int)(Math.log(n) / Math.log(2));
	}

	public static void generateSorter(int s,int e, int level) {
		// pw.println("Sorter: " + s + "-" + e);
		if( e - s == 1 ) {
			comparators.get(level).add(s + "-" + e + " ");
		} else {
			generateMerger(s,e,level);
			int mid = s + (e - s) / 2;
			int len = e - s + 1;
			int depth = lg(len);
			generateSorter(s,mid,level - lg(len));
			generateSorter(mid + 1,e,level - lg(len));
		}
	}

	public static void generateMerger(int s, int e, int level) {
		// pw.println("Merger: " + s + "-" + e);
		int mid = s + (e - s) / 2;
		int len = e - s + 1;
		int depth = lg(len);
		int currLevel = level - depth + 1;
		int s2 = s;
		int e2 = e;
		while(s2 < e2) {
			comparators.get(currLevel).add(s2 + "-" + e2 + " ");
			s2++; e2--;
		}
		
		generateBitonicSorter(s,mid,currLevel + 1);
		generateBitonicSorter(mid + 1,e,currLevel + 1);
	}

	public static void generateBitonicSorter(int s, int e, int level) {
		// pw.println("Bitonic: " + s + "-" + e);
		if( e - s == 1 ) {
			comparators.get(level).add(s + "-" + e + " ");
		} else {
			int mid = s + (e - s) / 2;
			int len = e - s + 1;
			for(int s2 = s,i = mid + 1; s2 <= mid; s2++,i++ ) {
				comparators.get(level).add(s2 + "-" + i + " ");
			}
			generateBitonicSorter(s,mid,level + 1);
			generateBitonicSorter(mid + 1,e,level + 1);
		}
	}

	private static class Operation {
		public int operation;
		public int s;
		public int e;
		public int level;
		public static final int SORTER = 0;
		public static final int MERGER = 1;
		public static final int BITONIC = 2;

		public Operation(int operation,int s,int e,int level) {
			this.operation = operation;
			this.s = s;
			this.e = e;
			this.level = level;
		}

		public void operate() {
			int mid = s + (e - s) / 2;
			int len = e - s + 1;
			int depth = lg(len);

			switch(operation) {
				case SORTER:
					// pw.println("Sorter: " + s + "-" + e);
					if( e - s == 1 ) {
						comparators.get(level).add(s + "-" + e + " ");
					} else {
						ops.add(new Operation(SORTER,mid + 1,e,level - depth));
						ops.add(new Operation(SORTER,s,mid,level - depth));
						ops.add(new Operation(MERGER,s,e,level));
					}
					break;
				case MERGER:
					int currLevel = level - depth + 1;
					int s2 = s;
					int e2 = e;
					while(s2 < e2) {
						comparators.get(currLevel).add(s2 + "-" + e2 + " ");
						s2++; e2--;
					}
					
					ops.add(new Operation(BITONIC,mid + 1,e,currLevel + 1));
					ops.add(new Operation(BITONIC,s,mid,currLevel + 1));
					break;
				case BITONIC:
					if( e - s == 1 ) {
						comparators.get(level).add(s + "-" + e + " ");
					} else {
						for(s2 = s,e2 = mid + 1; s2 <= mid; s2++,e2++ ) {
							comparators.get(level).add(s2 + "-" + e2 + " ");
						}
						ops.add(new Operation(BITONIC,mid + 1,e,level + 1));
						ops.add(new Operation(BITONIC,s,mid,level + 1));
					}
					break;
				default:
			}
		}
	}
}