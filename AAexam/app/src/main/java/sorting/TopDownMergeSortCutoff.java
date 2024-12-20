package sorting;

public class TopDownMergeSortCutoff {
    private TopDownMergeSortCutoff() {}

    private static <T extends Comparable<? super T>> int sort(T[] a, T[] aux, int lo, int hi, int c) {
        if(hi <= lo + c - 1) return InsertionSort.sort(a, lo, hi);

        int mid           = lo + (hi - lo) / 2;
        int comparesLeft  = sort(a, aux, lo, mid, c);
        int comparesRight = sort(a, aux, mid + 1, hi, c);
        int comparesMerge = Merge.merge(a, aux, lo, mid, hi);
        return comparesLeft + comparesRight + comparesMerge;
    }
    
    /** Rearranges the array in ascending order, using the natural order.
     * For subarrays of size {@code c}, switches to insertionsort. 
     * @param a the array to be sorted
     * @param c the cutoff value for switching to insertion sort; must be at least 1
     * @return the number of compares performed during the sort
     * @throws IllegalArgumentException if {@code c} is less than 1 */
    public static <T extends Comparable<? super T>> int sort(T[] a, int c) {
        if (c < 1) throw new IllegalArgumentException("Cutoff value must be at least 1.");
        T[] aux      = a.clone();
        int compares = sort(a, aux, 0, a.length-1, c);
        assert Util.isSorted(a);
        return compares;
    }

    /** Rearranges the array in ascending order, using the natural order.
     * Uses a cutoff value of 1, corresponding to just using mergesort
     * (only switches to insertionSort for trivial subarrays of size 1 or less)
     * @param a the array to be sorted
     * @return the number of compares performed for the sort */
    public static <T extends Comparable<? super T>> int sort(T[] a) { return sort(a, 1); }

}
