import java.util.Stack;

public class Test {
	public static void main(String[] args) {
		int i = 0;

		try {
			Stack<int[]> s = new Stack<int[]>();

			for(i = 0; i < 110100480; i++ ) {
				s.push(new int[]{(int)(Math.pow(2,31) - 1),(int)(Math.pow(2,31) - 1)});
			}
		} catch(Exception e) {
			System.out.println("Out of memory after " + i + " integer pairs");
		} 
	}
}