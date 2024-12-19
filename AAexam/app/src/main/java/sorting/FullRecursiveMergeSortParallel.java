package sorting;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class FullRecursiveMergeSortParallel {
    private static ForkJoinPool ex = new ForkJoinPool();

    public static <T extends Comparable<? super T>> int sort(T[] a, int c, int p) {
        return ex.invoke(new MergeSortTask<T>(a, a.clone(), 0, a.length-1, c, true, p)); }

    public static <T extends Comparable<? super T>> int sort(T[] a, int c) {
        return ex.invoke(new MergeSortTask<T>(a, a.clone(), 0, a.length-1, c, false, 1)); }

    public static class MergeSortTask<T extends Comparable<? super T>> extends RecursiveTask<Integer> {
        private final T[] a, aux;
        private final int lo, hi, c, mid, p;
        private final boolean parallel;

        public MergeSortTask(T[] a, T[] aux, int lo, int hi, int c, boolean parallelMerging, int p) {
            this.a = a; this.aux = aux; this.lo = lo; this.hi = hi;
            this.c = c; parallel = parallelMerging; this.p = p;
            mid = lo + (hi - lo) / 2;
        }

        @Override protected Integer compute() {
            if (hi - lo < c) return TopDownMergeSort.sort(a, aux, lo, hi);
            int nextP = (p == 1) ? 1 : p / 2;
            ForkJoinTask<Integer> fork  = new MergeSortTask<T>(a, aux, lo, mid, c, parallel, nextP).fork();
            int r                       = new MergeSortTask<T>(a, aux, mid+1, hi, c, parallel, nextP).invoke();
            int l                       = fork.join();
            int m = (parallel) ? MergeParallel.merge(a, aux, lo, mid, hi, p, ex, false) : Merge.merge(a, aux, lo, mid, hi); 
            
            return r + l + m;
        }
    }
}
