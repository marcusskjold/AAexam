package sorting;

import java.util.Arrays;
import java.util.Stack;

public class BottomUpMergeSort {
    private BottomUpMergeSort() {}


    //TODO: maybe T extends comparable necessary here?
    //TODO: could bit-operations and the like be utilized here, e.g. in modelling the stack, lengths etc.?
    private static  int sort(Comparable[] a, Comparable[] aux) {
        /*Creates empty stack for runs
        For now runs are length 2 int-arrays where int[0] is the leftmost index in array a,
        and int[1] is the length of the run */
        Stack<int[]> runs = new Stack<>();
        //find array length
        int n = a.length;
    
        //If no elements in array, no need to sort
        if(n==0) return 0;
    
        //iterate through array 'a' and compute runs as per Task 5
        for(int i=0; i<n; i++) {
            
            //Create run of size 1 from next element in array:
            int[] latestRun = new int[]{i,1};
            //debug:
            System.out.println("looking at latest run: " + Arrays.toString(latestRun));
            
    
            //While more runs in stack and length of next run is equal to that of current run
            while(!runs.isEmpty() && runs.peek()[1] == latestRun[1]) //TODO: operator order fine?
            {    
                    //pop next run
                    int[] nextRun = runs.pop();
                    //debug:
                    System.out.println("comparing with next run: " + Arrays.toString(nextRun));
    
                    //assign lo, mid and hi for merged of the two runs and perform merge
                    int lo = nextRun[0];
                    int mid = lo + nextRun[1] - 1;
                    int hi = latestRun[0] + latestRun[1] - 1;
                    Merge.merge(a, aux, lo, mid, hi);
    
                    //make merged run the latest run
                    latestRun = new int[]{lo, hi - lo + 1};
                    //debug:
                    System.out.println("new latest run: " + Arrays.toString(latestRun));
                  
            }
            //when latest merged with all other runs, or next run has different length, push to stack
            runs.push(latestRun);
    
            
        }
            //Then pop topmost element and merge it with next one, till just one run is left
            int[] latestRun = runs.pop();
            while(runs.size()>0) {
                int[] nextRun = runs.pop();
    
                //assign lo, mid and hi for merged of the two runs and perform merge
                int lo = nextRun[0];
                int mid = lo + nextRun[1] - 1;
                int hi = latestRun[0] + latestRun[1] - 1;
                Merge.merge(a, aux, lo, mid, hi);
    
                //make merged run the latest run
                latestRun = new int[]{lo, hi - lo + 1};
        }
        //debug:
        System.out.println("Resulting array: " + Arrays.toString(a));
    
        //TODO: handle counting compares
        return 0;
    }


    /**
     * Rearranges the array in ascending order, using the natural order
     * @param a the array to be sorted
     * @return the number of compares performed during the sort
     */
    public static int sort(Comparable[] a) {
        Comparable[] aux = new Comparable[a.length];
        int compares = sort(a, aux);
        assert SortUtils.isSorted(a);
        return compares;
    }



    
}






///Scratches from draft:
//int[] secondRun = runs.pop();
//                int secondRunStart = secondRun[0];
//                int secondRunLength = secondRun[1];
//                if(latestRunLength == secondRunLength) {
//                    //defines new lo
//                    int lo = latestRun[0];
//                    int hi = lo + latestRunLength + 1;
//                    Merge.merge(a, aux, latestRun[0], i, hi)
//                    runs.add(new int[]{latestRun[0],latestRunLength+secondRunLength});
//                } 
//                //if two top is not equal in length, put them back together with run containing next element
//                else {
//                    runs.add(secondRun);
//                    runs.add(latestRun);
//                    runs.add(new int[]{i,1});
//                }
//