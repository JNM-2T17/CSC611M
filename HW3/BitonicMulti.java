
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class BitonicMulti {
    static int exp = 17;
    static int n = (int) Math.pow(2, exp);
    static int[] nums = new int[n];
    static int threadCount = (int) Math.pow(2, 2);
    static int depth = getTriangular(exp);
    static volatile int threadsDoneInLayer;
    
    public static void main(String[] args) throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader("nums.sort"));
        for(int i=0; i<n; i++){
            nums[i] = new Integer(br.readLine());
        }
        br.close();
        
        float startTime = System.currentTimeMillis();
        br = new BufferedReader(new FileReader("comparators.txt"));
        for(int i=0; i<depth; i++){
            String layer = br.readLine();
            String[] split = layer.split(" ");
            threadsDoneInLayer = 0;
            
            int sizePerThread = split.length/threadCount;
            int start = 0;
            int end = sizePerThread;
            for(int j=0; j<threadCount; j++){
                new Thread(new SortingThread(split, start, end)).start();
                start = end;
                end = end+sizePerThread;
            }
            
            while(threadsDoneInLayer!=threadCount);
        }
        float endTime = System.currentTimeMillis();
        
        boolean isSorted = true;
        for(int i=0; i<n-1; i++){
            if(nums[i] > nums[i+1]){
                isSorted = false;
                break;
            }
        }
        
        System.out.println("Sorted: "+isSorted);
//        System.out.println("Total time: "+((endTime-startTime)*1000)+"s");
//        System.out.println(startTime+" "+endTime);
    }

    private static int getTriangular(int exp) {
        int total = 0;
        for(int i=1; i<=exp; i++) total+=i;
        return total;
    }
    
    static class Comparator {
        int a;
        int b;
        
        public Comparator(String c){
            String[] split = c.split("-");
            a = new Integer(split[0]);
            b = new Integer(split[1]);
        }
        
        public Comparator(int a, int b){
            this.a = a;
            this.b = b;
        }
        
        public void compare(){
            if(nums[a] > nums[b]){
                int temp = nums[a];
                nums[a] = nums[b];
                nums[b] = temp;
            }
        }
        
        public String toString(){
            return a+"-"+b;
        }

    }
    
    static class SortingThread implements Runnable {
        String[] split;
        int start;
        int end;
        
        public SortingThread(String[] split, int start, int end){
            this.split = split;
            this.start = start;
            this.end = end;
        }
        
        @Override
        public void run() {
            for(int i=start; i<end; i++){
                new Comparator(split[i]).compare();
            }
            update();
        }
        
    }
    
    static synchronized void update(){
        threadsDoneInLayer++;
    }
}
