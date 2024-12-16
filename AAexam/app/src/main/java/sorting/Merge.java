package sorting;

public class Merge {
    private Merge() {}


    /**
     * Stably merge a[lo .. mid] with a[mid+1 ..hi] using aux[lo .. hi].
     * Is stable since element from left half is inserted in case of tiebreak.
     * The two subarrays must already be sorted.
     * The merged subarray will also be sorted.
     * @param a the array, from which subarrays will be merged.
     * @param aux auxilliary array used for merging. Must be of same type as a.
     * @param lo the initial index to merge from (inclusive).
     * @param mid the final index of the left subarray (inclusive).
     * @param hi the final index to merge to (inclusive).
     * @return the number of compares used for the merge.
     */
    
    @SuppressWarnings("rawtypes")
    public static int merge(Comparable[] a, Comparable[] aux, int lo, int mid, int hi) {
        // precondition: a[lo .. mid] and a[mid+1 .. hi] are sorted subarrays
        assert Util.isSorted(a, lo, mid);
        assert Util.isSorted(a, mid+1, hi);

        //counter for number of compares:
        int compares = 0;

        // copy to aux[]
        for (int k = lo; k <= hi; k++) {
            aux[k] = a[k];
        }

        // merge back to a[]
        int left = lo, right = mid+1;
        for (int k = lo; k <= hi; k++) {
            //If left half exhausted, take from right
            if      (left > mid)                             a[k] = aux[right++]; 
            //If right half exhausted, take from left
            else if (right > hi)                             a[k] = aux[left++]; 
            //If current element on right less than current on left, take from right, inc compares
            else if (aux[right].compareTo(aux[left]) < 0 )  {a[k] = aux[right++]; compares++;}
            //If current key on left less or equal to key on right, take from left, inc compares (ensures stability)
            else                                            {a[k] = aux[left++]; compares++;}                            
        }

        // postcondition: a[lo .. hi] is sorted
        assert Util.isSorted(a, lo, hi);
        //return number of compares
        return compares;
    }

}
