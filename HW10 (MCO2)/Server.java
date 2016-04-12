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
import java.lang.management.*;

public class Server {
	private ServerSocket ss;
	private ProcessThread[] processors;
	private Map field;
	private WaitMonitor wm;
	private ConnectionManager cm;
	private static long tranId = 0;

	private class DeadlockMonitor implements Runnable {
		public void run(){
			while(true){
				ThreadMXBean bean = ManagementFactory.getThreadMXBean();
				long[] threadIds = bean.findDeadlockedThreads(); // Returns null if no threads are deadlocked.

				if (threadIds != null) {
				    ThreadInfo[] infos = bean.getThreadInfo(threadIds);

				    for (ThreadInfo info : infos) {
				        StackTraceElement[] stack = info.getStackTrace();
				        // Log or store stack trace information.
				    }
				}
			}
		}
	}

	public static void main(String[] args) {
		Server s = new Server(args);
		s.start();
	}

	public static synchronized long getTranId() {
		return tranId++;
	}

	public Server(String[] ips) {
		try {
			ss = new ServerSocket(8081);
			cm = ConnectionManager.instance("server");
			cm.connect("action",ips[0]);
			processors = new ProcessThread[20];
			for(int i = 0; i < processors.length; i++ ) {
				processors[i] = new ProcessThread();
				processors[i].start();
			}
 		} catch( IOException ioe ) {
			ioe.printStackTrace();
		}
	}

	public void start() {
		Socket curr;
		int i = 0;
		while(true) {
			try {
				System.out.println("Waiting ACCEPT ME SYFU" + i );
				curr = ss.accept();
				System.out.println("CONNECTED TO " + curr.getInetAddress()
									.getHostAddress() + " " 
									+ new java.util.Date());
				System.out.println("TRY Add");
				processors[i].addSocket(curr);
				System.out.println("END Add");
				i = (i + 1) % processors.length;
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	class ProcessThread extends Thread {
		private ArrayList<Socket> sockets;

		public ProcessThread() {
			sockets = new ArrayList<Socket>();
		}

		public synchronized void addSocket(Socket s) {
			sockets.add(s);
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
			Socket s = sockets.get(0);
			sockets.remove(0);
			process(s);
		}

		public void run() {
			while(true) {
				tryProcess();
			}
		}

		public void process(Socket curr) {
			String ip = curr.getInetAddress().getHostAddress();
			System.out.println("PROCESSING " + ip);
			DataInputStream dis = null;
			try {
				dis = new DataInputStream(curr.getInputStream());
			} catch(Exception e) {
				e.printStackTrace();
			}
			String message = "";
			String post = "";
			char c;
			int end = 0;
			String type = "";
			HashMap<String,String> params = new HashMap<String,String>();
			do {
				try {
					c = (char)dis.readUnsignedByte();
				} catch(Exception e) {
					e.printStackTrace();
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
					// System.out.println("THIS IS THE MESSAGE: " + message);
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
							try {
								post += (char)dis.readUnsignedByte();
							} catch(Exception e) {
								e.printStackTrace();
							}
						}
						try {
							post = URLDecoder.decode(post,"UTF-8");
						} catch(Exception e) {
							e.printStackTrace();
						}
					} else {
						type = "GET";
					}
				} 
			} while( end < 4 );
			try {
				if( message.length() > 0 ) {
					String fn = URLDecoder.decode(
									message.split(" ")[1].substring(1)
										,"UTF-8");

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
						// System.out.println("putting " + par[0] + " -> " 
						// 					+ (par.length == 1 ? "" : par[1]));
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
									ioe.printStackTrace();
								} finally {
									return;
								}
							} else {
								String id = "" + getTranId();
								cm.sendMessage("action","SPAWN " + id
												+ " 0" + (char)30 + (char)4
												,"REPLY " + id,curr);
							}
							break;
						case "POST":
							String dir = params.get("dir");
							String id = params.get("id");
							String tranId = "" + getTranId();
							String sendMessage = "";

							switch(params.get("request")) {
								case "move":
									sendMessage = id + " " + dir;
									System.out.println("TRY move");
									cm.sendMessage("action","MOVE " + tranId
												+ " " + sendMessage.length() 
												+ (char)30 + sendMessage 
												+ (char)4
												,"REPLY " + tranId,curr);
									System.out.println("END move");
									// field.moveSheep(id,dir);
									break;
								case "eat":
									sendMessage = id;
									System.out.println("TRY eat");
									cm.sendMessage("action","EAT " + tranId
												+ " " + sendMessage.length() 
												+ (char)30 + sendMessage 
												+ (char)4
												,"REPLY " + tranId,curr);
									System.out.println("END eat");
									break;
								case "update":
									sendMessage = id;
									System.out.println("TRY update");
									cm.sendMessage("action","UPDATE " + tranId
												+ " " + sendMessage.length() 
												+ (char)30 + sendMessage 
												+ (char)4
												,"REPLY " + tranId,curr);
									System.out.println("END update");
									// field.waitForChanges();
									break;
								default:
							}
							break;
						default:
					}
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
			} catch(IOException ioe) {
				ioe.printStackTrace();
			} finally {
				System.out.println("EXITING PROCESS");
			}
		}
	}

	class WaitMonitor {
		private int num;
		private int nCtr;
		private Map field;

		public WaitMonitor(int num,Map field) {
			this.num = num;
			this.field = field;
			nCtr = 0;
		}

		public synchronized void register() {
			nCtr++;
			if( nCtr == num) {
				field.update();
			}
		}

		public synchronized void unregister() {
			nCtr--;
		}
	}
}