package scripts;

import sorting.RecursiveMergeSortParallel;
import sorting.TopDownMergeSort;

import static data.Handler.generate;
import static data.Handler.randomize;
import static data.Handler.inPlaceRandomize;
import static sorting.Util.isSorted;
import static experiments.Experiments.measure;
import static experiments.Result.resultHeaders;

import java.util.function.IntFunction;

import data.Handler;
import data.TestData;
import experiments.Experiment;
import experiments.Experiments;
import experiments.Result;
import experiments.Measurement.Timer;

public class Part3 {
    private final static double MEDIUMTIME = 0.5, LONGTIME = 2.0;

    public static void print(String s) { System.out.println(s); }
    public static void print()         { System.out.println(); }
    public static void main(String[] args) {

        System.out.println("System info: ");
        Experiments.systemInfo();
        System.out.println();

        //task11(); 
        //task12();
        //task13();
        //task14();
        //task15();
        task16();
        //task17();
    }

    // ==================================================================
    // Task 11
    // ==================================================================
    public static void task11() {
        print("=====================================================================");
        print("Task 11: Implement a parallel versuin of the recursive MergeSort from Task 1");
        print("=====================================================================");
        print();

        int n = 8_000_000, c = 200_000;
        Integer[] pInput = generate(n, i -> i);
        inPlaceRandomize(pInput);
        Integer[] sInput = pInput.clone();
        Timer t = new Timer();
        t.play();
        int pComparisons = RecursiveMergeSortParallel.sort(pInput, c);
        long pTime = t.check();
        System.out.printf(
            "  Parallel sort %,d size array in %,5.2f seconds, and with %,d comparisons - cutoff %,d%n",
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

        t12e1(   256_000, 2.0, 6, 8.0, "t12e1_0");
        t12e1(   512_000, 2.0, 6, 8.0, "t12e1_1");
        t12e1( 1_024_000, 2.0, 6, 8.0, "t12e1_2");
        t12e1( 2_000_000, 2.0, 6, 8.0, "t12e1_3");
        t12e1( 4_000_000, 2.0, 6, 8.0, "t12e1_4");
        t12e1( 8_000_000, 2.0, 6, 8.0, "t12e1_5");
        t12e1(16_000_000, 2.0, 6, 8.0, "t12e1_6");
        t12e1(32_000_000, 2.0, 6, 8.0, "t12e1_7");

        // Potential speedup: Set the parallel cutoff automatically relative to cores and n.
        // Potential speedup: Run sequential mergesort with an optimal cutoff (~25).
        // The speedup is perhaps higher for higher n?

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

    public static void task13() {

    }

    public static void task14() {

    }

    public static void task15() {

    }

    public static void task16() {
        int activeThreads = Thread.activeCount();
        int availableProcs = Runtime.getRuntime().availableProcessors();
        print(String.format("active threads: %d. Available processors: %d", activeThreads, availableProcs));

    }

    public static void task17() {

    }
}
