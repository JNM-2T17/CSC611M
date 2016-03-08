import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.EOFException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server {
	private User[] users;
	private ServerSocket ss;

	public static void main(String[] args) {
		int count = Integer.parseInt(args[0]);
		Server s = new Server(count);
		s.start();

	}

	public Server(int count) {
		users = new User[count];
		try {
			ss = new ServerSocket(6969);
		} catch( IOException ioe ) {
			ioe.printStackTrace();
		}
	}

	public void start() {
		signUp();
		for( User u: users) {
			sendMessage(u.getSocket(),"OK\0");
		}
		serve();
	}

	public void serve() {
		Socket curr;
		while(true) {
			try {
				curr = ss.accept();
				String ip = curr.getInetAddress().getHostAddress();
				DataInputStream dis = new DataInputStream(curr.getInputStream());
				String message = dis.readUTF();
				System.out.println("GOT " + message);
				for( User u : users ) {
					if( !u.ip().equals(ip) ) {
						sendMessage(u.getSocket(),u.name() + ": " + message);
					}
				}
			} catch( IOException ioe ) {
				ioe.printStackTrace();
			}
		}
	}

	public void signUp() {
		int i = 0;
		Socket curr;
		while(i < users.length) {
			try {
				System.out.println("Waiting");
				curr = ss.accept();
				String ip = curr.getInetAddress().getHostAddress();
				DataInputStream dis = new DataInputStream(curr.getInputStream());
				String name = dis.readUTF();
				System.out.println(name + " signed in");
				users[i] = new User(name, ip);
				i++;
			} catch( IOException ioe ) {
				ioe.printStackTrace();
			}
		}
	}

	public void sendMessage(Socket s,String message) {
		try {
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeUTF(message + "\0");
			dos.close();
			s.close();
		} catch( IOException ioe ) {
			ioe.printStackTrace();
		}
	}


}