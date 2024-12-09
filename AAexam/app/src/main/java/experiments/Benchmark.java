package experiments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;

import data.Handler;
import sorting.TopDownMergeSort;

/**
 * Benchmark
 * Much of this code is adapted from code written by Peter Sestoft for the lecture note
 * Sestoft, Peter. 2015. "Microbenchmarks in Java and C#". (ITU 2013)
 */
public class Benchmark {

    private static final java.text.SimpleDateFormat dateformat 
        = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public record Experiment<T>(IntFunction<T> input, ToIntFunction<T> run, UnaryOperator<T> setup) { }
    public record Result(double mean, double stdev, int repetitions, int runs, List<Measurement> results) { }
    public record Measurement(long time, int[] result) { }

    public static class Timer {
        private long start, spent = 0;
        public Timer()        { }
        public long check()   { return  (System.nanoTime()-start+spent); }
        public void pause()   { spent += System.nanoTime()-start; }
        public void play()    { start  = System.nanoTime(); }
    }
    
    /** Prints system info
     * copied from Sestoft 2015
     */
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

    public static String get(String property) { return System.getProperty(property); }

    public static <T> Measurement measure(int repetitions, Experiment<T> ex) {
        Timer t = new Timer();
        int[] results = new int[repetitions];
        for (int i = 0; i < repetitions; i++) {
            T in = ex.setup.apply(ex.input().apply(i));
            t.play();
            Integer result = ex.run.applyAsInt(in);
            t.pause();
            results[i] = result;
        }
        return new Measurement( t.check(), results );
    }

    //public static <T> Result repeatMeasure(int runs, int repetitions, Experiment<T> ex) {
    //    if (runs < 2) throw new 
    //        IllegalArgumentException("There must be more than one run"); 
    //    double tt = 0.0, sdt = 0.0;
    //    int[][] results = new int[runs][repetitions];
    //    for (int i = 0; i < runs; i++) {
    //        Tuple<Long, int[]> r = measure(repetitions, ex);
    //        double t = (double) r.a() / (double) repetitions ;
    //        tt += t; sdt += t*t;
    //        results[i] = r.b();
    //    }
    //double mean = tt/runs;
    //double sdev = Math.sqrt(( sdt - mean*mean*runs ) / (runs - 1));
    //return new Result(mean, sdev, repetitions, runs, results);
    //}

    public static <T> Result performExperiment(int runs, double timeLimit, Experiment<T> ex) {
        if (runs < 2) throw new 
            IllegalArgumentException("There must be more than one run"); 
        int count = 1, countLimit = Integer.MAX_VALUE / 2;
        Measurement m;
        long runningTime = 0l;
        List<Measurement> ms;
        double tt, sdt;
        do {
            count *= 2; tt = 0.0; sdt = 0.0;
            ms = new ArrayList<>(runs);
            for (int i = 0; i < runs; i++) {
                m = measure(count, ex);
                runningTime = m.time();
                double t = (double) runningTime / (double) count ;
                tt += t; sdt += t*t;
                ms.add(m);
            }
        } while (runningTime < timeLimit*1e9 && count < countLimit);
        double mean = tt/runs;
        double sdev = Math.sqrt(( sdt - mean * mean * runs ) / (runs - 1));
        return new Result(mean, sdev, count, runs, ms);
    }

    public static <T> Map<Integer, Result> parameterizedExperiment(
        int min, int max, double scale, int runs, double timeLimit, IntFunction<Experiment<T>> experimentGenerator
    ) {
        int n = min;
        int limit = (int) (Integer.MAX_VALUE / scale);
        Map<Integer, Result> results = new TreeMap<>();
        do {
            //System.out.print(new StringBuilder("n is " + n));
            results.put(n, performExperiment(
                runs, timeLimit, experimentGenerator.apply(n)));
            n *= scale;
        } while (n < limit && n < max);
        return results;


    }

    public static int printResult(String name, Result r) {
        System.out.printf(
            "%-30s%,20.1f%13.2f%,13d%,13d%n", 
            name, r.mean(), r.stdev(), r.runs(), r.repetitions() );
        int dummy = 0;

        for (Measurement m : r.results())
            for (int i : m.result())
                dummy += i;
        return dummy;


    }

    public static int printResult(String name, Result r, int parameter) {
        
        System.out.printf(
            "%-20s%10d%,20.1f%,13.2f%,13d%,13d%n", 
            name, parameter, r.mean(), r.stdev(), r.runs(), r.repetitions() );

        int dummy = 0;
        for (Measurement m : r.results())
            for (int i : m.result())
                dummy += i;
        return dummy;



// double mean, double stdev, int repetitions, int runs, List<Measurement> results


    }

    public static int multiply(int i) {
        double x = 1.1 * (double)(i & 0xFF);
        return (int) (x * x * x * x * x * x * x * x * x * x * x * x * x * x * x * x * x * x * x * x);
    }

    public static void main(String[] args) {

        systemInfo();
        System.out.println();
        System.out.printf( "%-20s%10s%20s%13s%13s%13s%n", 
            "# title", "param", "mean", "stdev", "runs", "reps"
        );

        printResult(
            "multiply",
            performExperiment(10, 0.25, new Experiment<Integer>(i -> i, Benchmark::multiply, i -> i))
        );
        

        // ===========

        IntFunction<Experiment<Integer[]>> gen = i -> {
            Integer[] data = Handler.generate(i, j -> j);
            return new Experiment<Integer[]>(j -> data, TopDownMergeSort::sort, Handler::randomize);
        };

        Map<Integer, Result> x = parameterizedExperiment(100, 100_000, 2, 5, 0.25, gen);
        x.forEach((k,v) -> printResult("randomIntSort", v, k));
            //name, parameter, r.mean(), r.stdev(), r.runs(), r.repetitions() );
    }
}
