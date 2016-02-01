import java.util.Scanner;

public class Prime {
    public static volatile boolean isPrime = true;
    // public static long num = 9182237390125665823L;
    public static long num = 9576890767L;
    public static long sqrt = (long) Math.sqrt(num);
    public static long nThreads;
    public static volatile long threadCtr = 0;

    public static void main(String args[]) {
        System.out.print("ENTER: ");
        Scanner sc = new Scanner(System.in);
        int exp = sc.nextInt();
        nThreads = (long) Math.pow(2, exp);
        sc.close();
        long ave = 0;
        long aveM = 0;
        
        for(int x = 0; x < 5; x++ ) {
            threadCtr = 0;
            long time     = System.currentTimeMillis();
            long range    = sqrt / nThreads;
            long curStart = 2;
            long curEnd   = range;
            
            for(int i=0; i<nThreads; i++){
                new Thread(new Checker(curStart, curEnd)).start();
                curStart = curEnd+1;
                curEnd += range;
            }
            while(true){
                if( threadCtr == nThreads) {
                    break;
                }
            }
            System.out.println(isPrime);

            Runtime rt    = Runtime.getRuntime();
            double used = (rt.totalMemory() - rt.freeMemory()) / 1024.0 / 1024.0;
            System.out.println("used memory: " + used + " MB");
            aveM += used;

            time = System.currentTimeMillis() - time;
            System.out.println((time / 1000.0) + " seconds\n");
            ave += time;
        }
        System.out.println("Average Memory: " + aveM / 5.0 + " MB");
        System.out.println("Average Time: " + (ave / 5000.0 ) + " seconds");
    }
    
    public static synchronized void update(boolean result) {
        Prime.isPrime = Prime.isPrime && result;
        Prime.threadCtr++;
    }

    static class Checker implements Runnable {
        private long start;
        private long end;

        public Checker(long start, long end) {
            this.start = start;
            this.end = end;
        }

        public void run() {
            boolean tempIsPrime = true;
            for(long i = start; i <= end; i++){
                if(num % i == 0){
                    tempIsPrime = false;
                    break;
                }
            }

            update(tempIsPrime);
        }
    }
}

