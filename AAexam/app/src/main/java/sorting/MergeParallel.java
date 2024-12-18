package sorting;

public class MergeParallel {
    
    public record IntPair(int a, int b) { }

    //public static <T extends Comparable<? super T>> int merge(T[] a, T[] aux, int lo, int mid, int hi, int p) {
    //    int n = lo + hi + 1;
    //
    //}
    
    public static <T extends Comparable<? super T>> IntPair twoSequenceSelect(T[] array, IntPair a, IntPair b, int k) {
        final int la = a.b - a.a + 1;
        final int lb = b.b - b.a + 1;
        if (k < 0 || k > la + lb) throw new IllegalArgumentException("k is out of bounds");
        System.out.printf("k is %d: ", k);

        int j_b, j_a = (k+1)/2;
        boolean propA = false, propB = false;
        do {
            assert(0 <= j_a && j_a <= k);
            j_b = k - j_a;
            System.out.printf("%d, %d --> ", j_a, j_b);
            propA = (j_a == 0 || j_b == lb) ? true : (array[a.a+j_a-1].compareTo(array[b.a+j_b]) <= 0 );
            if (!propA) { j_a = (j_a+1)/2; continue; }
            System.out.printf("propA --> ");
            propB = (j_b == 0 || j_a == la) ? true : (array[b.a+j_b-1].compareTo(array[a.a+j_a]) <= 0 );
            if (!propB) { j_a = j_a*2; continue; }
            System.out.printf("propB --> ");
        } while (!(propA && propB));
        return new IntPair(j_a, j_b);
    }

    public static void main(String[] args) {
        Integer[] n = {1, 4, 4, 2, 3, 4, 6};
        //             0, 1, 2||0, 1, 2, 3
        //             -------------------
        //             1, 2, 3, 4, 4, 4, 6
        //             0, 1, 2, 3, 4, 5, 6
        //             k = 0 -> 0, 0 (a!1)
        //                 1 -> 1, 0 (b!2)
        //                 2 -> 1, 1 (b!3)
        //                 3 -> 1, 2 (a!4)
        //                 4 -> 2, 2 (a!4)
        //                 5 -> 3, 2 (b!4)
        //                 6 -> 3, 3 (b!6)
        for (int i = 0; i < 7; i++) {
            IntPair x = twoSequenceSelect(n, new IntPair(0, 2), new IntPair(3, 6), i);
            System.out.println(x.a + ", " + x.b);
        }

    }
}
