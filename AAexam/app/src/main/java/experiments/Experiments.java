package experiments;


import java.util.List;
import java.util.function.IntFunction;

import data.Handler;
import sorting.TopDownMergeSort;
import experiments.Result.Key;

/** The main class to access the experiments library.
 * <p>Provides the {@code measure()} function to run experiments.
 * {@measure} is overloaded such that it can run both simple, multirun, paramterized and multirun parameterized experiments.
 * The returned {@code Measurement} can then be analyzed by calling {@code analyze} on it.
 * The analyzed results, in turn, can be printed by calling {@code print}.</p>
 *
 * <p>This models an experimental pipeline going from {@code Experiment} to {@code Measurement} to {@code Result}.
 * Experiments are modelled as a simple collection of some data, a function on that data that produces an integer result,
 * and a setup function, if needed.
 * A parameterized experiment is a function that takes a specific integer parameter and returns an {@code Experiment}.</p>
 * 
 * <h2>========= Credits =========</h2>
 * <p> Much of this code is inspired by code written by Peter Sestoft for the lecture note
 * Sestoft, Peter. 2015. "Microbenchmarks in Java and C#". (ITU 2013)
 * It has been extensively reworked as an exercise in modeling the experimental proces. </p>
 */
public class Experiments {
    private static final java.text.SimpleDateFormat dateformat 
        = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    private Experiments() {}

    /** Perform a static single run measurement.
     * Run an experiment a specific amount of times and return the measurement.
     * the results reflect the average and standard error of those measurements. */
    public static <T> Measurement measure(Experiment<T> ex, int repetitions) { 
        return new SingleRunMeasurement(ex, repetitions); }

    /** Perform a dynamic single run experiment.
     * Dynamically find a proportional amount repetitions and measure an experiment */
    public static <T> Measurement measure(Experiment<T> ex, double timeLimit) { 
        return new SingleRunMeasurement(ex, timeLimit); }

    /** Perform a static multi run experiment.
     * Measure an experiment a specific amount of times, average the results, and 
     * repeat for a specific amount of runs. The results reflect the average of 
     * averages and the standard deviance of those averages. */
    public static <T> Measurement measure(int runs, Experiment<T> ex, int repetitions) { 
        return new MultiRunMeasurement(runs, ex, repetitions); }

    /** Perform a dynamic multi run experiment.
     * Measure an experiment a dynamic amount of times (based on the timeLimit),
     * average the results, and repeat for a specific amount of runs.
     * The results reflect the average of averages and the standard deviance of those averages. */
    public static <T> Measurement measure(int runs, Experiment<T> ex, double timeLimit) { 
        return new MultiRunMeasurement(runs, ex, timeLimit); }

    /** Perform a parameterized single run experiment. */
    public static <T> Measurement measure(
        IntFunction<Experiment<T>> ex, double timeLimit,
        int pMin, int pMax, double pScale
    ) { return new ParameterizedSingleRunMeasurement(ex, timeLimit, pMin, pMax, pScale); }

    /** Perform a parameterized multi run experiment. */
    public static <T> Measurement measure(
        int runs,IntFunction<Experiment<T>> ex, double timeLimit, 
        int pMin, int pMax, double pScale
    ) { return new ParameterizedMultiRunMeasurement(runs, ex, timeLimit, pMin, pMax, pScale); }

    /** Perform a parameterized single run experiment. */
    public static <T> Measurement measure(
        IntFunction<Experiment<T>> ex, double timeLimit, int pMax) 
        { return new ParameterizedSingleRunMeasurement(ex, timeLimit, pMax); }
    
    /** Perform a parameterized multi run experiment. */
    public static <T> Measurement measure(
        int runs, IntFunction<Experiment<T>> ex, double timeLimit, int pMax) 
        { return new ParameterizedMultiRunMeasurement(runs, ex, timeLimit, pMax); }

