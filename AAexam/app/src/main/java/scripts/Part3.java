package scripts;

import sorting.RecursiveMergeSortParallel;
import sorting.TopDownMergeSort;

import static data.Handler.generate;
import static data.Handler.randomize;
import static data.Handler.inPlaceRandomize;
import static sorting.Util.isSorted;
import static experiments.Experiments.measure;
import static experiments.Result.resultHeaders;
import static experiments.Result.Key;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.function.IntFunction;

import data.Handler;
import data.TestData;
import experiments.Experiment;
import experiments.Experiments;
import experiments.Result;
import experiments.Measurement.Timer;
import sorting.MergeParallel;
import sorting.Merge;

public class Part3 {
    private final static double MEDIUMTIME = 0.5, LONGTIME = 2.0;

    public static void print(String s) { System.out.println(s); }
    public static void print()         { System.out.println(); }
    public static void main(String[] args) {

        System.out.println("System info: ");
        Experiments.systemInfo();
        System.out.println();

        task11(); 
        task12();
        task13();
        task14();
        task15();
        task16();
        task17();
    }

    // ==================================================================
    // Task 11
    // ==================================================================

    public static void task11() {
        print("=====================================================================");
        print("Task 11: Implement a parallel version of the recursive MergeSort from Task 1");
        print("=====================================================================");
        print();

        int n = 16_000_000, c = 1_000_000;
        Integer[] pInput = generate(n, i -> i);
        inPlaceRandomize(pInput);
        Integer[] sInput = pInput.clone();
        Timer t = new Timer();
        t.play();
        int pComparisons = RecursiveMergeSortParallel.sort(pInput, c);
        long pTime = t.check();
        System.out.printf(
            "Parallel sort %,d size array in %,5.2f seconds, and with %,d comparisons - cutoff %,d%n",
            n, pTime/1e9, pComparisons, c);
        if (!isSorted(pInput)) throw new AssertionError(
            "Postcondition failed: Returned data is not sorted!");

        t.play();
        int sComparisons = TopDownMergeSort.sort(sInput);
        long sTime = t.check();
        System.out.printf(
            "Sequential sort %,d size array in %,5.2f seconds, and with %,d comparisons%n",
            n, sTime/1e9, sComparisons);
        if (!isSorted(pInput)) throw new AssertionError(
            "Postcondition failed: Returned data is not sorted!");
    }

    // ==================================================================
    // Task 12
    // ==================================================================

    public static void task12() {
        print("=====================================================================");
        print("Task 12: Judge potential speedups and find a good choice of the parallel-cut-off parameter");
        print("=====================================================================");
        print();
        print("Results summary:");
        print("We see pretty clearly that the results improve up untill the point where the ratio between");
        print("the size of the array and the cutoff parameter exceeds the number of available processors.");
        print("n / c >= Runtime.getruntime.availableprocessors()");
        print("This is in line with expectations: Parallelism should give improvement up until around the point where");
        print("There are as many tasks ready as processors available");
        print();
        print("Potential speedup: Set the parallel cutoff automatically relative to cores and n.");
        print("Potential speedup: Run sequential mergesort with an optimal cutoff (~25).");
        print();
        print("This behavior is consistent across a range of array sizes for randomized sequences.");
        print("Results on our machines suggest that the speedup is stronger for higher array sizes.");

        double T12TIME = 8.0;

        t12e1(   256_000, 2.0, 6, T12TIME, "t12e1_0");
        t12e1(   512_000, 2.0, 6, T12TIME, "t12e1_1");
        t12e1( 1_024_000, 2.0, 6, T12TIME, "t12e1_2");
        t12e1( 2_000_000, 2.0, 6, T12TIME, "t12e1_3");
        t12e1( 4_000_000, 2.0, 6, T12TIME, "t12e1_4");
        t12e1( 8_000_000, 2.0, 6, T12TIME, "t12e1_5");
        t12e1(16_000_000, 2.0, 6, T12TIME, "t12e1_6");
        t12e1(32_000_000, 2.0, 6, T12TIME, "t12e1_7");


    }

