/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MarcDominic
 */
public class Client {
    static String name;
    static Socket s;
    static DataInputStream din;
    static DataOutputStream dout;
    static boolean start = false;
    static ServerSocket ss;
	static String ip;
    
    public static void main(String[] args) {
        try{
			ip = args[0];
            s = new Socket(ip, 6969); //server ip and port
            ss = new ServerSocket(6969);
			name = args[1];
            din = new DataInputStream(s.getInputStream());
            dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF(name+"\0");
			dout.close();
			s.close();

            new Thread(new ClientReceiveThread()).start();
            while(!start);
            new Thread(new ClientSendThread()).start();
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static class ClientReceiveThread implements Runnable {

        @Override
        public void run() {
            try {
                while(true){
					Socket s = ss.accept();
					din = new DataInputStream(s.getInputStream());
                    String msgin = din.readUTF();
                    if("OK\0".equals(msgin)){
                        start = true;
                    } else {
                        System.out.println(msgin);
                    }
                } 
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    public static class ClientSendThread implements Runnable {

        @Override
        public void run() {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String msgout="";
            System.out.println("Try typing");
            while(true){
                try {
                    msgout = br.readLine();
					s = new Socket(ip, 6969);
					dout = new DataOutputStream(s.getOutputStream());
                    dout.writeUTF(name+": "+msgout+"\0");
					dout.close();
					s.close();					
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
    }
}
