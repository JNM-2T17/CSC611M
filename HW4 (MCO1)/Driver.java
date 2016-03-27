import java.io.*;

public class Driver {
	public static int[] dummy;
	public static int[] nums;

	public static void main(String[] args) throws Exception {
		int numCount = (int)Math.pow(2,Integer.parseInt(args[0]));
		int threads = (int)Math.pow(2,Integer.parseInt(args[1]));
		int depth = (int)(Math.log(numCount) / Math.log(2));
		depth = depth * (depth + 1) / 2;
		int compsPerThread = numCount / 2 / threads;
		CompareThread[][] threadList = new CompareThread[depth][threads];

		// DataInputStream dis = new DataInputStream(
		// 						new FileInputStream(
		// 							new File("layout20.sort")));
		BufferedReader br = new BufferedReader(
								new FileReader(
									new File("layout.sort")));

		for(int i = 0; i < depth; i++) {
			System.out.println("Level " + i);
			// String[] comps = br.readLine().split(" ");
			int curr = 0;
			for(int j = 0; j < threads; j++) {
				threadList[i][j] = new CompareThread();
				for(int k = 0; k < compsPerThread; k++) {
					// int n1 = dis.readInt();
					// int n2 = dis.readInt();
					// String[] nums = comps[curr].split("-");
					String s = br.readLine();
					if(s == null) {
						System.out.println("Line " + (i * numCount + j * compsPerThread + k + 1));
					}
					String[] nums = s.split("-");
					curr++;
					threadList[i][j].add(Integer.parseInt(nums[0])
											,Integer.parseInt(nums[1]));
					// System.out.print(nums[0] + "-" + nums[1] + " ");
				}
			}
			// System.out.println();
		}
		// dis.close();

		//read nums
		BufferedReader br2 = new BufferedReader(new FileReader("nums.sort"));
		dummy = new int[numCount];
		nums = new int[numCount];
        for(int i=0; i<numCount; i++){
            dummy[i] = Integer.parseInt(br2.readLine());
        }
        br2.close();

        float ave = 0;

		for(int i = 0; i < 6; i++) {
			//copy original list
			for(int j = 0; j < numCount; j++) {
				nums[j] = dummy[j];
			}

			long time = System.currentTimeMillis();

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