import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

public class Driver {
	public static int numElem = 8000000;
	public static int[] arr = new int[numElem];

	public static void main(String args[]) throws Exception {
		//args is the power of 2
		int power = Integer.parseInt(args[0]);
		ThreadPool threadPool = ThreadPool.init((int)Math.pow(2,power));
		long ave = 0;
		int[] storage = new int[numElem];
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(new File("nums.txt")));
		} catch(Exception e) {
			e.printStackTrace();
		}

		for(int i = 0; i < numElem; i++) {
			String s = br.readLine();
			try {
				storage[i] = Integer.parseInt(s);
			} catch(Exception e) {
				System.out.println(s);	
			}
		}
		br.close();

		for(int i = 0; i < 5; i++) {
			threadPool.instance().reset();
			for(int j = 0; j < numElem; j++) {
				arr[j] = storage[j];
			}
			
			long time = System.currentTimeMillis();
			MergeSorter m = threadPool.instance().getMergeSorter(0,numElem - 1);
			m.mergeSort();
			time = System.currentTimeMillis() - time;
			ave += time;

			boolean sorted = true;
			
			for(int j = 0; sorted && j < numElem - 1; j++) {
				sorted = sorted && arr[j] <= arr[j+1];
				if( !sorted ) {
					System.out.println("" + j);
					System.out.println(arr[j] + " <= " + arr[j+1]);
				}
			}

			System.out.println("Sorted: " + sorted);
			
			System.out.println("Time: " + (time / 1000.0) + "s");
		}

		System.out.println("Average Time: " + (ave / 5000.0) + "s");
	}
}