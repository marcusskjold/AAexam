package sorting;

import static java.lang.Math.min;
import static sorting.Util.exploreRun;
import static sorting.Util.isSorted;

import java.util.Arrays;

import static sorting.Merge.merge;

public class BinomialSortAdaptive {
    
    public static <T extends Comparable<? super T>> int sort(T[] a, int c) {
        //System.out.println(Arrays.toString(a));
        final int n  = a.length;
        int compares = 0;

        if (c < 1) throw new IllegalArgumentException("Cutoff value must be at least 1.");
        if (n < 2) return 0;

        final T[] aux       = a.clone();
        final byte stackmax = (byte) (32 - Integer.numberOfLeadingZeros(n) + 3); // |1|
        final int[] starts  = new int[stackmax];    // Setup stack
        final int[] lengths = new int[stackmax];
        byte top            = 0;
        lengths[0]          = Integer.MAX_VALUE;    // index 0 is a guard value
        
        int increment;

        for (int next = 0; next < n; next += increment) {
            int end = exploreRun(a, next);
            increment = (end - next) + 1;
            compares  += increment;
            //System.out.println("next = " + next);
            //System.out.println("endC = " + endC + ", endR = " + endR);
            if (increment <= c) { 
                end = min(next + c, n) - 1;
                compares += InsertionSort.sort(a, next, end);
                //System.out.println("INSERTIONSORT!");
                increment = (end - next) + 1;
            } 
            assert(next + increment <= n);
            //System.out.println();
            //System.out.println("n      = " + n);
            //System.out.println("endR   = " + endR);
            //System.out.println("endC   = " + endC);
            //System.out.println("end    = " + end);
            //System.out.println("next   = " + next);
            //System.out.println("length = " + length);
            //System.out.println("top    = " + top);
            //System.out.println("toplen = " + lengths[top]);
            int start  = next;                      // Define next run
            int length = increment;
            while (lengths[top] < length * 2) {     // Peek into the stack
                int mid   = start - 1;              // Merge next run with top of stack
                start     = starts[top];
                length   += lengths[top];
                //System.out.printf("a[%d .. %d] + a[%d .. %d]%n", start, mid, mid+1, end);
                compares += merge(a, aux, start, mid, end);
                top--;                              // Pop the stack
            }
            top++;
            //System.out.println(start + " at the top");
            //System.out.println(Arrays.toString(a));
            starts[top]  = start;
            lengths[top] = length;
        }
        //System.out.println(compares);

        final int hi = n-1;
        while (top > 1) {                           // Final merge
            int mid = starts[top] - 1;
            top--;
            int lo  = starts[top];
            //System.out.println(Arrays.toString(a));
            //System.out.printf("a[%d .. %d] + a[%d .. %d]%n", lo, mid, mid+1, hi);
            compares += merge(a, aux, lo, mid, hi);
        }

        assert isSorted(a);
        return compares - 1;
    }
}
