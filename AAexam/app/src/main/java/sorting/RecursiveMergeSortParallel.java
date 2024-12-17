package sorting;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class RecursiveMergeSortParallel {
    private static ForkJoinPool executor = new ForkJoinPool();

    public static <T extends Comparable<? super T>> int sort(T[] a, int c) {
        T[] aux = a.clone();
        return executor.invoke(new MergeSortTask<T>(a, aux, 0, a.length-1, c));
    }

    public static class MergeSortTask<T extends Comparable<? super T>> extends RecursiveTask<Integer> {
        private T[] a, aux;
        int compares, low, high, c;

        public MergeSortTask(T[] a, T[] aux, int lo, int hi, int c) {
            this.a = a; low = lo; high = hi; this.c = c; this.aux = aux;
        }

        public int sort(int lo, int hi) {
            if (hi <= lo) return 0;
            int mid = lo + (hi - lo) / 2;
            int left = sort(lo, mid);
            int right = sort(mid + 1, hi);
            int merge = Merge.merge(a, aux, lo, mid, hi);
            return left + right + merge;
        }

        @Override protected Integer compute() {
            if (high - low < c) return sort(low, high);
            int mid = low + (high - low) / 2;
            ForkJoinTask<Integer> left = new MergeSortTask<T>(a, aux, low, mid, c).fork();
            int r = new MergeSortTask<T>(a, aux, mid+1, high, c).invoke();
            int l = left.join();
            return r + l;
        }
    }
}
