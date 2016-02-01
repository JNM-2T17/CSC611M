import java.io.*;

public class Blah {
	public static void main(String[] args) throws Exception {
		int elem = (int)Math.pow(2,18);
		PrintWriter pw = new PrintWriter(
							new FileWriter(
								new File("nums.txt")));

		for(int i = 0; i < elem; i++ ) {
			pw.print(random());
			if( i < elem - 1) {
				pw.println();
			}
		}

		pw.close();

	}

	public static int random() {
		return (int)((Math.random() * Math.pow(2,32)) - Math.pow(2,31));
	}
}