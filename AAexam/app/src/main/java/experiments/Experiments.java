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
     * the results reflect the average and standard error of those measurements. 
     * @param ex The Experiment to measure
     * @param repetitions the amount of times to run and observe the experiment
     * */
    public static <T> Measurement measure(Experiment<T> ex, int repetitions) { 
        return new SingleRunMeasurement(ex, repetitions); }

    /** Perform a dynamic single run experiment.
     * Dynamically find a proportional amount repetitions and measure an experiment
     * @param ex The Experiment to measure
     * @param timeLimit Find the repetitions by doubling from 2 until the total running
     *                  time exceeds this value in seconds.
     */
    public static <T> Measurement measure(Experiment<T> ex, double timeLimit) { 
        return new SingleRunMeasurement(ex, timeLimit); }

    /** Perform a static multi run experiment.
     * Measure an experiment a specific amount of times, average the results, and 
     * repeat for a specific amount of runs. The results reflect the average of 
     * averages and the standard deviance of those averages.
     * @param ex The Experiment to measure
     * @param repetitions the amount of times to run and observe the experiment
     */
    public static <T> Measurement measure(int runs, Experiment<T> ex, int repetitions) { 
        return new MultiRunMeasurement(runs, ex, repetitions); }

    /** Perform a dynamic multi run experiment.
     * Measure an experiment a dynamic amount of times (based on the timeLimit),
     * average the results, and repeat for a specific amount of runs.
     * The results reflect the average of averages and the standard deviance of those averages.
     * @param runs This parameter specifies that the measurement should be performed {@code runs}
     *             amount of times. The results of each observation will be squashed into an average
     *             across all the repetitions of that run. Useful for very short operations with large
     *             variations in running time.
     * @param ex The Experiment to measure
     * @param timeLimit Find the repetitions by doubling from 2 until the total running
     *                  time exceeds this value in seconds.
     */
    public static <T> Measurement measure(int runs, Experiment<T> ex, double timeLimit) { 
        return new MultiRunMeasurement(runs, ex, timeLimit); }

    /** Perform a parameterized single run experiment.
     * This is a version where the parameter grows by a scaling factor.
     * @param ex A function that generates Experiments from parameter values
     * @param timeLimit Find the repetitions by doubling from 2 until the total running
     *                  time exceeds this value in seconds.
     * @param pMin The minimum value of the parameter.
     * @param pMax The maximim value of the parameter.
     * @param pScale The factor to scale the parameter value.
     */
    public static <T> Measurement measure(
        IntFunction<Experiment<T>> ex, double timeLimit,
        int pMin, int pMax, double pScale
    ) { return new ParameterizedSingleRunMeasurement(ex, timeLimit, pMin, pMax, pScale); }

    /** Perform a parameterized multi run experiment. 
     * @param runs This parameter specifies that the measurement should be performed {@code runs}
     *             amount of times. The results of each observation will be squashed into an average
     *             across all the repetitions of that run. Useful for very short operations with large
     *             variations in running time.
     * @param ex A function that generates Experiments from parameter values
     * @param timeLimit Find the repetitions by doubling from 2 until the total running
     *                  time exceeds this value in seconds.
     * @param pMin The minimum value of the parameter.
     * @param pMax The maximim value of the parameter.
     * @param pScale The factor to scale the parameter value.
     */
    public static <T> Measurement measure(
        int runs,IntFunction<Experiment<T>> ex, double timeLimit, 
        int pMin, int pMax, double pScale
    ) { return new ParameterizedMultiRunMeasurement(runs, ex, timeLimit, pMin, pMax, pScale); }

    /** Perform a parameterized single run experiment.
     * This is a version where the parameter value sequencially increases.
     * @param ex A function that generates Experiments from parameter values
     * @param timeLimit Find the repetitions by doubling from 2 until the total running
     *                  time exceeds this value in seconds.
     * @param pMax The maximim value of the parameter. The parameter values will be the 
     *             sequence 1 ... pMax including.
     */
    public static <T> Measurement measure(
        IntFunction<Experiment<T>> ex, double timeLimit, int pMax) 
        { return new ParameterizedSingleRunMeasurement(ex, timeLimit, pMax); }
    
    /** Perform a parameterized multi run experiment.
     * This is a version where the parameter value sequencially increases.
     * WARNING: This can be very slow, as the work per parameter value is 
     * runs * repetition * experiment.
     * @param runs This parameter specifies that the measurement should be performed {@code runs}
     *             amount of times. The results of each observation will be squashed into an average
     *             across all the repetitions of that run. Useful for very short operations with large
     *             variations in running time.
     * @param ex A function that generates Experiments from parameter values
     * @param timeLimit Find the repetitions by doubling from 2 until the total running
     *                  time exceeds this value in seconds.
     * @param pMax The maximim value of the parameter.
     */
    public static <T> Measurement measure(
        int runs, IntFunction<Experiment<T>> ex, double timeLimit, int pMax) 
        { return new ParameterizedMultiRunMeasurement(runs, ex, timeLimit, pMax); }

    /** Perform a parameterized single run experiment.
     * This is a version where the parameter values will be the sequence
     * pMin ... pMax inclusive.
     * @param ex A function that generates Experiments from parameter values
     * @param timeLimit Find the repetitions by doubling from 2 until the total running
     *                  time exceeds this value in seconds.
     * @param pMin The minimum value of the parameter.
     * @param pMax The maximim value of the parameter.
     */
    public static <T> Measurement measure(
        IntFunction<Experiment<T>> ex, double timeLimit, int pMin, int pMax) 
        { return new ParameterizedSingleRunMeasurement(ex, timeLimit, pMin, pMax); }
    
    /** Perform a parameterized multi run experiment.
     * This is a version where the parameter values will be the sequence
     * pMin ... pMax inclusive.
     * WARNING: This can be very slow, as the work per parameter value is 
     * runs * repetition * experiment.
     * @param runs This parameter specifies that the measurement should be performed {@code runs}
     *             amount of times. The results of each observation will be squashed into an average
     *             across all the repetitions of that run. Useful for very short operations with large
     *             variations in running time.
     * @param ex A function that generates Experiments from parameter values
     * @param timeLimit Find the repetitions by doubling from 2 until the total running
     *                  time exceeds this value in seconds.
     * @param pMin The minimum value of the parameter.
     * @param pMax The maximim value of the parameter.
     */
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
}
