package sorting;

/**
 * A class for variuous utility-methods for
 * our sorting algorithms.
 * Contains methods for checking that an array or
 * subarrays are sorted.
 */
public class SortUtils {
    
    /**
     * Checks that array a is sorted
     * @param a the array to be checked
     * @return a boolean indicating whether the array is sorted or not
     */
    public static boolean isSorted(Comparable[] a) {
        return isSorted(a, 0, a.length - 1);
    }

    /**
     * Checks whether the subarray a[lo..hi] is sorted
     * @param a the array to be checked
     * @param lo the initial index of the subarray (inclusive)
     * @param hi the final index of the subarray (inclusive)
     * @return
     */
    public static boolean isSorted(Comparable[] a, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++)
            if (a[i].compareTo(a[i-1]) < 0 ) return false;
        return true;
    }
}
