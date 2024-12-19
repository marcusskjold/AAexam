package sorting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class MergeParallel {
    private static final ForkJoinPool ex = new ForkJoinPool();
    
    public record IntPair(int a, int b) { }

    public static <T extends Comparable<? super T>> int merge(T[] a, T[] aux, int lo, int mid, int hi, int p) {
        int n = hi - lo + 1, compares = 0;
        double increment = n / (double) p;
        p--;
        //System.out.printf("Increment = %f, p = %d%n", increment, p);
        if (increment < 2) return Merge.merge(a, aux, lo, mid, hi);
        List<Callable<Integer>> tasks = new ArrayList<>();
        
        //int loc = 
        for (int i = 0; i < p; i++) {
            int k = (int)(i*increment);
            IntPair is = twoSequenceSelect(a, lo, mid, hi, k);
            int l = lo + is.a;
            int r = mid + is.b + 1;
            int o = lo + k;
            int length = (int)((i+1)*increment)-k;
            //System.out.printf("Length = %d%n", length);
            tasks.add(() -> { return mergeTask(a, aux, l, r, o, length); });
        }
        int k = (int)(p*increment);
        //System.out.println("Hey");
        IntPair is = twoSequenceSelect(a, lo, mid, hi, k);
        int l = lo + is.a;
        int r = mid + is.b + 1;
        int o = lo + k;
        int length = (int)((p+1)*increment)-k;
        //System.out.printf("End Length = %d%n", length);
            tasks.add(() -> { return mergeEndTask(a, aux, l, r, o, length, mid, hi); });
        try { for (Future<Integer> fut : ex.invokeAll(tasks)) compares += fut.get();
        } catch (InterruptedException exn) { System.out.println("Interrupted: " + exn);
        } catch (ExecutionException exn) { throw new RuntimeException(exn.getCause()); }
        return compares;
    }
    
    public static <T extends Comparable<? super T>> int mergeEndTask(
        T[] a, T[] aux, int i_a, int i_b, int i_o, int length, int mid, int hi) {
        int compares = 0;
        //System.out.println("Running!");
        //System.out.printf("i_a = %d, i_b = %d, i_o = %d, length = %d%n", i_a, i_b, i_o, length);

        // We need to make it have output arrays – this might 
        // Or we need to return aux instead of a
        // This last task is different from the others, because we may step out of bounds.


        int k, l = i_a, r = i_b, end = i_o+length;
        for (k = i_o; k < end; k++) {
            //System.out.printf("k = %d, l = %d, r = %d - ", k, l, r);
            if      (l > mid)                        a[k] = aux[r++]; 
            else if (r > hi)                         a[k] = aux[l++]; 
            else if (aux[r].compareTo(aux[l]) < 0 ) {a[k] = aux[r++]; compares++;}
            else                                    {a[k] = aux[l++]; compares++;}                            
            //System.out.println();
        }

        // postcondition: a[lo .. hi] is sorted
        //assert Util.isSorted(a, i_o, end - 1);
        //return number of compares
        return compares;
    }

    public static <T extends Comparable<? super T>> int mergeTask(
        T[] a, T[] aux, int i_a, int i_b, int i_o, int length) {
        //System.out.println("Running!");
        //System.out.printf("i_a = %d, i_b = %d, i_o = %d, length = %d", i_a, i_b, i_o, length);
        int compares = 0;

        // We need to make it have output arrays – this might 
        // Or we need to return aux instead of a

        int l = i_a, r = i_b, end = i_o+length;
        int k;
        //System.out.printf("i_o = %d, length = %d", i_o, length);
        for (k = i_o; k < end; k++) {
            //System.out.println("Jumpint!");
            if (aux[r].compareTo(aux[l]) < 0 ) {a[k] = aux[r++]; compares++;}
            else                               {a[k] = aux[l++]; compares++;}                            
        }

        //assert Util.isSorted(a, i_o, end - 1);
        return compares;
    }
    public static <T extends Comparable<? super T>> IntPair twoSequenceSelect(T[] array, int lo, int mid, int hi, int k) {
        System.out.printf("k is %d: ", k);
        final int l_a = mid - lo + 1;
        System.out.printf(" l_a = %d ", l_a);
        final int l_b = hi - mid;
        if (k < 0 || k > l_a + l_b) throw new IllegalArgumentException("k is out of bounds");

        int j_b, j_a;
        boolean propA = false, propB = false;
        int lo_a = 0, hi_a = k;
        do  {
            j_a = lo_a + ((hi_a-lo_a)/2);
            assert(0 <= j_a && j_a <= k);
            j_b = k - j_a;
            System.out.printf("%d, %d --> ", j_a, j_b);
            propA = (j_a == 0 || j_b == l_b) ? true : (array[lo+j_a-1].compareTo(array[mid+1+j_b]) <= 0 );
            if (!propA) { hi_a = j_a-1; continue; }
            System.out.printf("propA --> ");
            propB = (j_b == 0 || j_a == l_a) ? true : (array[mid+1+j_b-1].compareTo(array[lo+j_a]) <= 0 );
            if (!propB) { lo_a = j_a+1; continue; }
            System.out.printf("propB --> ");
            break;
        } while (lo_a <= hi_a);
        System.out.println("Done!");
        System.out.println(Arrays.toString(array));
        System.out.println(j_a + ", " + j_b);
        return new IntPair(j_a, j_b);
    }

    public static <T extends Comparable<? super T>> IntPair twoSequenceSelect(T[] array, IntPair a, IntPair b, int k) {
        final int la = a.b - a.a + 1;
        final int lb = b.b - b.a + 1;
        if (k < 0 || k > la + lb) throw new IllegalArgumentException("k is out of bounds");
        //System.out.printf("k is %d: ", k);

        int j_b, j_a = (k+1)/2;
        boolean propA = false, propB = false;
        do {
            assert(0 <= j_a && j_a <= k);
            j_b = k - j_a;
            //System.out.printf("%d, %d --> ", j_a, j_b);
            propA = (j_a == 0 || j_b == lb) ? true : (array[a.a+j_a-1].compareTo(array[b.a+j_b]) <= 0 );
            if (!propA) { j_a = (j_a+1)/2; continue; }
            //System.out.printf("propA --> ");
            propB = (j_b == 0 || j_a == la) ? true : (array[b.a+j_b-1].compareTo(array[a.a+j_a]) <= 0 );
            if (!propB) { j_a = j_a*2; continue; }
            //System.out.printf("propB --> ");
        } while (!(propA && propB));
        //System.out.println(j_a + ", " + j_b);
        return new IntPair(j_a, j_b);
    }

    public static void main(String[] args) {
        Integer[] n = {6, 5, 1, 4, 4, 2, 3, 4, 6};
        //                   0, 1, 2||0, 1, 2, 3
        //                   -------------------
        //                   1, 2, 3, 4, 4, 4, 6
        //             0  1  2, 3, 4, 5, 6, 7, 8
        //                   k = 0 -> 0, 0 (a!1)
        //                       1 -> 1, 0 (b!2)
        //                       2 -> 1, 1 (b!3)
        //                       3 -> 1, 2 (a!4)
        //                       4 -> 2, 2 (a!4)
        //                       5 -> 3, 2 (b!4)
        //                       6 -> 3, 3 (b!6)
        for (int i = 0; i < 7; i++) {
            IntPair x = twoSequenceSelect(n, new IntPair(2, 4), new IntPair(5, 8), i);
            IntPair y = twoSequenceSelect(n, 2, 4, 8, i);
        } 

        Integer[] aux = n.clone();
        merge(n, aux, 2, 4, 8, 3);
        //System.out.println(Arrays.toString(aux));
        //System.out.println(Arrays.toString(n));
    }
}


