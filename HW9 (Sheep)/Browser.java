import java.util.*;
import java.net.*;
import java.io.*;


public class Browser {
	private Scanner sc;
	private String[] requestParams;
	public static void main(String[] args) {
		Browser b = new Browser(args);
		try {
			b.start();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public Browser(String[] params) {
		sc = new Scanner(System.in);
		requestParams = params;
	}

	public void start() throws Exception {
		int asdf = 0;
		int nRequests = Integer.parseInt(requestParams[2]);
		while(asdf<nRequests) {
			asdf++;
			String request = "GET /sheep.html HTTP/1.1\r\n"
				+"Host: localhost:8081\r\n"
				+"Connection: keep-alive\r\n"
				+"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\r\n"
				+"Upgrade-Insecure-Requests: 1\r\n"
				+"User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36\r\n"
				+"Accept-Encoding: gzip, deflate, sdch\r\n"
				+"Accept-Language: en-US,en;q=0.8\r\n"
				+"If-None-Match: \"3f80f-1b6-3e1cb03b\"\r\n";
			
			final int port = Integer.parseInt(requestParams[1]);
			final Socket s = new Socket(requestParams[0],port);

			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeBytes(request);
			(new Thread(new Runnable() {
				public void run() {
					try {
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
								System.out.println(new String(post));
							} 
						} while( end < 4 );
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			})).start();
		}
	}
}