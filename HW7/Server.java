import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.EOFException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.URLDecoder;
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
				String post = "";
				char c;
				int end = 0;
				String type = "";
				HashMap<String,String> params = new HashMap<String,String>();
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
					if( end == 4 ) {
						System.out.println("THIS IS THE MESSAGE: " + message);
						if(message.split(" ")[0].equals("POST")) {
							type = "POST";
							String[] parts = message.split("\n");
							int length = 0;
							for(String s: parts) {
								if( s.startsWith("Content-Length: ")) {
									length 
										= Integer.parseInt(
												s.substring("Content-Length: "
																.length()
															,s.length() - 1));
									break;
								}
							}
							for(int i = 0; i < length; i++) {
								post += (char)dis.readUnsignedByte();
							}
							post = URLDecoder.decode(post,"UTF-8");
						} else {
							type = "GET";
						}
					} 
				} while( end < 4 );
				try {
					if( message.length() > 0 ) {
						String fn = message.split(" ")[1].substring(1);

						String[] parts = fn.split("\\?");
						fn = parts[0];
						String[] paramsStr = new String[0];
						if( post.length() > 0 ) {
							paramsStr = post.split("&");
						} else if( parts.length > 1 ) {
							paramsStr = parts[1].split("&");
						}

						for( String s: paramsStr) {
							String[] par = s.split("=");
							params.put(par[0],par.length == 1 ? "" : par[1]);
							System.out.println("putting " + par[0] + " -> " 
												+ (par.length == 1 ? "" : par[1]));
						}

					
						String reply = "HTTP/1.1 200 OK\n" + 
										"Date: Mon, 23 May 2005 22:38:34 GMT\n" + 
										"Content-Type: text/html; charset=UTF-8\n" + 
										"Content-Encoding: UTF-8\n";
						String content = "";
						
						if( type.equals("POST") && fn.equals("Gradebook.txt")) {
							PrintWriter pw = new PrintWriter(
												new FileWriter(
													new File("Gradebook.txt")));
							pw.print(params.get("gb"));
							pw.close();
						} else {
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
						}

						reply += "Content-Length: " + content.length() + "\n" + 
									"Server: Java/1.0 (Unix)\n" + 
									"ETag: \"3f80f-1b6-3e1cb03b\"\n" + 
									"Accept-Ranges: bytes\n" + 
									"Connection: close\r\n\n\n" + content;
						DataOutputStream dos 
							= new DataOutputStream(curr.getOutputStream());
						// System.out.println(reply);
						dos.writeBytes(reply);
						dos.flush();
						dos.close();
					} else {
						DataOutputStream dos 
							= new DataOutputStream(curr.getOutputStream());
						// System.out.println(reply);
						dos.writeBytes("HTTP/1.1 200 OK\n" + 
									"Date: Mon, 23 May 2005 22:38:34 GMT\n" + 
									"Content-Type: text/html; charset=UTF-8\n" + 
									"Content-Encoding: UTF-8\n" + 
									"Content-Length: 0\n" + 
									"Server: Java/1.0 (Unix)\n" + 
									"ETag: \"3f80f-1b6-3e1cb03b\"\n" + 
									"Accept-Ranges: bytes\n" + 
									"Connection: close\r\n\n\n"
									);
						dos.flush();
						dos.close();
					}
				} catch(IOException ioe) {}
				
				curr.close();
			} catch( IOException ioe ) {
				ioe.printStackTrace();
			}
		}
	}
}