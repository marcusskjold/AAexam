package sorting;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;


public class FullRecursiveMergeSortParallel {
    private static ForkJoinPool executor = new ForkJoinPool();

    public static <T extends Comparable<? super T>> int sort(T[] a, int c) {
        System.out.println("Start");
        T[] aux = a.clone();
        int compares = executor.invoke(new MergeSortTask<T>(a, aux, 0, a.length-1, c));
        System.out.println(Arrays.toString(a));
        System.out.println(Arrays.toString(aux));
        return compares;
    }

    public static <T extends Comparable<? super T>> int sort(T[] a, T[] aux, int lo, int hi) {
        System.out.println("Seq sort! " + lo + " + " + hi);
        if (hi <= lo) return 0;

        int mid = lo + (hi - lo) / 2;
        System.out.println("mid " + mid);
        int comparesLeft = sort(a, aux, lo, mid);
        int comparesRight = sort(a, aux, mid + 1, hi);
        int comparesMerge = merge(a, aux, lo, mid, hi);
        return comparesLeft + comparesRight + comparesMerge;
    }

    public static <T extends Comparable<? super T>> int merge(T[] a, T[] aux, int lo, int mid, int hi) {
        System.out.printf("merge with %d %d %d%n", lo, mid, hi);
        int compares = 0;
        int left = lo, right = mid+1;
        for (int k = lo; k <= hi; k++) {
            System.out.println("Pre: " + Arrays.toString(a) + Arrays.toString(aux) + "left = " + left + " right = " + right);
            if      (left > mid)                             a[k] = aux[right++]; 
            else if (right > hi)                             a[k] = aux[left++]; 
            else if (aux[right].compareTo(aux[left]) < 0 ) {{System.out.println(aux[right] + " " + right);a[k] = aux[right++]; compares++;}
                System.out.println("match"); }
            else                                            {a[k] = aux[left++]; compares++;}                            
            System.out.println("Rec: " + Arrays.toString(a) + Arrays.toString(aux) + "left = " + left + " right = " + right);
        } return compares;
    }

    public static class MergeSortTask<T extends Comparable<? super T>> extends RecursiveTask<Integer> {
        private T[] a, aux;
        int compares, low, high, c;

        public MergeSortTask(T[] a, T[] aux, int lo, int hi, int c) {
            System.out.println("new Task!");
            System.out.printf("lo = %d, hi = %d%n", lo, hi);
            this.a = a; low = lo; high = hi; this.c = c; this.aux = aux;
        }

        @Override protected Integer compute() {
            System.out.println("Computing");
            if (high - low < c) return sort(a, aux, low, high);
            int mid = low + (high - low) / 2;
            int r = 0, l = 0;
            if (high-low > 0) {
                ForkJoinTask<Integer> fork  = new MergeSortTask<T>(a, aux, low, mid, c).fork();
                r = new MergeSortTask<T>(a, aux, mid+1, high, c).invoke();
                l = fork.join();
            }
            int m = MergeParallel.merge(a, aux, low, mid, high, 2); // TODO: Dynamically set merge parameter

            return r + l + m;
        }
    }
}
