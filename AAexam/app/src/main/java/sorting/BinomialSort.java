package sorting;

import static sorting.Merge.merge;
import static java.lang.Math.min;
import static sorting.Util.isSorted;


public class BinomialSort {
    
    public static <T extends Comparable<? super T>> int sort(T[] a, int c) {
        final int n  = a.length;
        int compares = 0;

        if (c < 1) throw new IllegalArgumentException("Cutoff value must be at least 1.");
        if (n < 2) return 0;

        final T[] aux       = a.clone();
        final byte stackmax = (byte) (32 - Integer.numberOfLeadingZeros(n) + 1); // |1|
        final int[] starts  = new int[stackmax];    // Setup stack
        final int[] lengths = new int[stackmax];
        byte top            = 0;
        lengths[0]          = Integer.MAX_VALUE;    // index 0 is a guard value

        for (int next = 0; next < n; next += c) {
            int start     = next;                   // Define next run
            final int end = min(next + c, n) - 1;
            int length    = end - next + 1;
            compares     += InsertionSort.sort(a, next, end);

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
        return compares;
    }
}
