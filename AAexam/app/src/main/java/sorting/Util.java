package sorting;

/** A class for variuous utility-methods for
 * our sorting algorithms.
 * Contains methods for checking that an array or
 * subarrays are sorted. */
public class Util {
    
    /** Checks that array a is sorted
     * @param a the array to be checked
     * @return a boolean indicating whether the array is sorted or not */
    public static <T extends Comparable<? super T>> boolean isSorted(T[] a) {
        return isSorted(a, 0, a.length - 1); }

    /** Checks whether the subarray a[lo..hi] is sorted
     * @param a the array to be checked
     * @param lo the initial index of the subarray (inclusive)
     * @param hi the final index of the subarray (inclusive)
     * @return */
    public static <T extends Comparable<? super T>> boolean isSorted(T[] a, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++)
            if (a[i].compareTo(a[i-1]) < 0 ) return false;
        return true;
    }
    
    /** Explore a run, by locating the longest weakly increasing
     * or strictly decreasing sequence, starting from index {@code first} in array {@code a},
     * from left to right, and returning the last index of said sequence.
     * If the sequence is strictly decreasing, it is reversed.
     * <p>
     * The length of the run returned  {@code (last - first + 1)} is equal to
     * the number of compares performed, EXCEPT for if the run extends
     * to the end of {@code a} {@code (last == a.length - 1)}, 
     * in which case the number of compares corresponds to the run-length minus 1.
     * As such, the latter will only be the case for the last run in {@code a}.
     * @param a the array to explore the run in
     * @param first the initial index of the run
     * @return the last index of the run */
    public static <T extends Comparable<? super T>> int exploreRun(T[] a, int first) {
        int n = a.length;
        assert (first >= 0 && first < n);
        if (n == first + 1) return first; //if first is the last index of a, run starts and ends at first
        int last = first + 1;            //else define last index of run (starting at first + 1)

        //If weakly increasing: find the end of weakly increasing sequence
        if(a[first].compareTo(a[last]) <= 0) last = findIncreasing(a, last);
        else {                           //If strictly decreasing: find the end of strictly increasing run and reverse
            last = findDecreasing(a, last);
            reverseSequence(a, first, last);
        }
        return last;
    }

    private static <T extends Comparable<? super T>> int findIncreasing(T[] a, int from) {
        int to = from;
        while(to < a.length - 1) {
            if (a[to].compareTo(a[to + 1]) > 0) break;
            to++;
        }
        return to;
    }

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

    //--------------------------------------------------------------------------------------------
    //ONLY FOR TESTING COMPARES
    //--------------------------------------------------------------------------------------------

    /** Test-version of exploreRun. Only to be used for Tests.
     * Instead of returning last index, return tuple of last index
     * and number of compares performed
     * performed during run-exploration
     * @param a the array to explore the run in
     * @param first the initial index of the run
     * @return length two int[] where int[0]= last index of run
     * and int[1]= number of compares */
    public static <T extends Comparable<? super T>> int[] exploreRunDebug(T[] a, int first) {
        int n = a.length;
        assert(first >=0 && first < a.length);
        //if first is the last index of a, run starts and ends at first, with 0 compares
        if(n == first + 1) return new int[]{first,0};


        //define last index of run (starting at first + 1) and number of compares as a tuple
        int[] lastAndCompares = new int[]{first + 1, 1};
        int last = lastAndCompares[0];
        int compares = lastAndCompares[1];
        //If weakly increasing: find the end of weakly increasing sequence
        if(a[first].compareTo(a[last]) <= 0) lastAndCompares = findIncreasingDebug(a, last, compares);
        //If strictly decreasing: find the end of strictly increasing run and reverse
        else {
            lastAndCompares = findDecreasingDebug(a, last, compares);
            reverseSequence(a, first, last);
        }

        //return number of compares
        return lastAndCompares;
    }

    private static <T extends Comparable<? super T>> int[] findIncreasingDebug(T[] a, int from, int compares) {
        int to = from;
        while(to < a.length - 1) {
            compares++;
            if (a[to].compareTo(a[to + 1]) > 0) break;
            to++;
        }
        return new int[]{to,compares};
    }

    private static <T extends Comparable<? super T>> int[] findDecreasingDebug(T[] a, int from, int compares) {
        int to = from;
        while(to < a.length-1) {
            compares++;
            if (a[to].compareTo(a[to + 1]) <= 0) break;
            to++;
        }
        return new int[]{to,compares};
    }
}
