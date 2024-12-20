package sorting;

public class Merge {
    private Merge() {}

    /** Stably merge a[lo .. mid] with a[mid+1 ..hi] using aux[lo .. hi].
     * Is stable since element from left half is inserted in case of tiebreak.
     * The two subarrays must already be sorted.
     * The merged subarray will also be sorted.
     * @param a the array, from which subarrays will be merged.
     * @param aux auxilliary array used for merging. Must be of same type as a.
     * @param lo the initial index to merge from (inclusive).
     * @param mid the final index of the left subarray (inclusive).
     * @param hi the final index to merge to (inclusive).
     * @return the number of compares used for the merge. */
    public static <T extends Comparable<? super T>> int merge(T[] a, T[] aux, int lo, int mid, int hi) {
        // precondition: a[lo .. mid] and a[mid+1 .. hi] are sorted subarrays
        assert Util.isSorted(a, lo, mid);
        assert Util.isSorted(a, mid+1, hi);
        int compares = 0;
        for (int k = lo; k <= hi; k++) aux[k] = a[k];

        int left = lo, right = mid+1;
        for (int k = lo; k <= hi; k++) {
            if      (left > mid)                             a[k] = aux[right++]; 
            else if (right > hi)                             a[k] = aux[left++]; 
            else if (aux[right].compareTo(aux[left]) < 0 )  {a[k] = aux[right++]; compares++;}
            else                                            {a[k] = aux[left++]; compares++;}                            
        }

        assert Util.isSorted(a, lo, hi); // postcondition: a[lo .. hi] is sorted
        return compares;
    }
}
