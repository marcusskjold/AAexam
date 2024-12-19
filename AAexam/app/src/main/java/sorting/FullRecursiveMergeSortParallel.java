package sorting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;


public class FullRecursiveMergeSortParallel {
    private static ForkJoinPool executor = new ForkJoinPool();
    private static List<Callable<Integer>> tasks = new ArrayList<>();

    public static <T extends Comparable<? super T>> int sort(T[] a, int c) {
        //System.out.println("Start");
        T[] aux = a.clone();
        int compares = executor.invoke(new MergeSortTask<T>(a, aux, 0, a.length-1, c));
        return compares;
    }

    public record IntPair(int a, int b) { }

    public static class MergeSortTask<T extends Comparable<? super T>> extends RecursiveTask<Integer> {
        private T[] a, aux;
        int compares, low, high, c;

        public MergeSortTask(T[] a, T[] aux, int lo, int hi, int c) {
            //System.out.println("new Task!");
            //System.out.printf("lo = %d, hi = %d%n", lo, hi);
            this.a = a; low = lo; high = hi; this.c = c; this.aux = aux;
        }

        @Override protected Integer compute() {
            if (high - low < c) return TopDownMergeSort.sort(a, aux, low, high);
            int mid = low + (high - low) / 2;
            //synchronized (this) {
            ForkJoinTask<Integer> fork  = new MergeSortTask<T>(a, aux, low, mid, c).fork();
            int r = new MergeSortTask<T>(a, aux, mid+1, high, c).invoke();
            int l = fork.join();
            int m = Merge.merge(a, aux, low, mid, high); // TODO: Dynamically set merge parameter
            return r + l + m;
            //}
        }
    }
}
