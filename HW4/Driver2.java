import java.io.*;

public class Driver2 {
	public static int[] dummy;
	public static int[] nums;
	private static int numCount;
	private static int threads;
	private static int depth;
	private static int compsPerThread;
	private static int size;
	private static int start;

	public static void main(String[] args) throws Exception {
		//number of numbers
		numCount = (int)Math.pow(2,Integer.parseInt(args[0]));

		//number of threads
		threads = (int)Math.pow(2,Integer.parseInt(args[1]));

		//max layers in a merger
		depth = (int)(Math.log(numCount) / Math.log(2));

		// comparators per thread
		compsPerThread = numCount / 2 / threads;

		//read nums
		BufferedReader br2 = new BufferedReader(new FileReader("nums.sort"));
		dummy = new int[numCount];
		nums = new int[numCount];
        for(int i=0; i<numCount; i++){
            dummy[i] = Integer.parseInt(br2.readLine());
        }
        br2.close();

        float ave = 0;
		
		CompareThread[][] threadList;
		long time;
		int currDepth;
		float maxTime = 0;

		// depth = depth * (depth + 1) / 2;

		for(int i = 0; i < 6; i++){

			float currTime = 0;
			size = 2; //number of elements being sorted at a time
			start = 0; //start of current block

			for(int j = 0; j < numCount; j++) {
				nums[j] = dummy[j];
			}

			for(int g = 0; g < 2; g++){
				int half = (int)Math.floor((Math.sqrt(2 * Math.pow(depth,2) + 2 * depth + 1) - 1)/2);

				if(g == 0){
					threadList = generateThreadList(1, half);
				}

				else{
					threadList = generateThreadList(half + 1, depth);
				}

				time = System.currentTimeMillis();

				for(int layer = 0; layer < threadList.length; layer++) {
					for(int j = 0; j < threads; j++) {
						threadList[layer][j].start();
					}

					for(int j = 0; j < threads; j++) {
						threadList[layer][j].join();
					}	
				}

				currTime += System.currentTimeMillis() - time;
				
			}

			//CPU caches on first iteration, making it slower.
			//first iteration is not considered
			ave += currTime;
			if( currTime > maxTime) {
				maxTime = currTime;
			}
			
			boolean isSorted = true;
			for(int j = 0; j < numCount - 1; j++ ){
				if(nums[j] > nums[j + 1]){
					isSorted = false;
					break;
				}
			}

			System.out.println("List is " + (isSorted ? "" : "not ") 
							+ "sorted at " + (currTime / 1000.0) + " seconds");
		}

		//check if sorted
		
		ave -= maxTime;

		System.out.println("Average time: " + (ave / 5000.0) + "s");
	}

	private static CompareThread[][] generateThreadList(int gStart, int gEnd){

		//list of threads
		CompareThread[][] threadList 
			= new CompareThread[(gEnd * (gEnd + 1) / 2) - ((gStart - 1) * gStart / 2) ][threads];

		long time = System.currentTimeMillis();		

		//what is being created at the moment
		final int MERGE = 0;
		final int BITONIC = 1;
		int state = MERGE;

		//level in network
		int level = 0;

		//for each block
		for(int i = gStart; i <= gEnd; i++,size *= 2) {
			int currSize = size;
			state = MERGE;
			for(int j = i; j > 0; j--,currSize /= 2) {
				// System.out.println("Level " + level);
				start = 0;
				int k = 0;
				int chunk = currSize / 2; //number of comps before skip
				int currComps = 0;  //comparators already threads
				int currIndex = 0; //current thread in layer

				//for each wire
				while(k < numCount) {

					//no comps yet, create thread
					if(currComps == 0) {
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
					currComps++;

					if(currComps == compsPerThread) {
						currComps = 0;
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
		// System.out.println("File generated after " + (time / 1000.0) + "s");

		return threadList;
	}
}