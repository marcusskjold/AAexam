package sorting;

public class BottomUpMergeSortCutoff {
    private BottomUpMergeSortCutoff() {}

    /**
     * Rearranges the array in ascending order, using the natural order
     * Works iteratively through by computing runs of next {@code c} elements in {@code a} 
     * (or less when not enough elements left in array), and merges them
     * @param a the array to be sorted
     * @param c the initial length of runs to merge, when enough left; must be at least 1
     * @return the number of compares performed during the sort
     * @throws IllegalArgumentException if {@code c} is less than 1
     */
    public static <T extends Comparable<? super T>> int sort(T[] a, int c) {
        if (c < 1) throw new IllegalArgumentException("Cutoff value must be at least 1.");
        T[] aux = a.clone();
        int compares = sort(a, aux, c);
        assert Util.isSorted(a);
        return compares;
    }

    private static <T extends Comparable<? super T>> int sort(T[] a, T[] aux, int c) {
        int compares = 0;
        int n = a.length;
        if(n<=1) return compares;
        int runStack = 0;

        for(int i=0; i+c-1<n; i+=c) {
            //iterates through array in intervals of length c, run by run
            //sets length of next run to c^1
            int runLength = 1;
            //Create new run of length c with insertion-sort (and count compares)
            compares += InsertionSort.sort(a, i, i+c-1);

            while((runStack & runLength) != 0) {
                //find interval to merge in 'a' (scaled by c)
                int lo = c*(runStack - runLength);
                int mid = c*(runStack) - 1;
                int hi = c*(runStack + runLength) - 1;
                //System.out.println("Now merging: a[" + lo + " .. " + mid + "] + a [" + mid + " + 1 .. " + hi + "]");
                compares += Merge.merge(a, aux, lo, mid, hi);
                runStack = runStack & (~runLength);
                runLength = runLength << 1;
            }
            runStack = runStack | runLength;
        }
        
        //make sure all elements (up to a residue smaller than c) in array are now computed as runs in stack
        assert(c*runStack + c > n);
        int runLength = Integer.lowestOneBit(runStack); //go to top run on stack
        int hi = n - 1;
        //Sort any remaining elements not in the stack (upscaled by c):
        if (c * runStack < n) compares += InsertionSort.sort(a, c * runStack, hi); 
        else runStack = runStack & (~runLength); //Else, If all elements already in stack of runs, remove top run
            
        while(runStack != 0) {
            runLength = Integer.lowestOneBit(runStack); //go to next run in stack
            int lo    = c*(runStack - runLength);       // define indexes for merge, scaled by c, and merge
            int mid   = c*(runStack) - 1;
            //System.out.println("Now merging: a[" + lo + " .. " + mid + "] + a [" + mid + " + 1 .. " + hi + "]");
            compares += Merge.merge(a, aux, lo, mid, hi);
            runStack  = runStack & (~runLength);
        }
        return compares;
    }
}
