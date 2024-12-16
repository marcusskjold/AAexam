package sorting;

public class TopDownMergeSortCutoff {
    private TopDownMergeSortCutoff() {}

    private static int sort(Comparable[] a, Comparable[] aux, int lo, int hi, int c) {

        //If subarray a[lo .. hi] (inclusive) is at length at most c, use insertion-sort
        if(hi <= lo + c - 1) return InsertionSort.sort(a, lo, hi);

        //split up array and recursively sort the two, returning number of compares from each
        int mid = lo + (hi - lo) / 2;
        int comparesLeft = sort(a, aux, lo, mid, c);
        int comparesRight = sort(a, aux, mid + 1, hi, c);

        //merge the sorted arrays
        int comparesMerge = Merge.merge(a, aux, lo, mid, hi);

        //returns sum of compares from recursive calls and current merge
        return comparesLeft + comparesRight + comparesMerge;
    }
    
    /**
     * Rearranges the array in ascending order, using the natural order.
     * For subarrays of size {@code c}, switches to insertionsort. 
     * @param a the array to be sorted
     * @param c the cutoff value for switching to insertion sort; must be at least 1
     * @return the number of compares performed during the sort
     * @throws IllegalArgumentException if {@code c} is less than 1
     */
    @SuppressWarnings("rawtypes") //TODO: For now I suppress warnings like this
    public static int sort(Comparable[] a, int c) {
        if (c < 1) {
            throw new IllegalArgumentException("Cutoff value must be at least 1.");
        }
        Comparable[] aux = new Comparable[a.length];
        int compares = sort(a, aux, 0, a.length-1, c);
        assert Util.isSorted(a);
        return compares;
    }


    /**
     * Rearranges the array in ascending order, using the natural order.
     * Uses a cutoff value of 1, corresponding to just using mergesort
     * (only switches to insertionSort for trivial subarrays of size 1 or less)
     * @param a the array to be sorted
     * @return the number of compares performed for the sort
     */
    @SuppressWarnings("rawtypes") //TODO: For now I suppress warnings like this
    public static int sort(Comparable[] a) {
        return sort(a, 1);
    }

}
