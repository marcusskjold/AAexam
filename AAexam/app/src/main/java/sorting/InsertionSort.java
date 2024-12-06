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
        int compares = 0;
        int n = a.length;
        for (int i = 1; i < n; i++) {
            for (int j = i; j > 0; j--) {
                compares++;
                if(a[j].compareTo(a[j-1]) < 0)
                    exch(a, j, j-1);
                else
                    break;
            }
            assert isSorted(a, 0, i);
        }
        assert isSorted(a);
        return compares;
    }


    //TODO: Should we make hi inclusive for our purpose?
    /**
     * Rearranges the subarray a[lo..hi) in ascending order, using the natural order.
     * @param a the array to be sorted
     * @param lo left endpoint (inclusive)
     * @param hi right endpoint (exclusive)
     */
    public static int sort(Comparable[] a, int lo, int hi) {
        int compares = 0;
        for (int i = lo + 1; i < hi; i++) {
            for (int j = i; j > lo; j--) {
                compares++;
                if(a[j].compareTo(a[j-1]) < 0)
                    exch(a, j, j-1);
                else
                    break;
            }
        }
        assert isSorted(a, lo, hi);
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


   /***************************************************************************
    *  Check if array is sorted - useful for debugging.
    ***************************************************************************/
    private static boolean isSorted(Comparable[] a) {
        return isSorted(a, 0, a.length);
    }

    // is the array a[lo..hi) sorted
    private static boolean isSorted(Comparable[] a, int lo, int hi) {
        for (int i = lo + 1; i < hi; i++)
            if (a[i].compareTo(a[i-1]) < 0 ) return false;
        return true;
    }
}
