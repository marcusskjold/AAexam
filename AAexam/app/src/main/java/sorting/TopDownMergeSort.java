package sorting;
//Inspiration: https://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/Merge.java.html


public class TopDownMergeSort {

    // This class should not be instantiated.
    private TopDownMergeSort() { }


    private static int sort(Comparable[] a, Comparable[] aux, int lo, int hi) {
        //if just one element (or empty array), ready to merge (so 0 compares)
        if (hi <= lo) return 0;

        //split up array and recursively sort the two, returning number of compares from each
        int mid = lo + (hi - lo) / 2;
        int comparesLeft = sort(a, aux, lo, mid);
        int comparesRight = sort(a, aux, mid + 1, hi);

        //merge the sorted arrays
        int comparesMerge = Merge.merge(a, aux, lo, mid, hi);

        //returns sum of compares from recursive calls and current merge
        return comparesLeft + comparesRight + comparesMerge;
    }
    
    /**
     * Rearranges the array in ascending order, using the natural order.
     * @param a the array to be sorted
     * @return the number of compares performed for the sort
     */
    @SuppressWarnings("rawtypes") //TODO: For now I suppress warnings like this
    public static int sort(Comparable[] a) {
        Comparable[] aux = new Comparable[a.length];
        int compares = sort(a, aux, 0, a.length-1);
        assert Util.isSorted(a);
        return compares;
    }

}
