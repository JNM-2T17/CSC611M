import java.net.Socket;
import java.net.UnknownHostException;

public class User {
	private String name;
	private String ip;

	public User(String name, String ip) {
		this.name = name;
		this.ip = ip;
	}

	public String name() {
		return name;
	}

	public String ip() {
		return ip;
	}

	public Socket getSocket() {
		try {
			return new Socket(ip,6969);
		} catch(Exception uhe) {
			uhe.printStackTrace();
			return null;
		}
	}
}