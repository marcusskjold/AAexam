package sorting;

//import java.util.Stack;

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
        //int compares = sort(a, aux);
        int compares = msort(a, aux);
        assert Util.isSorted(a);
        return compares;
    }


    /** By the book implementation */
    public static <T extends Comparable<? super T>> int bottomupsort(T[] a, T[] aux) {
        int compares = 0, n = a.length;
        if(n <= 1) return compares;

        // Represents the stack of runs as an int, with a set bit corresponding to the length of a run.
        // The rightmost bit then represents the top of the stack
        // E.g. '...1101' represents first a run of length 1 in the top, then a length 4 run, then an 8 long run
        int runStack = 0;

        for(int i = 0; i < n; i++) {                          // Iterate through array and compute runs
            int runLength = 1;
            while((runStack & runLength) != 0) {
                int lo    = runStack - runLength;
                int mid   = runStack - 1;
                int hi    = runStack + runLength - 1;
                //System.out.printf("Now merging: a[%d .. %d] + a [%d + 1 .. %d]", lo, mid, mid+1, hi); // Debug print
                compares += Merge.merge(a, aux, lo, mid, hi); // merge current run with next run (and count compares)
                runStack  = runStack - runLength;             // remove the next (now merged) run from stack
                runLength = runLength << 1;                   // double length of current run
            }
            runStack += runLength; // When not a next run of same length, put current run in stack.
        }
        assert (runStack == n);  // All elements in array are now computed as runs in stack

        int runLength = Integer.lowestOneBit(runStack); //pop the top run of the stack
        runStack     -= runLength;
        final int hi  = n - 1; // define final index for merges as the last index in the array.
        
        while(runStack != 0) {
            //find next run in stack to merge with (can only be one for each length pr the stack invariant)
            runLength = Integer.lowestOneBit(runStack);
            int lo = runStack - runLength;
            int mid = runStack - 1;
            //System.out.println("Now merging: a[" + lo + " .. " + mid + "] + a [" + mid + " + 1 .. " + hi + "]");
            compares += Merge.merge(a, aux, lo, mid, hi); //merge top run with next run (and count compares):
            runStack -= runLength;           //remove the next (now merged) run from stack
        }
        return compares;
    }

    /** A slightly more efficient implementation of classicBottomUpSort. */
    public static <T extends Comparable<? super T>> int msort(T[] a, T[] aux) {
        final int n = a.length;
        if (n <= 1) return 0;
        int compares = 0;
        for (int i = 1; i < n; i += 2) {
            //if (i >= n) i = n-1;      // TODO: Check if this is necessary, find a breaking test
            int stack = i;
            for (int length = 1; (length & stack) != 0; length *= 2) {
                int lo    = stack - length;
                int mid   = stack - 1;
                int hi    = stack + length - 1;
                compares += Merge.merge(a, aux, lo, mid, hi);
                stack     = stack - length;
            }
        }

        int length = Integer.lowestOneBit(n);
        int hi     = n - 1;
        
        for (int stack = n - length; stack != 0; stack -= length) {
            length    = Integer.lowestOneBit(stack);
            int lo    = stack - length;
            int mid   = stack - 1;
            compares += Merge.merge(a, aux, lo, mid, hi);
        }
        return compares;
    }
    
    /** Alternative, even more iterative variation */
    public static <T extends Comparable<? super T>> int itersort(T[] a, T[] aux) {
        final int n = a.length;
        if (n <= 1) return 0;
        int compares = 0;

        for (int i = 2; i <= n; i *= 2 ) {
            System.out.println(i);
            for (int j = i; j <= n; j += i) {
                int lo = j - i;
                int mid = lo + (i / 2) -1;
                int hi = j - 1;
                compares += Merge.merge(a, aux, lo, mid, hi);
            }
        }

        int length   = Integer.lowestOneBit(n);
        final int hi = n - 1;
        
        for (int stack = n - length; stack != 0; stack -= length) {
            length = Integer.lowestOneBit(stack);
            int lo    = stack - length;
            int mid   = stack - 1;
            compares += Merge.merge(a, aux, lo, mid, hi);
        }
        return compares;
    }

//------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------

/* NONFUNCTIONAL!
    //Version of sort that uses a stack data-structure, rather than a binary number
    //Have only been checked by a few tests
    public  static <T extends Comparable<? super T>> int _TESTINGsortStack(T[] a, T[] aux) {
        // Creates empty stack for runs
        // For now runs are length 2 int-arrays where int[0] is the leftmost index in array a,
        // and int[1] is the length of the run
        Stack<int[]> runs = new Stack<>();
        int n = a.length;
        if(n==0) return 0;
    
        for(int i=0; i<n; i++) {
            //Create run of size 1 from next element in array:
            int[] latestRun = new int[]{i,1};
            //While more runs in stack and length of next run is equal to that of current run
            while(!runs.isEmpty() && runs.peek()[1] == latestRun[1]) { //TODO: operator order fine?
                int[] nextRun = runs.pop();
                int lo = nextRun[0];
                int mid = lo + nextRun[1] - 1;
                int hi = latestRun[0] + latestRun[1] - 1;
                Merge.merge(a, aux, lo, mid, hi);
                latestRun = new int[]{lo, hi - lo + 1};
            }
            runs.push(latestRun); //when latest merged with all other runs, or next run has different length, push to stack
        }
            //Then pop topmost element and merge it with next one, till just one run is left
            int[] latestRun = runs.pop();
            while(runs.size()>0) {
                int[] nextRun = runs.pop();
                int lo = nextRun[0];
                int mid = lo + nextRun[1] - 1;
                int hi = latestRun[0] + latestRun[1] - 1;
                Merge.merge(a, aux, lo, mid, hi);
                latestRun = new int[]{lo, hi - lo + 1};
        }
        return 0;
    }
*/
}

