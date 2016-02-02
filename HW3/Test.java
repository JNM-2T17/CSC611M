import java.util.Stack;

public class Test {
	public static void main(String[] args) {
		Stack<String> s = new Stack<String>();

		int n = (int)Math.pow(2,19) * 210;

		for(int i = 0; i < n; i++ ) {
			s.push("11111111111-111111112");
		}
	}
}