    public static void t12e1(int n, double s, int e, double time, String title) {
        IntFunction<Experiment<Integer[]>> ex = parameterValue -> new Experiment<>(
            generate(n, i -> i),
            d -> RecursiveMergeSortParallel.sort(d, parameterValue),
            Handler::randomize
        );

        print();
        System.out.printf("For randomized Integer array of length %,d%n",n);
        print("With param being the cutoff for applying sequential mergesort");
        print(resultHeaders());
        int lowerBound = (int) (n / (Math.pow(s, e)));
        measure(ex, time, lowerBound, n+1, s).analyze(title).saveAsCSV().print();
        print();
    }

    // ==================================================================
    // Task 13
    // ==================================================================

    public static void task13() {
        print("=====================================================================");
        print("Task 13: Implement twoSequenceSelect() with binary search.");
        print("=====================================================================");
        print();

        print("Our implementation has the signature MergeParallel.twoSequenceSelect(T[] in, int lo, int mid, int hi, int k)");
        print("Rather than use explicit sequences, as per the task description, we translate implicitly from a normal");
        print("merge range, as it is used in our sequential implementation (See the javadoc for specifics).");
        print("The result given is a pair of integers: i_a, from which i_b can be derived, as well as the comparison count.");
        print("to use i_a and i_b, they must be translated back into indexes in the input array (i_a+lo, i_b+mid+1)");
        print();
        print("");

        Integer[] in = {6, 5, 1, 4, 4, 2, 3, 4, 6};
        //                    0, 1, 2||0, 1, 2, 3    The indexes of sequences a and b seq
        //                    -------------------
        //                    1, 2, 3, 4, 4, 4, 6    The merged output
        //              0  1  2, 3, 4, 5, 6, 7, 8    The indexes
        //                    k = 0 -> 0, 0 (a!1)    Trace of twoSequenceSelect calculation
        //                        1 -> 1, 0 (b!2)    k_j -> i_a_j, i_b_j (the sequence ! element chosen)
        //                        2 -> 1, 1 (b!3)
        //                        3 -> 1, 2 (a!4)
        //                        4 -> 2, 2 (a!4)
        //                        5 -> 3, 2 (b!4)
        //                        6 -> 3, 3 (b!6)

        if (MergeParallel.twoSequenceSelect(in, 2, 4, 8, 0).a() != 0 ||
            MergeParallel.twoSequenceSelect(in, 2, 4, 8, 1).a() != 1 ||
            MergeParallel.twoSequenceSelect(in, 2, 4, 8, 2).a() != 1 ||
            MergeParallel.twoSequenceSelect(in, 2, 4, 8, 3).a() != 1 ||
            MergeParallel.twoSequenceSelect(in, 2, 4, 8, 4).a() != 2 ||
            MergeParallel.twoSequenceSelect(in, 2, 4, 8, 5).a() != 3 ||
            MergeParallel.twoSequenceSelect(in, 2, 4, 8, 6).a() != 3)
            throw new AssertionError("twoSequenceSelect gives wrong output.");

        print("twoSequenceSelect gives correct output for a test array.");
        print();
    }

    // ==================================================================
    // Task 14
    // ==================================================================

    public static void task14() {
        print("=====================================================================");
        print("Task 14: ");
        print("=====================================================================");
        print();

        print("We have implemented a parallel version of merge using twoSequenceSelect to");
        print("split the input merge sequence into p parts.");
        print("The parts are created with as even size as possible, by rounding increment value");

        Integer[] a = new Integer[]{7,2,3,5,7,10,4,5,5,6,5,4,3,2,1};
        Integer[] aux = a.clone();
        MergeParallel.merge(a, aux, 1, 5, 9, 2);
        if (!isSorted(a, 1, 9)) throw new AssertionError("Merge was unsuccessful.");
        print();
    }

    // ==================================================================
    // Task 15
    // ==================================================================


