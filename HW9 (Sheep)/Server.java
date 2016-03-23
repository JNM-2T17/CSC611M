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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Server {
	private ServerSocket ss;
	private ProcessThread[] processors;
	private Map field;

	public static void main(String[] args) {
		Server s = new Server();
		s.start();
	}

	public Server() {
		try {
			ss = new ServerSocket(8081);
			processors = new ProcessThread[20];
			for(int i = 0; i < processors.length; i++ ) {
				processors[i] = new ProcessThread();
				processors[i].start();
			}
			field = new Map(20);
 		} catch( IOException ioe ) {
			ioe.printStackTrace();
		}
	}

	public void start() {
		Socket curr;
		int i = 0;
		while(true) {
			try {
				System.out.println("Waiting");
				curr = ss.accept();
				System.out.println("CONNECTED TO " + curr.getInetAddress().getHostAddress());
				processors[i].addSocket(curr);
				i = (i + 1) % processors.length;
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	class ProcessThread extends Thread {
		private Stack<Socket> sockets;

		public ProcessThread() {
			sockets = new Stack<Socket>();
		}

		public synchronized void addSocket(Socket s) {
			sockets.push(s);
			notifyAll();
		}

		public synchronized void tryProcess() {
			while(sockets.size() == 0 ) {
				try {
					wait();
				} catch( Exception e) {
					e.printStackTrace();
				}
			}
			Socket s = sockets.pop();
			process(s);
		}

		public void run() {
			while(true) {
				tryProcess();
			}
		}

		public void process(Socket curr) {
			try {
				String ip = curr.getInetAddress().getHostAddress();
				System.out.println("PROCESSING " + ip);
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
						String fn = URLDecoder.decode(
										message.split(" ")[1].substring(1));

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

						String replyContent = "";
						byte[] replyBytes;
						switch(type) {
							case "GET":
								if(!fn.equals("sheep")) {
									try {
										int length = 32768;
										File file = new File(fn);
										DataInputStream is = new DataInputStream(
																	new FileInputStream(file));
										byte[] fileInput = new byte[length];
										int current = 0;
										int read;

										DataOutputStream dos 
											= new DataOutputStream(curr.getOutputStream());
										String reply = "HTTP/1.1 200 OK\r\n" + 
														"Date: Mon, 23 May 2005 22:38:34 GMT\r\n" + 
														"Content-Type: text/html; charset=UTF-8\r\n" + 
														"Content-Encoding: UTF-8\r\n" +
														"Content-Length: " + file.length() +
														"\r\n" + 
														"Server: Java/1.0 (Unix)\r\n" + 
														"ETag: \"3f80f-1b6-3e1cb03b\"\r\n" + 
														"Accept-Ranges: bytes\r\n" + 
														"Connection: close\r\n\r\n";
										// System.out.println(reply);
										dos.writeBytes(reply);
										do{
											read = is.read(fileInput,0
															,(int)Math.min(length
																,file.length() - current));
											if( read >= 0 ) {
												current += read;
											}
											dos.write(fileInput,0,read);
										} while(current < file.length());
										dos.flush();
										dos.close();
									} catch( IOException ioe ) {

									} finally {
										return;
									}
								} else {
									String id = field.spawnSheep();
									replyContent = "{\"id\":\"" + id
											+ "\",\"map\":\"" + field.toString(id) 
											+ "\",\"sheep\":" + field.sheep() + "}";
								}
								break;
							case "POST":
								String dir = params.get("dir");
								String id = params.get("id");
										
								switch(params.get("request")) {
									case "move":
										field.moveSheep(id,dir);
										break;
									case "eat":
										boolean done = field.eat(id);
										break;
									default:
								}

								replyContent = "{\"map\":\"" + field.toString(id) 
											+ "\",\"sheep\":" + field.sheep() 
											+ ",\"done\":\"" + field.done() + "\"}";
								break;
							default:
						}

						DataOutputStream dos 
							= new DataOutputStream(curr.getOutputStream());
						String reply = "HTTP/1.1 200 OK\r\n" + 
										"Date: Mon, 23 May 2005 22:38:34 GMT\r\n" + 
										"Content-Type: text/html; charset=UTF-8\r\n" + 
										"Content-Encoding: UTF-8\r\n";
						
						reply += "Content-Length: " + replyContent.length() 
									+ "\r\n" + 
									"Server: Java/1.0 (Unix)\r\n" + 
									"ETag: \"3f80f-1b6-3e1cb03b\"\r\n" + 
									"Accept-Ranges: bytes\r\n" + 
									"Connection: close\r\n\r\n" + replyContent;
						// System.out.println(reply);
						dos.writeBytes(reply);
						dos.flush();
						dos.close();
					} else {
						DataOutputStream dos 
							= new DataOutputStream(curr.getOutputStream());
						// System.out.println(reply);
						dos.writeBytes("HTTP/1.1 200 OK\r\n" + 
									"Date: Mon, 23 May 2005 22:38:34 GMT\r\n" + 
									"Content-Type: text/html; charset=UTF-8\r\n" + 
									"Content-Encoding: UTF-8\r\n" + 
									"Content-Length: 0\r\n" + 
									"Server: Java/1.0 (Unix)\r\n" + 
									"ETag: \"3f80f-1b6-3e1cb03b\"\r\n" + 
									"Accept-Ranges: bytes\r\n" + 
									"Connection: close\r\n\r\n"
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