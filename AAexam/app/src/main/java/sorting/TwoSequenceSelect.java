package sorting;

public class TwoSequenceSelect {
    
    public static <T extends Comparable<? super T>> void twoSequenceSelect(T[] a, T[] b, int k) {
        int la = a.length, lb = b.length;
        if (k < 0 || k > la + lb) throw new IllegalArgumentException( "k is out of bounds");
    }
}
