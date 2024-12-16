package sorting;

import java.lang.reflect.Array;

public class RunUtils {
    

    //TODO: I think the number of compares for this operation is not counted -> should we do that?
    /**
     * Explore a run, by locating the longest weakly increasing
     * or strictly decreasing, starting from index {@code first} in array {@code a},
     * from left to right, and returning the last index of said sequence.
     * If the sequence is strictly decreasing, it is reversed.
     * @param a the array to explore the run in
     * @param first the initial index of the run
     * @return the last index of the run
     */
    public static int exploreRun(Comparable[] a, int first) {
        int n = a.length;
        assert(first >=0 && first < a.length);
        //if first is the last index of a, run starts and ends at first
        if(n == first + 1) return first;

        //else define last index of run (starting at first + 1)
        int last = first + 1;
        //If weakly increasing: find the end of weakly increasing sequence
        if(a[first].compareTo(a[last]) <= 0) last = findIncreasing(a, last);
        //If strictly decreasing: find the end of strictly increasing run and reverse
        else {
            last = findDecreasing(a, last);
            reverseSequence(a, first, last);
        }
        return last;
    }

    private static int findIncreasing(Comparable[] a, int from) {
        int to = from;
        //index for penultimate element in a
        while(to < a.length - 1) {
            if (a[to].compareTo(a[to + 1]) > 0) break;
            to++;
        }
        return to;
    }

    //TODO: what if to < penult from beginning?
    private static int findDecreasing(Comparable[] a, int from) {
        int to = from;
        while(to < a.length-1) {
            if (a[to].compareTo(a[to + 1]) <= 0) break;
            to++;
        }
        return to;
    }

    private static void reverseSequence(Comparable[] a, int from, int to) {
        while (from < to) {
            Comparable temp = a[from];
            a[from] = a[to];
            a[to] = temp;
            from++;
            to--;
        }
    }
}
