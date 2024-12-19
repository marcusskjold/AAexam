package sorting;

import java.util.Stack;

public class BottomUpMergeSort {
    private BottomUpMergeSort() {}

    /**
     * Rearranges the array in ascending order, using the natural order
     * Keeps a stack of computed runs. 
     * When a new run of size one is added, it is merged with the next 
     * run in the stack till the next run has a different length than the resulting run
     * Afterwards, merges all runs together, one by one, starting at the top
     * of the stack.
     * @param a the array to be sorted
     * @return the number of compares performed during the sort
     */
    public static <T extends Comparable<? super T>> int sort(T[] a) {
        T[] aux = a.clone();
        int compares = sort(a, aux);
        assert Util.isSorted(a);
        return compares;
    }


    private static <T extends Comparable<? super T>> int sort(T[] a, T[] aux) {
        int compares = 0, n = a.length;
        if(n <= 1) return compares;

        //Represents the stack of runs as an int, with a set bit corresponding to the length of a run.
        //The rightmost bit then represents the top of the stack
        //For instance ...1101 would correspond to a run of length 1 in the top,
        //followed by a run of length 4, and a run of length 8
        //This utilizes the fact that the run lengths decreases by a factor of 2, when going up the stack
        int runStack = 0;

        //Iterate through array and compute runs
        for(int i=0;i<n;i++) {
            //sets length of next run to 1
            int runLength = 1;
            //Iterate through bits of the stack (leftshifting runLength for each succes)
            //till next bit not set as well.
            //1: While next element in stack is same length as current element
            while((runStack & runLength) != 0) {
                //define initial index for run to merge with (the lengths of the runs minus the one to merge with)
                int lo = runStack - runLength;
                //compute mid as the final index of the next run in stack (as usual)
                int mid = runStack - 1;
                //define final index as the sum of lengths and current run minus 1
                int hi = runStack + runLength - 1;
                ////debug
                //System.out.println("Now merging: a[" + lo + " .. " + mid + "] + a [" + mid + " + 1 .. " + hi + "]");
                //merge current run with next run (and count compares):
                compares += Merge.merge(a, aux, lo, mid, hi);
                //remove the next (now merged) run from stack (by bitwise AND with reverse of runLength)
                runStack = runStack & (~runLength);
                //double length of current run:
                runLength = runLength << 1;
            }
            //when not a next run of same length, put current run in stack (using bitwise or):
            runStack = runStack | runLength;
        }

        //make sure all elements in array are now computed as runs in stack (length of runs equal to length of array)
        assert(runStack==n);

        //When stack iterated through, merge rest of runs in stack till just one run:
        //pop the top run of the stack
        int runLength = Integer.lowestOneBit(runStack);
        runStack = runStack & (~runLength);

        //define final index for merges as the last index in the array (since upmost merged, this will stay the same)
        int hi = n - 1;
        
        ////debug
        //System.out.println("Finishing up stack:");
        //System.out.println("runStack: " + Integer.toBinaryString(runStack));
        //System.out.println("runLength: " + runLength);
        //while still elements in stack:
        while(runStack != 0) {
            //find next run in stack to merge with (can only be one for each length pr the stack invariant)
            runLength = Integer.lowestOneBit(runStack);
                //define lower bound
                int lo = runStack - runLength;
                //compute mid (as usual)
                int mid = runStack - 1;
                    ////debug
                    //System.out.println("Now merging: a[" + lo + " .. " + mid + "] + a [" + mid + " + 1 .. " + hi + "]");
                //merge top run with next run (and count compares):
                compares += Merge.merge(a, aux, lo, mid, hi);
                //remove the next (now merged) run from stack
                runStack = runStack & (~runLength);   
        }
        //////debug
        //System.out.println(Arrays.toString(a));

        return compares;
    }



//------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------

    //Version of sort that uses a stack data-structure, rather than a binary number
    //Have only been checked by a few tests
    private static <T extends Comparable<? super T>> int sortStack(T[] a, T[] aux) {
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
            //System.out.println("looking at latest run: " + Arrays.toString(latestRun));
            
    
            //While more runs in stack and length of next run is equal to that of current run
            while(!runs.isEmpty() && runs.peek()[1] == latestRun[1]) //TODO: operator order fine?
            {    
                    //pop next run
                    int[] nextRun = runs.pop();
                    //debug:
                    //System.out.println("comparing with next run: " + Arrays.toString(nextRun));
    
                    //assign lo, mid and hi for merged of the two runs and perform merge
                    int lo = nextRun[0];
                    int mid = lo + nextRun[1] - 1;
                    int hi = latestRun[0] + latestRun[1] - 1;
                    Merge.merge(a, aux, lo, mid, hi);
    
                    //make merged run the latest run
                    latestRun = new int[]{lo, hi - lo + 1};
                    //debug:
                    //System.out.println("new latest run: " + Arrays.toString(latestRun));
                  
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
        //System.out.println("Resulting array: " + Arrays.toString(a));
    
        return 0;
    }

}
