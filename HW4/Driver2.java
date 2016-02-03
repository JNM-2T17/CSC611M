import java.io.*;

public class Driver2 {
	public static int[] dummy;
	public static int[] nums;

	public static void main(String[] args) throws Exception {
		int numCount = (int)Math.pow(2,Integer.parseInt(args[0]));
		int threads = (int)Math.pow(2,Integer.parseInt(args[1]));
		int depth = (int)(Math.log(numCount) / Math.log(2));
		int compsPerThread = numCount / 2 / threads;
		CompareThread[][] threadList 
			= new CompareThread[depth * (depth + 1) / 2][threads];

		long time = System.currentTimeMillis();

		int size = 2;
		int start = 0;

		final int MERGE = 0;
		final int BITONIC = 1;
		int state = MERGE;

		int level = 0;
		for(int i = 1; i <= depth; i++,size *= 2) {
			int currSize = size;
			state = MERGE;
			for(int j = i; j > 0; j--,currSize /= 2) {
				System.out.println("Level " + level);
				start = 0;
				int k = 0;
				int chunk = currSize / 2;
				int currThreads = 0;
				int currIndex = 0;
				while(k < numCount) {
					if(currThreads == 0) {
						threadList[level][currIndex] = new CompareThread();
					}
					int partner;
					switch(state) {
						case MERGE:
							partner = currSize - 1 - k + 2 * start;
							break;
						case BITONIC:
							partner = k + currSize / 2;
							break;
						default:
							partner = 0;
					}
					threadList[level][currIndex].add(k,partner);
					// System.out.println(k + "-" + partner + " to thread " + currIndex + " of level " + level);
					currThreads++;
					if(currThreads == compsPerThread) {
						currThreads = 0;
						currIndex++;
					}
					// dos.writeInt(k);
					// dos.writeInt(partner);
					// System.out.print(k + "-" + partner + " ");
					chunk--;
					k++;
					if( chunk == 0) {
						chunk = currSize / 2;
						k += chunk;
						start = k;
					}
				}
				state = BITONIC;
				// System.out.println();
				// pw.println();
				level++;
			}
		}
		
		time = System.currentTimeMillis() - time;
		System.out.println("File generated after " + (time / 1000.0) + "s");
			
		//read nums
		BufferedReader br2 = new BufferedReader(new FileReader("nums.sort"));
		dummy = new int[numCount];
		nums = new int[numCount];
        for(int i=0; i<numCount; i++){
            dummy[i] = Integer.parseInt(br2.readLine());
        }
        br2.close();

        float ave = 0;

        depth = depth * (depth + 1) / 2;

		for(int i = 0; i < 6; i++) {
			//copy original list
			for(int j = 0; j < numCount; j++) {
				nums[j] = dummy[j];
			}

			time = System.currentTimeMillis();

			for(int layer = 0; layer < depth; layer++) {
				for(int j = 0; j < threads; j++) {
					// System.out.println("Starting thread " + j + " at layer " + layer);
					threadList[layer][j].start();
				}

				for(int j = 0; j < threads; j++) {
					threadList[layer][j].join();
				}	
			}

			time = System.currentTimeMillis() - time;

			//CPU caches on first iteration, making it slower.
			//first iteration is not considered
			if( i > 0 ) {
				ave += time;
			}

			//check if sorted
			boolean isSorted = true;
			for(int j = 0; isSorted && j < numCount - 1; j++ ){
				isSorted = nums[j] <= nums[j + 1];
			}

			if( i > 0 ) {
				System.out.println("List is " + (isSorted ? "" : "not ") 
								+ "sorted at " + (time / 1000.0) + " seconds");
			}

			if( i < 5 ) {
				for(int i2 = 0; i2 < depth; i2++) {
					// System.out.println("Cloning Level " + i2);
					// String[] comps = br.readLine().split(" ");
					int curr = 0;
					for(int j = 0; j < threads; j++) {
						threadList[i2][j] = threadList[i2][j].copy();
					}
				}
			}
		}

		System.out.println("Average time: " + (ave / 5000.0) + "s");
	}
}