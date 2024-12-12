package experiments;

import static experiments.Result.Key;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.IntFunction;

/** <p>A measurement is a collection of observations of time and experimental results.
 * There are four types of measurements:</p>
 * <ol>
 * <li>{@code SingleRunMeasurement}</li>
 * <li>{@code ParameterizedRunMeasurement}</li>
 * <li>{@code MultiRunMeasurement}</li>
 * <li>{@code ParameterizedMultiRunMeasurement}</li>
 * </ol>
 * Measurements are created through the {@code Experiments} factory method {@code measure}.
 * The only public method os an {@code Measurement} is {@code analyze()}, which produces a result.
 *
 * Measurement also contains the {@code Timer} class, which is used for basic timekeeping.
 */
public abstract sealed class Measurement {

    public static class Timer {
        private long start, spent = 0;
        public Timer()        { }
        public long check()   { return  (System.nanoTime()-start+spent); }
        public void pause()   { spent += System.nanoTime()-start; }
        public void play()    { start  = System.nanoTime(); }
    }

    abstract public Result analyze(String withTitle);
}

final class SingleRunMeasurement extends Measurement {
    private int[] results;
    private long[] times;
    private final long runningTime;
    private final double averageTime,averageResult;
    private final int observations;

    <T> SingleRunMeasurement(Experiment<T> ex, int repetitions) {
        Timer t              = new Timer();
        long runningTime     = 0; 
        double averageResult = 0;
        results              = new int[repetitions];
        times                = new long[repetitions];
        for (int i = 0; i < repetitions; i++) {
            T in           = ex.setup(ex.input(i));
            t.play();
            int result     = ex.run(in);
            long time      = t.check();
            results[i]     = result;
            times[i]       = time;
            runningTime   += time;
            averageResult += result / (double) repetitions;
        }
        averageTime = runningTime / (double) repetitions;
        this.runningTime = runningTime;
        this.averageResult = averageResult;
        this.observations = repetitions;
    }

    public <T> SingleRunMeasurement(Experiment<T> ex, double timeLimit) {
        int count = 1, countLimit = Integer.MAX_VALUE / 2;
        SingleRunMeasurement obs;
        do { count *= 2;
             obs = new SingleRunMeasurement(ex, count);
        } while (obs.runningTime < timeLimit*1e9 && count < countLimit);
        this.averageResult = obs.averageResult;
        this.averageTime   = obs.averageTime;
        this.runningTime   = obs.runningTime;
        this.times         = obs.times;
        this.results       = obs.results;
        this.observations  = obs.observations;
    }

    public long runningTime()     { return runningTime; }
    public double averageResult() { return averageResult; }
    public double averageTime()   { return averageTime; }
    public int observations()     { return observations; }

    public SingleResult analyze(String withTitle) {

        // Try to implement Welford's algorithm
        // https://en.wikipedia.org/wiki/Algorithms_for_calculating_variance
        int count = 0;
        double meanResult = 0.0, m2Result = 0.0, meanTime = 0.0, m2Time = 0.0, sdResult, sdTime;
        for (int i = 0; i < observations; i++) {
            count++;
            int result      = results[i];          long time     = times[i];
            double d1Result = result - meanResult; double d1Time = time - meanTime;
            meanResult     += d1Result / count;    meanTime     += d1Time / count;
            double d2Result = result - meanResult; double d2Time = time - meanTime;
            m2Result       += d1Result * d2Result; m2Time       += d1Time * d2Time;
        }
        sdResult  = Math.sqrt(m2Result/(count-1));     sdTime = Math.sqrt(m2Time/(count-1));

        SingleResult r = new SingleResult(withTitle);
        r.put(Key.MEANRESULT, meanResult);             r.put(Key.MEANTIME, meanTime);
        r.put(Key.SDEVRESULT, sdResult);               r.put(Key.SDEVTIME, sdTime);
        r.put(Key.REPETITIONS, (double) count);
        return r;
    }
}

