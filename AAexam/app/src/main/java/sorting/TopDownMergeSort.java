package sorting;
//Inspiration: https://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/Merge.java.html


public class TopDownMergeSort {

    // This class should not be instantiated.
    private TopDownMergeSort() { }

    // stably merge a[lo .. mid] with a[mid+1 ..hi] using aux[lo .. hi]
    //is stable since element from left half is inserted in case of tiebreak
    @SuppressWarnings("rawtypes")
    private static int merge(Comparable[] a, Comparable[] aux, int lo, int mid, int hi) {
        // precondition: a[lo .. mid] and a[mid+1 .. hi] are sorted subarrays
        assert isSorted(a, lo, mid);
        assert isSorted(a, mid+1, hi);

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
            //If current key on left less or equal to key on rigt, take from left, inc compares
            else                                            {a[k] = aux[left++]; compares++;}                            
        }

        // postcondition: a[lo .. hi] is sorted
        assert isSorted(a, lo, hi);
        //return number of compares
        return compares;
    }

    private static int sort(Comparable[] a, Comparable[] aux, int lo, int hi) {
        //number of total compares
        int compares = 0;
        //if just one element (or empty array), ready to merge (TODO:threshold for insertion sort could be here?)
        if (hi <= lo) return compares;

        //split up array and recursively sort the two, returning number of compares from each
        int mid = lo + (hi - lo) / 2;
        int comparesLeft = sort(a, aux, lo, mid);
        int comparesRight = sort(a, aux, mid + 1, hi);

        //merge the sorted arrays
        int comparesMerge = merge(a, aux, lo, mid, hi);

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
        assert isSorted(a);
        return compares;
    }


   /***************************************************************************
    *  Check if array is sorted - useful for debugging.
    ***************************************************************************/

    private static boolean isSorted(Comparable[] a) {
        return isSorted(a, 0, a.length - 1);
    }

    private static boolean isSorted(Comparable[] a, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++)
            if (a[i].compareTo(a[i-1]) < 0 ) return false;
        return true;
    }

}