    /** Perform a parameterized single run experiment. */
    public static <T> Measurement measure(
        IntFunction<Experiment<T>> ex, double timeLimit, int pMin, int pMax) 
        { return new ParameterizedSingleRunMeasurement(ex, timeLimit, pMin, pMax); }
    
    /** Perform a parameterized multi run experiment. */
    public static <T> Measurement measure(
        int runs, IntFunction<Experiment<T>> ex, double timeLimit, int pMin, int pMax) 
        { return new ParameterizedMultiRunMeasurement(runs, ex, timeLimit, pMin, pMax); }


    
    /** Prints system info */
    public static void systemInfo() {
        final Runtime runtime = Runtime.getRuntime();
        System.out.printf("# OS: %s; %s; %s%n",
                          get("os.name"),
                          get("os.version"),
                          get("os.arch"));
        System.out.printf("# JVM: %s; %s%n",
                          get("java.vendor"),
                          get("java.version"));
        // The processor identifier works only on MS Windows:
        System.out.printf("# CPU: %s; %d \"procs\"%n",
                          System.getenv("PROCESSOR_IDENTIFIER"),
                          runtime.availableProcessors());
        final java.util.Date now = new java.util.Date();
        System.out.printf("# Date: %s%n", dateformat.format(now));
    }

    /** Unimportant helper method for systemInfo()*/
    private static String get(String property) { return System.getProperty(property); }

    /** Unimportant test function */
    public static int multiply(int i) {
        double x = 1.1 * (double)(i & 0xFF);
        return (int) (x * x * x * x * x * x * x * x * x * x * x * x * x * x * x * x * x * x * x * x);
    }

    // ========================   Main   ==========================

    /** Demonstration of experimental pipeline in 4 experiments */
    public static void main(String[] args) {

        // ========== Setup

        Result r;
        Experiment<Integer> iEx;
        Experiment<Integer[]> ex;
        IntFunction<Experiment<Integer[]>> gen;
        Integer[] data;

        Experiments.systemInfo();
        System.out.println();

        System.out.println(Result.resultHeaders());

        // ========== Experiment 1: Simple integer multiplication

        iEx = new Experiment<Integer>(i -> i, Experiments::multiply, i -> i);
        r = Experiments.measure(iEx, 0.1).analyze("multiply");
        r.removeKeys(List.of(Key.MEANRESULT, Key.SDEVRESULT));
        System.out.println(r);


        // =========== Experiment 2: Mergesort [parameterized]

        gen = i -> new Experiment<Integer[]>(
            Handler.generate(i, j -> j),
            TopDownMergeSort::sort, 
            Handler::randomize
        );

        Experiments.measure(gen, 0.25, 100, 100_000, 2.0)
                   .analyze("singlerunsort")
                   .print();


        // Experiment 3: Compare the effect of input on the number of comparisons and running time.

        // Experiment 3A: Mergesort with different input

        data = Handler.generate(50_000, j -> j);

        ex = new Experiment<>(
            data, TopDownMergeSort::sort, j -> j);

        r = Experiments.measure(ex, 0.25).analyze("sorted");
        r.put(Key.PARAMETER, (double) 50_000);
        System.out.println(r);


        // Experiment 3B: Mergesort with different input

        ex = new Experiment<>(
            data, TopDownMergeSort::sort, j -> Handler.randomize(j, 20));

        r = Experiments.measure(ex, 0.25).analyze("20pRandom");
        r.put(Key.PARAMETER, (double) 50_000);
        System.out.println(r);


        // Experiment 3C: Mergesort with different input

        ex = new Experiment<>(
            data, TopDownMergeSort::sort, Handler::invert);

        r = Experiments.measure(ex, 0.25).analyze("inverted");
        r.put(Key.PARAMETER, (double) 50_000);
        System.out.println(r);


        // =========== Experiment 4: Complex mergesort [parameterized, multiple runs]

        Experiments.measure(10, gen, 0.25, 100, 100_000, 2.0)
                   .analyze("sort")
                   .print();
    }
}