final class MultiRunMeasurement extends Measurement {
    List<SingleRunMeasurement> obs;
    int runs;
    int repetitions;

    <T> MultiRunMeasurement(Experiment<T> ex, int runs, int repetitions) {
        this.runs = runs;
        this.repetitions = repetitions;
        List<SingleRunMeasurement> x = new ArrayList<>();
        for (int i = 0; i < runs; i++) {
            x.add(new SingleRunMeasurement(ex, repetitions));
        } obs = x;
    }

    <T> MultiRunMeasurement(Experiment<T> ex, int runs, double timeLimit) {
        this(ex, runs, new SingleRunMeasurement(ex, timeLimit).observations()); }

    public SingleResult analyze(String withTitle) {
        int count = 0;
        double meanResult = 0.0, m2Result = 0.0, meanTime = 0.0, m2Time = 0.0, sdResult, sdTime;
        for (SingleRunMeasurement o : obs ) {
            double result   = o.averageResult();       double time   = o.averageTime();
            count++;
            double d1Result = result   - meanResult;   double d1Time = time   - meanTime;
            meanResult     += d1Result / count;        meanTime     += d1Time / count;
            double d2Result = result   - meanResult;   double d2Time = time   - meanTime;
            m2Result       += d1Result * d2Result;     m2Time       += d1Time * d2Time;
        }
        sdResult = Math.sqrt(m2Result/(count-1));      sdTime = Math.sqrt(m2Time/(count-1));

        SingleResult r = new SingleResult(withTitle);
        r.put(Key.MEANRESULT, meanResult);             r.put(Key.MEANTIME, meanTime);
        r.put(Key.SDEVRESULT, sdResult);               r.put(Key.SDEVTIME, sdTime);
        r.put(Key.REPETITIONS, (double) count);
        return r;
    }
}

final class ParameterizedSingleRunMeasurement extends Measurement {
    Map<Integer, SingleRunMeasurement> obs;

    <T> ParameterizedSingleRunMeasurement(
        IntFunction<Experiment<T>> exGen, double timeLimit,
        int pMin, int pMax, double pScale
    ) {
        int limit = (int) (Integer.MAX_VALUE / pScale);
        Map<Integer, SingleRunMeasurement> results = new TreeMap<>();
        for (int p = pMin; p < limit && p < pMax; p *= pScale ) {
            Experiment<T> ex = exGen.apply(p);
            results.put(p, new SingleRunMeasurement(ex, timeLimit));
        }
        obs = results;
    }

    public ParameterizedResult analyze(String withTitle) {
        List<SingleResult> x = new ArrayList<>();
        obs.keySet().stream().sorted().forEach(i -> {
            SingleResult m = obs.get(i).analyze(withTitle);
            m.put(Key.PARAMETER, (double) i);
            x.add(m);
        });
        return new ParameterizedResult(x);
    }
}

final class ParameterizedMultiRunMeasurement extends Measurement {
    Map<Integer, MultiRunMeasurement> obs;

    <T> ParameterizedMultiRunMeasurement(
        IntFunction<Experiment<T>> exGen, double timeLimit, int runs,
        int pMin, int pMax, double pScale
    ) {
        int limit = (int) (Integer.MAX_VALUE / pScale);
        Map<Integer, MultiRunMeasurement> results = new TreeMap<>();
        for (int p = pMin; p < limit && p < pMax; p *= pScale ) {
            Experiment<T> ex = exGen.apply(p);
            results.put(p, 
                new MultiRunMeasurement(
                    ex, runs, new SingleRunMeasurement(ex, timeLimit).observations())
            );
        }
        obs = results;
    }
    public ParameterizedResult analyze(String withTitle) {
        List<SingleResult> x = new ArrayList<>();
        obs.keySet().stream().sorted().forEach(i -> {
            SingleResult m = obs.get(i).analyze(withTitle);
            m.put(Key.PARAMETER, (double) i);
            x.add(m);
        });
        return new ParameterizedResult(x);
    }
}
