import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ComparatorGenerator {
    private static ArrayList<ArrayList<String>> comparators;

    public static void main(String[] args) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("comparators.txt"));
        int exp = 17;
        
        int n = (int) Math.pow(2, exp);
        int depth = lg(n);
        depth = depth * (depth + 1) / 2;
        comparators = new ArrayList<ArrayList<String>>();

        for(int i = 0; i < depth; i++ ) {
                comparators.add(new ArrayList<String>());
        }

        generateSorter(0,n-1,depth - 1);
        for(int i = 0; i < depth; i++ ) {
                for(String s: comparators.get(i) ) {
                        bw.write(s);
                }
                bw.write("\r\n");
        }

        bw.close();

        int elem = (int)Math.pow(2, exp);
        PrintWriter pw = new PrintWriter(
                            new FileWriter(
                                    new File("nums.sort")));

        for(int i = 0; i < elem; i++ ) {
            pw.print(random());
            if( i < elem - 1) {
                pw.println();
            }
        }

        pw.close();
    }

    public static int lg(int n) {
            return (int)(Math.log(n) / Math.log(2));
    }

    public static void generateSorter(int s,int e, int level) {
            // System.out.println("Sorter: " + s + "-" + e);
            if( e - s == 1 ) {
                    comparators.get(level).add(s + "-" + e + " ");
            } else {
                    generateMerger(s,e,level);
                    int mid = s + (e - s) / 2;
                    int len = e - s + 1;
                    int depth = lg(len);
                    generateSorter(s,mid,level - lg(len));
                    generateSorter(mid + 1,e,level - lg(len));
            }
    }

    public static void generateMerger(int s, int e, int level) {
            // System.out.println("Merger: " + s + "-" + e);
            int mid = s + (e - s) / 2;
            int len = e - s + 1;
            int depth = lg(len);
            int currLevel = level - depth + 1;
            int s2 = s;
            int e2 = e;
            while(s2 < e2) {
                    comparators.get(currLevel).add(s2 + "-" + e2 + " ");
                    s2++; e2--;
            }

            generateBitonicSorter(s,mid,currLevel + 1);
            generateBitonicSorter(mid + 1,e,currLevel + 1);
    }

    public static void generateBitonicSorter(int s, int e, int level) {
            // System.out.println("Bitonic: " + s + "-" + e);
            if( e - s == 1 ) {
                    comparators.get(level).add(s + "-" + e + " ");
            } else {
                    int mid = s + (e - s) / 2;
                    int len = e - s + 1;
                    for(int s2 = s,i = mid + 1; s2 <= mid; s2++,i++ ) {
                            comparators.get(level).add(s2 + "-" + i + " ");
                    }
                    generateBitonicSorter(s,mid,level + 1);
                    generateBitonicSorter(mid + 1,e,level + 1);
            }
    }

    public static int random() {
        return (int)((Math.random() * Math.pow(2,32)) - Math.pow(2,31));
    }
}