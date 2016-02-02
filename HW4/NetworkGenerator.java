import java.io.*;

public class NetworkGenerator {
	public static void main(String[] args) {
		try {
			// PrintWriter pw = new PrintWriter(
			// 					new FileWriter(
			// 						new File("layout.sort")));
			DataOutputStream dos = new DataOutputStream(
										new FileOutputStream(
											new File("layout.sort")));
			int nums = (int)Math.pow(2,Integer.parseInt(args[0]));
			int depth = (int)(Math.log(nums) / Math.log(2));

			int size = 2;
			int start = 0;

			final int MERGE = 0;
			final int BITONIC = 1;
			int state = MERGE;

			long time = System.currentTimeMillis();

			for(int i = 1; i <= depth; i++,size *= 2) {
				int currSize = size;
				state = MERGE;
				for(int j = i; j > 0; j--,currSize /= 2) {
					System.out.println("Level " + ((i - 1) * i / 2 + i - j));
					start = 0;
					int k = 0;
					int chunk = currSize / 2;
					while(k < nums) {
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
						// pw.print(k + "-" + partner + " ");
						dos.writeInt(k);
						dos.writeInt(partner);
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
				}
			}
			// pw.close();
			dos.close();

			time = System.currentTimeMillis() - time;
			System.out.println("File generated after " + (time / 1000.0) + "s");
			// DataInputStream dis = new DataInputStream(
			// 						new FileInputStream(
			// 							new File("layout.sort")));
			// depth = depth * (depth + 1) / 2;
			// for(int i = 0; i < depth; i++) {
			// 	for(int j = 0; j < nums; j++) {
			// 		int n = dis.readInt();
			// 		System.out.print((j % 2 == 0 ? n : "-" + n + " "));
			// 	}
			// 	System.out.println();
			// }
			// dis.close();
		} catch( IOException ioe ) {
			ioe.printStackTrace();
		}

	}
}