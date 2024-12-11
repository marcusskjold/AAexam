import static experiments.Benchmark.*;
import experiments.Benchmark;
//import benchmarking.*;

import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

import data.Handler;
import sorting.TopDownMergeSort;

public class Main {
    public static void main(String[] args) {
        systemInfo();
        System.out.println();
        //System.out.printf("%-20s %10s %6s %12s %12s %12s %12s %12s%n", 
        //    "# title", "param", "runs", "reps", "meanTime", "sdevTime", "meanResult", "sdevResult"
        //);

        System.out.println(resultHeaders());
        Map<Result, Double> result;
        List<Map<Result, Double>> results;

        List<Observation> obs;
        List<List<Observation>> multObs;
        Map<Integer, List<Observation>> paramObs;
        Map<Integer, List<List<Observation>>> paramMultObs;

        Experiment<Integer> intExperiment;
        Experiment<Integer[]> sortExperiment;

        experiments.Benchmark.Timer t = new Timer();

        t.play();
        // ========== Experiment 1: Simple integer multiplication [multiple runs]

        intExperiment    = new Experiment<Integer>(i -> i, Benchmark::multiply, i -> i);
        multObs          = dynamicMultipleRunMeasure(intExperiment, 10, 0.01); // This breaks with a higher timelimit
        result           = analyzeMultipleRuns(multObs);
        System.out.println(resultToString("multiply", result, List.of(Result.MEANRESULT, Result.SDEVRESULT)));
        
        System.out.println(t.check() / 1e9);

        // =========== Experiment 2: Mergesort [parameterized]
        t.play();

        IntFunction<Experiment<Integer[]>> gen = i -> {
            Integer[] data = Handler.generate(i, j -> j);
            return new Experiment<Integer[]>(
                j -> data, TopDownMergeSort::sort, Handler::randomize
            );
        };

        /* 
        paramObs = parameterizedMeasurement(100, 100_000, 2, 0.25, gen);
        results  = analyzeParameterized(paramObs);
        for (Map<Result, Double> r : results) {
        System.out.println(resultToString("singlerunsort", r));
        }

        System.out.println(t.check() / 1e9);
        */

        // =========== Experiment 3A: Mergesort with different input

        t.play();

        IntFunction<Integer[]> data = i -> Handler.generate(50_000, j -> j);

        sortExperiment = new Experiment<>(
            data, TopDownMergeSort::sort, j -> j);

        obs = dynamicMeasure(sortExperiment, 0.25);
        result = analyze(obs);
        result.put(Result.PARAMETER, (double) 50_000);
        System.out.println(resultToString("sorted", result));

        System.out.println(t.check() / 1e9);

        //t.play();

        // =========== Experiment 3B: Mergesort with different input

        sortExperiment = new Experiment<>(
            data, TopDownMergeSort::sort, j -> Handler.randomize(j, 20));

        obs = dynamicMeasure(sortExperiment, 0.25);
        result = analyze(obs);
        result.put(Result.PARAMETER, (double) 50_000);
        System.out.println(resultToString("20pRandom", result));

        //System.out.println(t.check() / 1e9);

        // =========== Experiment 3C: Mergesort with different input

        sortExperiment = new Experiment<>(
            data, TopDownMergeSort::sort, Handler::invert);

        Integer[] in = sortExperiment.setup().apply(data.apply(1));
        boolean b = false;

        int j = 0;
        for (int i = in.length; i > 0; i--) {
            b = (in[j] == i-1) ? true : false; 
            j++;
        }

        System.out.println(b);
        obs = dynamicMeasure(sortExperiment, 0.25);
        result = analyze(obs);
        result.put(Result.PARAMETER, (double) 50_000);
        System.out.println(resultToString("inverted", result));

        //System.out.println(t.check() / 1e9);

        // =========== Experiment 4: Complex mergesort [parameterized, multiple runs]
        
        //t.play();

        paramMultObs = parameterizedMultipleRunMeasure(100, 100_000, 2, 10, 0.25, gen);
        results      = analyzeMultipleRunsParameterized(paramMultObs);
        for (Map<Result, Double> r : results) {
            System.out.println(resultToString("sort", r));
        }
       
        //System.out.println(t.check() / 1e9);

        // ============ Experiment 4: Comparison [parameterized, multiple runs]

        t.reset();

        for (int size = 100; size <= 100_000; size *= 2) { 
            final Integer[] intArray = Handler.generate(size, i -> i);
            benchmarking.Benchmark.Mark8Setup(
                "rIntSort", 
                String.format("%8d", size),
                new benchmarking.Benchmarkable() {
                    public void setup() { Handler.inPlaceRandomize(intArray);}
                    public double applyAsDouble(int i) {
                        return (double) TopDownMergeSort.sort(intArray);
                    }
                });
        }

        System.out.println(t.check() / 1e9);
    }
}
