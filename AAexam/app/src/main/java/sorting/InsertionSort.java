package sorting;

public class InsertionSort {


    // This class should not be instantiated.
    private InsertionSort() { }

    /**
     * Rearranges the array in ascending order, using the natural order.
     * @param a the array to be sorted
     * @return the number of compares performed
     */
    public static int sort(Comparable[] a) {
        int compares = sort(a, 0, a.length - 1);
        assert SortUtils.isSorted(a);
        return compares;
    }


    
    /**
     * Rearranges the subarray a[lo..hi] in ascending order, using the natural order.
     * @param a the array to be sorted
     * @param lo left endpoint (inclusive)
     * @param hi right endpoint (inclusive)
     */
    public static int sort(Comparable[] a, int lo, int hi) {
        int compares = 0;
        for (int i = lo + 1; i <= hi; i++) {
            for (int j = i; j > lo; j--) {
                compares++;
                if(a[j].compareTo(a[j-1]) < 0)
                    exch(a, j, j-1);
                else
                    break;
            }
        }
        assert SortUtils.isSorted(a, lo, hi);
        return compares;
    }


   /***************************************************************************
    *  Helper sorting functions.
    ***************************************************************************/


    // exchange a[i] and a[j]
    private static void exch(Object[] a, int i, int j) {
        Object swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

}
