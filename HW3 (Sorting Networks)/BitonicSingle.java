
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class BitonicSingle {
    static int exp = 17;
    static int n = (int) Math.pow(2, exp);
    static int[] nums = new int[n];
    static Comparator[][] comparators;
    static int threadCount;
    
    public static void main(String[] args) throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader("nums.sort"));
        for(int i=0; i<n; i++){
            nums[i] = new Integer(br.readLine());
        }
        br.close();
        
        boolean isSorted = true;
        for(int i=0; i<n-1; i++){
            if(nums[i] > nums[i+1]){
                isSorted = false;
                break;
            }
        }
        
        System.out.println("Sorted: "+isSorted);
        
        int depth = getTriangular(exp);
        comparators = new Comparator[depth][n/2];
        
        br = new BufferedReader(new FileReader("comparators.txt"));
        for(int i=0; i<depth; i++){
            String layer = br.readLine();
            String[] split = layer.split(" ");
            for(int j=0; j<n/2; j++){
//                new Thread(new Comparator(split[j])).start();
                new Comparator(split[j]).compare();
            }
        }
        
        isSorted = true;
        for(int i=0; i<n-1; i++){
            if(nums[i] > nums[i+1]){
                isSorted = false;
                break;
            }
        }
        
        System.out.println("Sorted: "+isSorted);
    }

    private static int getTriangular(int exp) {
        return exp * (exp + 1) / 2;
    }
    
    static class Comparator implements Runnable {
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
        
        @Override
        public void run() {
            this.compare();
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
}
