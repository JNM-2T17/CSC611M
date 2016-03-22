import java.io.*;
import java.math.BigInteger;
import java.util.Scanner;

public class Driver {
    public static boolean isPrime = true;
    public static BigInteger num = new BigInteger("9182237390125665823");
    public static BigInteger sqrt = sqrt(num);
    static long nThreads;
    static volatile long threadCtr = 0;
    
	public static void main(String[] args) throws IOException{
		System.out.print("ENTER: ");
        Scanner sc = new Scanner(System.in);
        int exp = sc.nextInt();
        nThreads = (long) Math.pow(2, exp);
        sc.close();
        long ave = 0;
        long aveM = 0;
        Process[] procs = new Process[(int)nThreads];
        
        for(int x = 0; x < 5; x++ ) {
            threadCtr = 0;
            long time     = System.currentTimeMillis();
            BigInteger range    = sqrt.divide(new BigInteger(nThreads+""));
            BigInteger curStart = new BigInteger("2");
            BigInteger curEnd   = range;
            Runtime rt    = Runtime.getRuntime();
            double used   = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
            
            for(int i=0; i<nThreads; i++){
            //    new Thread(new Checker(curStart, curEnd)).start();
            	procs[i] = Runtime.getRuntime().exec("java SubPrime " + num + " "+curStart+" "+curEnd);
                curStart = curEnd.add(BigInteger.ONE);
                curEnd = curEnd.add(range);
            }
            
            for(int i = 0; i < nThreads; i++) {
            	try{
            		rocs[i].waitFor();
	            } catch(InterruptedException ie) {
	            	ie.printStackTrace();
	            }
            }

            
            time = System.currentTimeMillis() - time;
            System.out.println((time / 1000.0) + " seconds\n");
            ave += time;
        }
        System.out.println("Average Time: " + (ave / 5000.0 ) + " seconds");
	}
	
	public static BigInteger sqrt(BigInteger x) {
	    BigInteger div = BigInteger.ZERO.setBit(x.bitLength()/2);
	    BigInteger div2 = div;
	    // Loop until we hit the same value twice in a row, or wind
	    // up alternating.
	    for(;;) {
	        BigInteger y = div.add(x.divide(div)).shiftRight(1);
	        if (y.equals(div) || y.equals(div2))
	            return y;
	        div2 = div;
	        div = y;
	    }
	}
}
