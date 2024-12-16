package sorting;

/**
 * A class for variuous utility-methods for
 * our sorting algorithms.
 * Contains methods for checking that an array or
 * subarrays are sorted.
 */
public class Util {
    
    /**
     * Checks that array a is sorted
     * @param a the array to be checked
     * @return a boolean indicating whether the array is sorted or not
     */
    public static <T extends Comparable<? super T>> boolean isSorted(T[] a) {
        return isSorted(a, 0, a.length - 1);
    }

    /**
     * Checks whether the subarray a[lo..hi] is sorted
     * @param a the array to be checked
     * @param lo the initial index of the subarray (inclusive)
     * @param hi the final index of the subarray (inclusive)
     * @return
     */
    public static <T extends Comparable<? super T>> boolean isSorted(T[] a, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++)
            if (a[i].compareTo(a[i-1]) < 0 ) return false;
        return true;
    }
    

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
    public static <T extends Comparable<? super T>> int exploreRun(T[] a, int first) {
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

    private static <T extends Comparable<? super T>> int findIncreasing(T[] a, int from) {
        int to = from;
        //index for penultimate element in a
        while(to < a.length - 1) {
            if (a[to].compareTo(a[to + 1]) > 0) break;
            to++;
        }
        return to;
    }

    //TODO: what if to < penult from beginning?
    private static <T extends Comparable<? super T>> int findDecreasing(T[] a, int from) {
        int to = from;
        while(to < a.length-1) {
            if (a[to].compareTo(a[to + 1]) <= 0) break;
            to++;
        }
        return to;
    }

    private static <T extends Comparable<? super T>> void reverseSequence(T[] a, int from, int to) {
        while (from < to) {
            T temp = a[from];
            a[from] = a[to];
            a[to] = temp;
            from++;
            to--;
        }
    }
}
