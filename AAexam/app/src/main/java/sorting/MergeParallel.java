package sorting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class MergeParallel {
    private static ForkJoinPool ex = new ForkJoinPool();
    
    /** Generic inner utility data structure */
    public record IntPair(int a, int b) { }

    /** Merge two sorted neighboring sequences in the array in parallel. 
     * @param a The array to be modified
     * @param aux An auxillary array. Its contents do not matter.
     * @param lo The first index of the first sorted sequence.
     * @param mid The final index of the first sorted sequence. {@code mid + 1} is the first index of the second sorted sequence.
     * @param hi The final index of the second sorted sequence.
     * @param p The number of parallel tasks to spread the work across evenly.
     * @param pool a pool for the tasks to avoid creating many competing pools when called by a recursive merge sorting algorithm.
     * @param measureSpan a flag if the returned value should reflect the span of comparisons rather than the sum. 
     *                    That means that the highest comparison count returned by one of the tasks will chosen.
     */
    public static <T extends Comparable<? super T>> int merge(T[] a, T[] aux, int lo, int mid, int hi, int p, 
                                                              ForkJoinPool pool, boolean measureSpan) {
        ex = pool; return merge(a, aux, lo, mid, hi, p, measureSpan); }

    /** Merge two sorted neighboring sequences in the array in parallel. 
     * @param a The array to be modified
     * @param aux An auxillary array. Its contents do not matter.
     * @param lo The first index of the first sorted sequence.
     * @param mid The final index of the first sorted sequence. {@code mid + 1} is the first index of the second sorted sequence.
     * @param hi The final index of the second sorted sequence.
     * @param p The number of parallel tasks to spread the work across evenly.
     */
    public static <T extends Comparable<? super T>> int merge(T[] a, T[] aux, int lo, int mid, int hi, int p) {
        return merge(a, aux, lo, mid, hi, p, false); }

    /** Merge two sorted neighboring sequences in the array in parallel. 
     * @param a The array to be modified
     * @param aux An auxillary array. Its contents do not matter.
     * @param lo The first index of the first sorted sequence.
     * @param mid The final index of the first sorted sequence. {@code mid + 1} is the first index of the second sorted sequence.
     * @param hi The final index of the second sorted sequence.
     * @param p The number of parallel tasks to spread the work across evenly.
     * @param measureSpan a flag if the returned value should reflect the span of comparisons rather than the sum. 
     *                    That means that the highest comparison count returned by one of the tasks will chosen.
     */
    public static <T extends Comparable<? super T>> int merge(T[] a, T[] aux, int lo, int mid, int hi, int p, 
                                                              boolean measureSpan) {
        int n = hi - lo + 1, compares = 0;                  // Setup
        double increment = n / (double) p;
        for (int k = lo; k <= hi; k++) { aux[k] = a[k]; }

        List<Callable<Integer>> tasks = new ArrayList<>(); // Create tasks
        tasks.add(() -> { return mergeTask(a, aux, lo, mid+1, lo, (int) increment); });
        for (int i = 1; i < p; i++) {
            int k = (int)(i*increment);
            IntPair is = twoSequenceSelect(a, lo, mid, hi, k);
            compares += is.b;
            int i_a = lo + is.a;
            int i_b = mid + 1 + (k - is.a);
            int i_o = lo + k;
            int length = (int)((i+1)*increment)-k;    // The final task needs guards to avoid indexing out of bounds.
            if (i < p-1) tasks.add(() -> { return mergeTask(a, aux, i_a, i_b, i_o, length); });
            else         tasks.add(() -> { return mergeEndTask(a, aux, i_a, i_b, i_o, length, mid, hi); });
        }

        // Collect results
        int maxCmp = 0; // For span calculation
        try {
            for (Future<Integer> fut : ex.invokeAll(tasks)) {
                int cmp = fut.get(); 
                if (measureSpan && cmp > maxCmp) maxCmp = cmp;  // Span
                else compares += fut.get();                     // Sum
            }
        } catch (InterruptedException e) { System.out.println("Interrupted: " + e);
        } catch (ExecutionException e)   { throw new RuntimeException(e.getCause()); }
        return (measureSpan) ? compares + maxCmp : compares;
    }
    
    /** Merges {@code length} elements from the output index {@code i_o}.
     * @param i_a the starting position of the 
     */
    public static <T extends Comparable<? super T>> int mergeEndTask(
        T[] a, T[] aux, int i_a, int i_b, int i_o, int length, int mid, int hi) {
        int compares = 0;
        int k, l = i_a, r = i_b, end = i_o+length;
        assert(hi == end-1);                          // Invariant
        for (k = i_o; k < end; k++) {
            if      (l > mid)                        a[k] = aux[r++]; 
            else if (r > hi)                         a[k] = aux[l++]; 
            else if (aux[r].compareTo(aux[l]) < 0 ) {a[k] = aux[r++]; compares++;}
            else                                    {a[k] = aux[l++]; compares++;}                            
        }
        assert Util.isSorted(a, i_o, end - 1);
        return compares;
    }

    /** This is a slightly more efficient version of mergeEndTask making use of the fact that it is
     * guaranteed that neither of the subsequences will index out of bounds */
    public static <T extends Comparable<? super T>> int mergeTask(
        T[] a, T[] aux, int i_a, int i_b, int i_o, int length) {
        int compares = 0;
        int k, l = i_a, r = i_b, end = i_o+length;
        for (k = i_o; k < end; k++) {
            if (aux[r].compareTo(aux[l]) < 0 ) {a[k] = aux[r++]; compares++; }
            else                               {a[k] = aux[l++]; compares++; }                            
        }
        assert Util.isSorted(a, i_o, end - 1);
        return compares;
    }

    /** Returns the index of inside the subsequence {@code a} that would have been reached by a sequential merge
     * before putting the element at index {@code k} in the output sequence.
     * The output sequence has its index 0 at index {@code lo} in the {@code in} array.
     * Therefor {@code k} would be placed at {@code in[lo+k]}.
     * {@code a} is a sequence with index 0 at {@code in[lo]} in the input array and with its final index at {@code in[mid]}.
     * {@code b} is a sequence with index 0 at {@code in[mid+1]}, and its final index at {@code in[hi]}.
     * {@code i_a} is the index in {@code a} that would have been reached by a sequential merge when considering {@code k}.
     * {@code i_b} is the index in {@code b} that would have been reached by a sequential merge when considering {@code k}.
     * {@code i_a + i_b == k}. 
     * @param in The input array
     * @param lo The index in the input array where {@code a} starts.
     * @param mid The index in the input array where {@code a} ends.
     * @param hi The index in the input array where {@code b} ends.
     * @return A pair of ints. Value .a is {@code i_a}, value .b is the sum of comparison performed during calculation.
     *         Note that {@code i_b} can be derived trivially from this result.
     */
    public static <T extends Comparable<? super T>> IntPair twoSequenceSelect(T[] in, int lo, int mid, int hi, int k) {
        final int l_a = mid - lo + 1;
        final int l_b = hi - mid;
        if (k < 0 || k >= l_a + l_b) throw new IllegalArgumentException("k is out of bounds");
        int j_b, j_a, lo_a = 0, hi_a = Math.min(k, l_a);    // k sets an upper bound.
        int cmp = 0;

        while (true) {
            j_a = lo_a + ((hi_a-lo_a)/2);
            assert(0 <= j_a && j_a <= k);
            j_b = k - j_a;

            if (!(j_a == 0 || j_b >= l_b)) {  // Is the first inequality is violated?
                if (j_a > l_a)                                             {hi_a = j_a-1; continue;}
                else if (in[lo+j_a-1].compareTo(in[mid+1+j_b]) >  0){cmp++; hi_a = j_a-1; continue;} }

            if (!(j_b == 0 || j_a >= l_a)) {   // Is the second inequality violated?
                if (j_b > l_b)                                             {lo_a = j_a+1; continue;}
                else if (in[mid+1+j_b-1].compareTo(in[lo+j_a]) >= 0){cmp++; lo_a = j_a+1; continue;} } 

            break;  // Both properties upheld, we have found i_a and i_b
        }
        return new IntPair(j_a, cmp);
    }

    public static void main(String[] args) {
        Integer[] n = {6, 5, 1, 4, 4, 2, 3, 4, 6};
        //                   0, 1, 2||0, 1, 2, 3    The indexes of sequences a and b seq
        //                   -------------------
        //                   1, 2, 3, 4, 4, 4, 6    The merged output
        //             0  1  2, 3, 4, 5, 6, 7, 8    The indexes
        //                   k = 0 -> 0, 0 (a!1)    Trace of twoSequenceSelect calculation
        //                       1 -> 1, 0 (b!2)    k_j -> i_a_j, i_b_j (the sequence ! element chosen)
        //                       2 -> 1, 1 (b!3)
        //                       3 -> 1, 2 (a!4)
        //                       4 -> 2, 2 (a!4)
        //                       5 -> 3, 2 (b!4)
        //                       6 -> 3, 3 (b!6)

        Integer[] aux = n.clone();
        merge(n, aux, 2, 4, 8, 3, false);
    }
}
