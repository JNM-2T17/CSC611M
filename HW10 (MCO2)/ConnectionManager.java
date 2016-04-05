import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.EOFException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ConnectionManager {
	private static ConnectionManager instance;

	private Map field;
	private Updatable updatable;
	private String iden;
	private HashMap<String,Socket> sockets;
	private HashMap<String,DataOutputStream> doss;
	private HashMap<String,Integer> flushes;
	private HashMap<String,Flusher> flushThreads;
	private HashMap<String,Socket> actions;
	private Receiver r;

	private ConnectionManager(String iden) {
		this.iden = iden;
		sockets = new HashMap<String,Socket>();
		doss = new HashMap<String,DataOutputStream>();
		flushes = new HashMap<String,Integer>();
		flushThreads = new HashMap<String,Flusher>();
		actions = new HashMap<String,Socket>();
		switch(iden) {
			case "action":
				r = new Receiver(8082);
				r.start();
				break;
			case "update":
				r = new Receiver(8083);
				r.start();
				break;	
			default:
		}
	}

	public void setField(Map field) {
		this.field = field;
	}

	public void setUpdatable(Updatable updatable) {
		this.updatable = updatable;
	}

	public static synchronized ConnectionManager instance(String iden) {
		if( instance == null ) {
			instance = new ConnectionManager(iden);
		}
		return instance;
	}

	public static synchronized ConnectionManager instance() {
		return instance;
	}

	public void connect(String tag, String ipAddress) {
		while(!isConnected(tag)) {
			try {
				Socket s = null;
				switch(tag) {
					case "server":
						s = new Socket(ipAddress,8081);
						break;
					case "action":
						s = new Socket(ipAddress,8082);
						break;
					case "update":
						s = new Socket(ipAddress,8083);
						break;	
					default:
				}	
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				dos.writeBytes("CONNECT " + iden + (char)4);

				DataInputStream dis = new DataInputStream(s.getInputStream());
				String message = "";
				char c;
				do {
					c = (char)dis.readUnsignedByte();
					if( c != 4 ) {
						message += c;
					}
				} while(c != 4);
				System.out.println(message + " from " + s.getInetAddress().getHostAddress());
				if( message.equals("OK") ) {
					register(tag,s);
				}
			} catch( Exception e) {
				System.out.println("Connection Failed");
			}
		}
	}

	class Flusher extends Thread {
		private String tag;

		public Flusher(String tag) {
			this.tag = tag;
		}

		public void run() {
			while(true) {
				try {
					if( flushes.get(tag) > 0 ) {
						doss.get(tag).flush();
						flushes.put(tag,0);
					}
					Thread.sleep(100);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized void register(String tag, Socket registree) {
		sockets.put(tag,registree);
		try {
			doss.put(tag,new DataOutputStream(registree.getOutputStream()));
		} catch(Exception e) {
			e.printStackTrace();
		}
		flushes.put(tag,0);
		Flusher f = new Flusher(tag);
		flushThreads.put(tag,f);
		f.start();
		(new Listener(tag,registree)).start();
	}

	public synchronized void unregister(String tag) {
		sockets.remove(tag);
		doss.remove(tag);
		flushes.remove(tag);
		flushThreads.get(tag).stop();
		flushThreads.remove(tag);
	}

	public synchronized boolean sendMessage(String tag, String message) {
		Socket s = sockets.get(tag);
		if( s != null ) {
			System.out.println("SENDING " + tag + " " + message + " to " + s);
			try {
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				dos.writeBytes(message);
				flushes.put(tag,flushes.get(tag) + 1);
				return true;
			} catch( Exception e ) {
				e.printStackTrace();	
			}
		} 
		return false;
	}

	public synchronized boolean sendMessage(String tag, String message, String replyHeader,Socket s) {
		Socket temp = sockets.get(tag);
		if( temp != null ) {
			System.out.println("SENDING " + tag + " " + message);
			try {
				DataOutputStream dos = new DataOutputStream(temp.getOutputStream());
				dos.writeBytes(message);
				dos.flush();
				System.out.println("Waiting for " + replyHeader);
				actions.put(tag + replyHeader,s);
				return true;
			} catch( Exception e ) {
				e.printStackTrace();	
			}
		} 
		return false;
	}

	public synchronized boolean isConnected(String tag) {
		return sockets.get(tag) != null;
	}

	public synchronized void processMessage(final String tag, String header
												, final String id
												, final String message) {
		String replyContent = "";
		Sheep s = null;
		System.out.println(tag + " " + header + " " + id + " " + message);
		String sheepId = "";
		switch(header) {
			case "EAT":
				boolean done = field.eat(message);
				s = field.sheep(message);
				
				switch(iden) {
					case "action":
						Action a = new EatAction(field, s, id, message);
						updatable.schedule(a);
						break;
					case "update":
						System.out.println("UPDATING");
						updatable.update();
						break;
				}
				// System.out.println(replyContent);
				break;
			case "SPAWN":
				switch(iden) {
					case "action":
						sheepId = field.spawnSheep();
						s = field.sheep(sheepId);
						Action a = new SpawnAction(field, s, id, sheepId);
						updatable.schedule(a);
						break;
					case "update":
						String[] split = message.split(" ");
						sheepId = field.spawnSheep(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
						s = field.sheep(sheepId);
						System.out.println("UPDATING");
						updatable.update();
						break;
				}
				// System.out.println(replyContent);
				break;
			case "MOVE":
				String[] args = message.split(" ");
				sheepId = args[0];
				String dir = args[1];
				s = field.sheep(sheepId);
				field.moveSheep(sheepId,dir);
				
				switch(iden) {
					case "action":
						Action a = new MoveAction(field, s, id, message, sheepId);
						updatable.schedule(a);
						break;
					case "update":
						System.out.println("UPDATING");
						updatable.update();
						break;
				}
				break;
			case "REPLY":
				try {
					Socket rep = actions.get(tag + header + " " + id);
					if( rep != null ) {
						DataOutputStream dos = new DataOutputStream(rep.getOutputStream());
						dos.writeBytes(message);
						dos.close();
						rep.close();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				break;
			case "UPDATE":
				updatable.schedule(id,message);
				break;
			default:
		}
	}

	private class Receiver extends Thread {
		private ServerSocket ss;
		
		public Receiver(int port) {
			try {
				ss = new ServerSocket(port);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		public synchronized void receive() {
			while(true) {
				try {
					Socket s = ss.accept();
					System.out.println("FROM " + s.getInetAddress().getHostAddress());
					String message = "";
					DataInputStream dis = new DataInputStream(s.getInputStream());
					char c;
					do {
						c = (char)dis.readUnsignedByte();
						if( c != 4 ) {
							message += c;
						}
					} while(c != 4);
					String ip = s.getInetAddress().getHostAddress();
					System.out.println(message + " from " + ip);
					String[] segments = message.split(" ");
					String tag = segments[1];
					if( segments[0].equals("CONNECT")) {
						DataOutputStream dos 
							= new DataOutputStream(s.getOutputStream());
						dos.writeBytes("OK" + (char)4);
						register(tag,s);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}

		public void run() {
			receive();
		}
	}

	private class Listener extends Thread {
		private String tag;
		private DataInputStream dis;

		public Listener(String tag,Socket s) {
			this.tag = tag;
			try {
				dis = new DataInputStream(s.getInputStream());
			} catch( Exception e ) {
				e.printStackTrace();
			}
		}

		public void run() {
			while(true) {
				try {
					String header = "";
					char c;
					do {
						try {
							c = (char)dis.readUnsignedByte();
							if( c != 30 ) {
								header += c;
							}
						} catch(Exception e ) {
							unregister(tag);
							return;
						}
					} while(c != 30);
					// System.out.println(header);
					//header is type<space>id<space>length
					final String[] parts = header.split(" ");
					int length = Integer.parseInt(parts[2]);
					byte[] data = new byte[length];

					int curr = 0;
					do {
						int read = dis.read(data,curr,length - curr);
						if( read != -1 ) {
							curr += read;
						} else {
							break;
						}
					} while( curr < length );
					final String message = new String(data);
					dis.readUnsignedByte(); //throw terminator
					(new Thread(new Runnable() {
						public void run() {
							processMessage(tag,parts[0],parts[1],message);
						}
					})).start();
				} catch( Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}