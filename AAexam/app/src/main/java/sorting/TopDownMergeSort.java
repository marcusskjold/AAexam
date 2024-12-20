package sorting;

public class TopDownMergeSort {
    private TopDownMergeSort() { }

    public static <T extends Comparable<? super T>> int sort(T[] a, T[] aux, int lo, int hi) {
        if (hi <= lo) return 0;
        int mid           = lo + (hi - lo) / 2;
        int comparesLeft  = sort(a, aux, lo, mid);
        int comparesRight = sort(a, aux, mid + 1, hi);
        int comparesMerge = Merge.merge(a, aux, lo, mid, hi);
        return comparesLeft + comparesRight + comparesMerge;
    }
    
    /** Rearranges the array in ascending order, using the natural order.
     * @param a the array to be sorted
     * @return the number of compares performed for the sort */
    public static <T extends Comparable<? super T>> int sort(T[] a) {
        T[] aux = a.clone();
        int compares = sort(a, aux, 0, a.length-1);
        assert Util.isSorted(a);
        return compares;
    }

}
