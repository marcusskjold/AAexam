package sorting;

import java.util.Arrays;

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
    public static int sort(Comparable[] a, int c) {
        if (c < 1) {
            throw new IllegalArgumentException("Cutoff value must be at least 1.");
        }
        Comparable[] aux = new Comparable[a.length];
        int compares = sort(a, aux, c);
        assert SortUtils.isSorted(a);
        return compares;
    }


    private static int sort(Comparable[] a, Comparable[] aux, int c) {

        int compares = 0;
        int n = a.length;

        if(n<=1) return compares;

        int runStack = 0;
        //iterates through array (scaled by c), run by run
        for(int i=0;i<n/c;i++) {
            //sets length of next run to c^1
            int runLength = 1;
            //Create new run of length c with insertion-sort (and count compares)
            compares += InsertionSort.sort(a,c*i, c*(i+1)-1);

            while((runStack & runLength) != 0) {
                
                //find interval to merge (scaled by c)
                int lo = c*(runStack - runLength);
                int mid = c*(runStack) - 1;
                int hi = c*(runStack + runLength) - 1;

                //debug
                System.out.println("Now merging: a[" + lo + " .. " + mid + "] + a [" + mid + " + 1 .. " + hi + "]");

                compares += Merge.merge(a, aux, lo, mid, hi);
                runStack = runStack & (~runLength);
                runLength = runLength << 1;
            }
            runStack = runStack | runLength;
        }

        int runLength = 1;
        int hi = n - 1;
        //sort any remaining elements not in the stack (upscaled by c):
        if(c*runStack < hi) compares += InsertionSort.sort(a,c*runStack,hi);

        //debug
        System.out.println("Finishing up stack:");
        while(runStack != 0) {

            if((runStack & runLength) != 0) {

                //define indexes for merge, scaled by c
                int lo = c*(runStack - runLength);
                int mid = c*(runStack) - 1;

                //If not a redundant first merge : //TODO: verify this check (Maybe a bit ad hoc)
                if(hi >= mid + 1) {
                    //debug
                    System.out.println("Now merging: a[" + lo + " .. " + mid + "] + a [" + mid + " + 1 .. " + hi + "]");
                    compares += Merge.merge(a, aux, lo, mid, hi);
                }

                runStack = runStack & (~runLength);
            }

            runLength = runLength << 1;
        }

        //debug
        System.out.println(Arrays.toString(a));

        return compares;
    }
}
