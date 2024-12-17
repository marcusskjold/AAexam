package sorting;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import static sorting.Merge.merge;

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

        @Override protected Integer compute() {
            if (high - low < c) return TopDownMergeSort.sort(a, aux, low, high);
            int mid = low + (high - low) / 2;
            ForkJoinTask<Integer> fork  = new MergeSortTask<T>(a, aux, low, mid, c).fork();
            Integer r = new MergeSortTask<T>(a, aux, mid+1, high, c).invoke();
            int l = fork.join();
            int m = merge(a, aux, low, mid, high);

            return r + l + m;
        }
    }
}
