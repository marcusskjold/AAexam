package sorting;

import static java.lang.Math.min;
import static sorting.Merge.merge;
import static sorting.Util.exploreRun;
import static sorting.Util.isSorted;

public class BinomialSortAdaptive {
    private BinomialSortAdaptive() {}
    public static <T extends Comparable<? super T>> int sort(T[] a, int c) {
        final int n  = a.length;
        int compares = 0;

        if (c < 1) throw new IllegalArgumentException("Cutoff value must be at least 1.");
        if (n < 2) return 0;

        final T[] aux       = a.clone();
        final byte stackmax = (byte) (32 - Integer.numberOfLeadingZeros(n) + 1); // |1|
        final int[] starts  = new int[stackmax];    // Setup stack
        final int[] lengths = new int[stackmax];
        byte top            = 0;                    // The position of the top of the stack (& the size!)
        lengths[0]          = Integer.MAX_VALUE;    // index 0 is a guard value
        
        int increment;
        for (int next = 0; next < n; next += increment) {
            int end   = exploreRun(a, next);
            increment = (end - next) + 1;
            compares += increment;

            if (increment <= c) { end       = min(next + c, n) - 1;
                                  compares += InsertionSort.sort(a, next, end);
                                  increment = (end - next) + 1; } 

            assert(next + increment <= n);

            int start  = next;                      // Define next run
            int length = increment;
            while (lengths[top] < length * 2) {     // Peek into the stack
                int mid   = start - 1;              // Merge next run with top of stack
                start     = starts[top];
                length   += lengths[top];
                compares += merge(a, aux, start, mid, end);
                top--;                              // Pop the stack
            }
            top++;
            starts[top]  = start;
            lengths[top] = length;
        }

        final int hi = n-1;
        while (top > 1) {                           // Final merge
            int mid = starts[top] - 1;
            top--;
            int lo  = starts[top];
            compares += merge(a, aux, lo, mid, hi);
        }

        assert isSorted(a);
        return compares - 1;
    }
}
