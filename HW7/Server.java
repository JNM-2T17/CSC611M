import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.EOFException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class Server {
	private ServerSocket ss;

	public static void main(String[] args) {
		Server s = new Server();
		s.start();

	}

	public Server() {
		try {
			ss = new ServerSocket(80);
		} catch( IOException ioe ) {
			ioe.printStackTrace();
		}
	}

	public void start() {
		Socket curr;
		while(true) {
			try {
				curr = ss.accept();
				String ip = curr.getInetAddress().getHostAddress();
				System.out.println("CONNECTED TO " + ip);
				DataInputStream dis = new DataInputStream(curr.getInputStream());
				String message = "";
				char c;
				int end = 0;
				do {
					try {
						c = (char)dis.readUnsignedByte();
					} catch(EOFException e) {
						break;
					}
					switch( end ) {
						case 0:
							if( c == '\r') {
								end++;
							}
							break;
						case 1:
							if( c == '\n') {
								end++;
							} else {
								end = 0;
							}
							break;
						case 2:
							if( c == '\r') {
								end++;
							} else {
								end = 0;
							}
							break;
						case 3:
							if( c == '\n') {
								end++;
							} else {
								end = 0;
							}
							break;
						default:	
					}
					message += c;
				} while( end < 4 );
				System.out.println(message);
				String fn = message.split(" ")[1].substring(1);

				String[] parts = fn.split("\\?");
				fn = parts[0];
				String[] paramsStr = null;
				HashMap<String,String> params = new HashMap<String,String>();
				if( parts.length > 1 ) {
					paramsStr = parts[1].split("&");
					for( String s: paramsStr) {
						String[] par = s.split("=");
						params.put(par[0],par.length == 1 ? "" : par[1]);
						System.out.println("putting " + par[0] + " -> " + (par.length == 1 ? "" : par[1]));
					}
				}
				
				String reply = "HTTP/1.1 200 OK\n" + 
								"Date: Mon, 23 May 2005 22:38:34 GMT\n" + 
								"Content-Type: text/html; charset=UTF-8\n" + 
								"Content-Encoding: UTF-8\n";
				String content = "";
				try {
					BufferedReader br = new BufferedReader(
											new FileReader(
												new File(fn)));
					String s;
					do {
						s = br.readLine();
						if( s != null) {
							content += s + "\n";
						} else {
							break;
						}
					} while(true);
					br.close();

					reply += "Content-Length: " + content.length() + "\n" + 
								"Server: Apache/1.3.3.7 (Unix) (Red-Hat/Linux)\n" + 
								"ETag: \"3f80f-1b6-3e1cb03b\"\n" + 
								"Accept-Ranges: bytes\n" + 
								"Connection: close\n\n" + content;
					DataOutputStream dos 
						= new DataOutputStream(curr.getOutputStream());
					// System.out.println(reply);
					dos.writeBytes(reply);
					dos.flush();
					dos.close();
				} catch(IOException ioe) {}
				curr.close();
			} catch( IOException ioe ) {
				ioe.printStackTrace();
			}
		}
	}
}