    public static void task15() {
        print("=====================================================================");
        print("Task 15: ");
        print("=====================================================================");
        print();

        print("Our version of parallel merge sort using parallel merging can be used by specifying a");
        print("p parameter > 1 when calling the function.");
        print("We divide p for each layer down the recursive tree we walk. This is to avoid generating");
        print("wasteful amounts of parallel tasks, which would probably slow down the implementation.");
        print("If p falls below 2, there is no point in using a parallel framework, and we switch to");
        print("sequencial merging.");

        int n = 16_000_000, c = 1_000_000;
        Integer[] pInput = generate(n, i -> i);
        inPlaceRandomize(pInput);
        Integer[] sInput = pInput.clone();
        Timer t = new Timer();
        t.play();
        int pComparisons = RecursiveMergeSortParallel.sort(pInput, c, 8); // 8 is p
        long pTime = t.check();
        System.out.printf(
            "Parallel sort %,d size array in %,5.2f seconds, and with %,d comparisons - cutoff %,d%n",
            n, pTime/1e9, pComparisons, c);
        if (!isSorted(pInput)) throw new AssertionError(
            "Postcondition failed: Returned data is not sorted!");

        t.play();
        int sComparisons = TopDownMergeSort.sort(sInput);
        long sTime = t.check();
        System.out.printf(
            "Sequential sort %,d size array in %,5.2f seconds, and with %,d comparisons%n",
            n, sTime/1e9, sComparisons);
        if (!isSorted(pInput)) throw new AssertionError(
            "Postcondition failed: Returned data is not sorted!");
        print();
    }

    // ==================================================================
    // Task 16
    // ==================================================================


    public static void task16() {
        print("=====================================================================");
        print("Task 16: Investigate the scaling behavior of your implementations when the");
        print("number of available threads change.");
        print("=====================================================================");
        print();

        int activeThreads = Thread.activeCount();
        int availableProcs = Runtime.getRuntime().availableProcessors();
        print(String.format("active threads: %d. Available processors: %d", activeThreads, availableProcs));
        print();
        print("We see that in no case does our implementation beat Arrays.parallelSort(). However, we see that");
        print("the performance of our implementation does improve as we allow for more threads to run in");
        print("parallel.");

        int n = 4_000_000;
        Integer[] in = generate(n, i -> i);

        IntFunction<Experiment<Integer[]>> ex1 = parameterValue -> new Experiment<>(
            in,
            d ->   RecursiveMergeSortParallel.sort(d, n/availableProcs, availableProcs),
            a -> { RecursiveMergeSortParallel.setPool(new ForkJoinPool(parameterValue));
                   return Handler.randomize(a);});

        Experiment<Integer[]> ex2 = new Experiment<>(
            in.clone(),
            d -> {Arrays.parallelSort(d); return d.hashCode();},
            Handler::randomize);

        print(Result.resultHeaders());
        Experiments.measure(ex2, LONGTIME).analyze("t16_e1_control").saveAsCSV().print();
        Experiments.measure(ex1, LONGTIME, 1, 16).analyze("t16_e1_recursive").saveAsCSV().print();

    }

    // ==================================================================
    // Task 17
    // ==================================================================


    public static void task17() {
        print("=====================================================================");
        print("Task 17: ");
        print("=====================================================================");
        print();

        print("We have implemented the functionality for calculating the span.");
        print("But we ran out of time while designing the experiment.");
        print();
        print("Here is a test case showing that the correct span is returned:");

        Integer[] a = {4,2,3,1,5,6,7};
        Integer[] aux = a.clone();
        int span = RecursiveMergeSortParallel.sort(a, 4, 0, true);
        int lc = TopDownMergeSort.sort(new Integer[]{4,2,3,1});
        int rc = TopDownMergeSort.sort(new Integer[]{5,6,7});
        int mc = Merge.merge(a, aux, 0, 3, 6);
        int expectedcmp = Math.max(lc, rc) + mc;
        System.out.printf("Spans: left sort: %d Right sort: %d Merge: %d%nExpected span: %d, actual span returned: %d%n",
                            lc,rc,mc,expectedcmp,span);

    }

}
