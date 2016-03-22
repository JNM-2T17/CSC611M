import java.util.Scanner;
import java.net.*;
import java.io.*;


public class LoadTester {
	private static volatile long time = 0;
	private static volatile long count = 0;

	private Scanner sc;
	public static void main(String[] args) {
		LoadTester b = new LoadTester();
		try {
			b.start();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static synchronized void updateAvg(long time) {
		LoadTester.time += time;
		count++;
		System.out.println("Avg Time: " + (LoadTester.time / count / 1000.0) 
							+ " from " + count + " connections");
	}

	public LoadTester() {
		sc = new Scanner(System.in);
	}

	public void start() {
		while(true) {
			int success = 0;
			try {
				String[] requestParams = getInput();
				for(int i = 0; i < 75854; i++ ) {
					System.out.println(i);
					try {
						processRequest(requestParams);
						success++;
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
				System.out.println(success + " / 75854 successful requests");
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void processRequest(String[] requestParams) throws Exception {
		final String request = "GET " + requestParams[2] + " HTTP/1.1\n" + 
							"Host: 192.168.8.105\n" + 
							"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\n" + 
							"Accept-Language: en-us\n" + 
							"Connection: keep-alive\n" + 
							"Accept-Encoding: gzip, deflate\n" + 
							"User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_4) AppleWebKit/600.7.12 (KHTML, like Gecko) Version/8.0.7 Safari/600.7.12\r\n\r\n";
		final int port = Integer.parseInt(requestParams[1]);
		final Socket s = new Socket(requestParams[0],port);

		(new Thread(new Runnable() {
			public void run() {
				try {
					DataOutputStream dos = new DataOutputStream(s.getOutputStream());
					long time = System.currentTimeMillis();
					dos.writeBytes(request);
					Socket curr = s;
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
						if( end == 4 ) {
							System.out.println("THIS IS THE MESSAGE: " + message);
						
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
							byte[] post = new byte[length];
							int current = 0;
							int read;
							do {
								read = dis.read(post,current,length - current);
								if( read >= 0 ) {
									current += read;
								}
							} while( read != -1 && current < length);
							// post = URLDecoder.decode(post,"UTF-8");
							// System.out.println(new String(post));
							time = System.currentTimeMillis() - time;
							LoadTester.updateAvg(time);
							System.out.println("OK");
						} 
					} while( end < 4 );
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		})).start();
	}

	public String[] getInput() {
		String req = sc.nextLine();
		int slashIndex = req.indexOf("/");
		String[] ret = new String[3];
		if( slashIndex != -1 ) {
			ret[2] = req.substring(slashIndex);
			req = req.substring(0,slashIndex);
		} else {
			ret[2] = "/";
		}

		int colonIndex = req.indexOf(":");
		if( colonIndex != -1 ) {
			ret[1] = req.substring(colonIndex + 1);
			ret[0] = req.substring(0,colonIndex);
		} else {
			ret[1] = "80";
			ret[0] = req;
		}

		return ret;
	}
}