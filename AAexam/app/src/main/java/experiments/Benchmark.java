package experiments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import com.sun.net.httpserver.Authenticator.Result;

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
    private static final String PRINTFORMAT = "%-20s %10s %7s %13s %20s %13s %13s %13s";

    public record Experiment<T>(IntFunction<T> input, ToIntFunction<T> run, UnaryOperator<T> setup) { }
    //public record Result(double meanTime, double sdevTime, double meanResult, double sdevResult, int repetitions) { }
    //public record Measurement(long time, double meanResult, double stdevResult) { }
    public record Observation(long time, int result) { }

    public static class Timer {
        private long start, spent = 0;
        public Timer()        { }
        public long check()   { return  (System.nanoTime()-start+spent); }
        public void pause()   { spent += System.nanoTime()-start; }
        public void play()    { start  = System.nanoTime(); }
        public void reset()   { spent  = 0; }
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

    public static <T> List<Observation> measure(int repetitions, Experiment<T> ex) {
        Timer t = new Timer();
        List<Observation> x = new ArrayList<>(repetitions);
        for (int i = 0; i < repetitions; i++) {
            T in = ex.setup.apply(ex.input().apply(i));
            //t.reset();
            t.play();
            int result = ex.run.applyAsInt(in);
            long time = t.check();
            x.add(new Observation(time, result));
        }
        return x;
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
    
    public static <T> List<Observation> dynamicMeasure(Experiment<T> ex, double timeLimit) {
        int count = 1, countLimit = Integer.MAX_VALUE / 2;
        long runningTime = 0l;
        List<Observation> obs;
        do {
            count *= 2;
            obs = measure(count, ex);
            runningTime = obs.stream()
                             .mapToLong(o -> o.time())
                             .sum();
        } while (runningTime < timeLimit*1e9 && count < countLimit);
        return obs;

    }

    public static <T> Map<Integer, List<Observation>> parameterizedMeasurement(
        int min, int max, double scale, double timeLimit, IntFunction<Experiment<T>> exGen
    ) {
        int limit = (int) (Integer.MAX_VALUE / scale);
        Map<Integer, List<Observation>> results = new TreeMap<>();
        for (int n = min; n < limit && n < max; n *= scale ) {
            Experiment<T> ex = exGen.apply(n);
            results.put(n, dynamicMeasure(ex, timeLimit));
        }
        return results;
    }

    // =============== Multiple measurements runs (why?) =================

    public static <T> int findRepetitions(Experiment<T> ex, double timeLimit) {
        int count = 1, countLimit = Integer.MAX_VALUE / 2;
        long runningTime = 0l;
        do {
            count *= 2;
            runningTime = measure(count, ex).stream()
                                            .mapToLong(o -> o.time())
                                            .sum();
        } while (runningTime < timeLimit*1e9 && count < countLimit);
        return count;
    }

    public static <T> List<List<Observation>> multipleRunMeasure(Experiment<T> ex, int runs, int repetitions) {
        //return Stream.generate(() -> measure(repetitions, ex)).limit(runs).toList();
        List<List<Observation>> x = new ArrayList<>();
        for (int i = 0; i < runs; i++) {
            x.add(measure(repetitions, ex));
        }
        return x;
    }

    public static <T> List<List<Observation>> dynamicMultipleRunMeasure(Experiment<T> ex, int runs, double timeLimit) {
        return multipleRunMeasure(ex, runs, findRepetitions(ex, timeLimit));
    }

    public static <T> Map<Integer, List<List<Observation>>> parameterizedMultipleRunMeasure(
        int min, int max, double scale,
        int runs, double timeLimit, 
        IntFunction<Experiment<T>> experimentGenerator
    ) {
        int limit = (int) (Integer.MAX_VALUE / scale);
        Map<Integer, List<List<Observation>>> results = new TreeMap<>();
        for (int n = min; n < limit && n < max; n *= scale ) {
            Experiment<T> ex = experimentGenerator.apply(n);
            results.put(n, 
                multipleRunMeasure(ex, runs, findRepetitions(ex, timeLimit)));
        }
        return results;
    }

    // ======================== Analysis ==========================
    
    public static Map<Result, Double> analyze(List<Observation> obs) {
        int n = obs.size();
        long accTime = 0, accResult = 0, accSDTime = 0, accSDResult = 0;
        for (Observation o : obs ) {
            long t = o.time();   accTime   += t; accSDTime   += t*t;
            int  r = o.result(); accResult += r; accSDResult += r*r;
        }
        return resultFromAccumulation(accTime, accResult, accSDTime, accSDResult, n);
    }

    public static List<Map<Result, Double>> analyzeParameterized(Map<Integer, List<Observation>> obs) {
        List<Map<Result, Double>> x = new ArrayList<>();
        obs.keySet().stream().sorted().forEach(i -> {
            Map<Result, Double> m = analyze(obs.get(i));
            m.put(Result.PARAMETER, (double) i);
            x.add(m);
        });
        return x;
    }

    public static Map<Result, Double> analyzeMultipleRuns(List<List<Observation>> obs) {
        long accTime = 0, accResult = 0, accSDTime = 0, accSDResult = 0;
        int n = obs.size(), nn = -1;
        for (List<Observation> ob : obs) {
            long accT = 0, accR = 0;
            nn = ob.size();
            for (Observation o : ob ) {
                accT += o.time(); accR += o.result();
            }
            long t = accT / nn; accTime   += t; accSDTime   += t*t;
            long r = accR / nn; accResult += r; accSDResult += r*r;
        }
        Map<Result, Double> x = 
            resultFromAccumulation(accTime, accResult, accSDTime, accSDResult, n);
        x.put(Result.REPETITIONS, (double) nn);
        x.put(Result.RUNS, (double) n);
        return x;
    }

    public static List<Map<Result, Double>> analyzeMultipleRunsParameterized(
        Map<Integer, List<List<Observation>>> obs
    ) {
        List<Map<Result, Double>> x = new ArrayList<>();
        obs.keySet().stream().sorted().forEach(i -> {
            Map<Result, Double> m = analyzeMultipleRuns(obs.get(i));
            m.put(Result.PARAMETER, (double) i);
            x.add(m);
        });
        return x;

    }

    public enum Result {
        MEANTIME, SDEVTIME, MEANRESULT, SDEVRESULT, PARAMETER, REPETITIONS, RUNS;
    }

    public static Map<Result, Double> resultFromAccumulation(
        long accTime, long accResult, long accSDTime, long accSDResult, int n
    ) {
        Map<Result, Double> r = new HashMap<>();
        double meanTime   = (double) accTime   / (double) n;
        double meanResult = (double) accResult / (double) n;
        double sdevTime   = sdev(accSDTime, meanTime, n);
        double sdevResult = sdev(accSDResult, meanResult, n);
        r.put(Result.MEANTIME, meanTime);
        r.put(Result.SDEVTIME, sdevTime);
        r.put(Result.MEANRESULT, meanResult);
        r.put(Result.SDEVRESULT, sdevResult);
        r.put(Result.REPETITIONS, (double) n);
        return r;

    }

    public static double sdev(long acc, double mean, int n) {
        return (n > 1)
            ? Math.sqrt((acc - mean * mean * n) / (n - 1))
            : 0.0;
    }


    // ======================== Printing ==========================

    // ========================   Main   ==========================

    //public static <T> List<List<Observation>> dynamicMeasure(int runs, double timeLimit, Experiment<T> ex) {
    //    if (runs < 2) throw new 
    //        IllegalArgumentException("There must be more than one run"); 
    //    int count = 1, countLimit = Integer.MAX_VALUE / 2;
    //    List<Observation> m;
    //    long runningTime = 0l;
    //    List<List<Observation>> ms;
    //    do {
    //        count *= 2; tt = 0.0; sdt = 0.0;
    //        ms = new ArrayList<>(runs);
    //        for (int i = 0; i < runs; i++) {
    //            m = measure(count, ex);
    //            runningTime = m.stream().mapToLong(o -> o.time()).sum();
    //            ms.add(m);
    //        }
    //    } while (runningTime < timeLimit*1e9 && count < countLimit);
    //    return ms;
    //}

    public static String resultHeaders() {
        return String.format(
            PRINTFORMAT,
            "# title", "param", "runs", "reps", "meanTime", "sdevTime", "meanResult", "sdevResult"
        );
    }

    public static String resultToString(String name, Map<Result, Double> r) {
        return String.format(
            PRINTFORMAT, 
            name,
            r.containsKey(Result.PARAMETER)   ? String.format("%,9.0f",  r.get(Result.PARAMETER))   : "",
            r.containsKey(Result.RUNS)        ? String.format("%,4.0f",  r.get(Result.RUNS))        : "",
            r.containsKey(Result.REPETITIONS) ? String.format("%,9.0f",  r.get(Result.REPETITIONS)) : "",
            r.containsKey(Result.MEANTIME)    ? String.format("%,10.0f", r.get(Result.MEANTIME))    : "",
            r.containsKey(Result.SDEVTIME)    ? String.format("%,6.1f",  r.get(Result.SDEVTIME))    : "",
            r.containsKey(Result.MEANRESULT)  ? String.format("%,10.0f", r.get(Result.MEANRESULT))  : "",
            r.containsKey(Result.SDEVRESULT)  ? String.format("%,6.1f",  r.get(Result.SDEVRESULT))  : ""
        );
    }

    public static String resultToString(String name, Map<Result, Double> r, Collection<Result> ignore) {
        for (Result result : ignore) { r.remove(result); }
        return resultToString(name, r);

    }

    public static int multiply(int i) {
        double x = 1.1 * (double)(i & 0xFF);
        return (int) (x * x * x * x * x * x * x * x * x * x * x * x * x * x * x * x * x * x * x * x);
    }

    public static void main(String[] args) {

        systemInfo();
        System.out.println();
        System.out.printf( "%-20s%10s%7%13d%20s%13s%13s%13s%n", 
            "# title", "param", "reps", "meanTime", "sdevTime", "meanResult", "sdevResult"
        );

        Map<Result, Double> result;
        List<Map<Result, Double>> results;
        List<Observation> obs;
        List<List<Observation>> multObs;
        Map<Integer, List<Observation>> paramObs;
        Map<Integer, List<List<Observation>>> paramMultObs;
        Experiment<Integer> integerExperiment;
        Experiment<Integer[]> sortExperiment;

        integerExperiment =  new Experiment<Integer>(i -> i, Benchmark::multiply, i -> i);
        multObs = dynamicMultipleRunMeasure( integerExperiment, 10, 0.25);
        result = analyzeMultipleRuns(multObs);
        System.out.print(resultToString("multiply", result));
        

        // ===========

        IntFunction<Experiment<Integer[]>> gen = i -> {
            Integer[] data = Handler.generate(i, j -> j);
            return new Experiment<Integer[]>(j -> data, TopDownMergeSort::sort, Handler::randomize);
        };

        //Map<Result, Double> x = analyzeMultipleRuns();

        paramMultObs = parameterizedMultipleRunMeasure(100, 100_000, 2, 5, 0.25, gen);
        results      = analyzeMultipleRunsParameterized(paramMultObs);
        for (Map<Result, Double> r : results) {
            System.out.printf(resultToString("sort", r));
        }


        //x.forEach((k,v) -> printResult("randomIntSort", v, k));
            //name, parameter, r.mean(), r.stdev(), r.runs(), r.repetitions() );
    }
}
