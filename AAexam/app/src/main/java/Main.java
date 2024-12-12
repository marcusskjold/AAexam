import experiments.Experiments;
import experiments.Experiment;
import experiments.Result;
import experiments.Result.Key;

import java.util.List;
import java.util.function.IntFunction;

import data.Handler;
import sorting.TopDownMergeSort;

public class Main {
    public static void main(String[] args) {

        // ========== Setup

        Experiment<Integer> iEx;
        Experiment<Integer[]> ex;
        IntFunction<Experiment<Integer[]>> gen;
        Integer[] data;

        Experiments.systemInfo();
        System.out.println();

        System.out.println(Result.resultHeaders());

        // ========== Experiment 1: Simple integer multiplication [multiple runs]

        iEx = new Experiment<Integer>(i -> i, Experiments::multiply, i -> i);
        Experiments.measure(iEx, 0.1).analyze("multiply")
                   .removeKeys(List.of(Key.MEANRESULT, Key.SDEVRESULT))
                   .print();


        // =========== Experiment 2: Mergesort [parameterized]

        gen = i -> new Experiment<Integer[]>(
            Handler.generate(i, j -> j),
            TopDownMergeSort::sort, 
            Handler::randomize
        );

        Experiments.measure(gen, 0.25, 100, 100_000, 2.0)
                   .analyze("singlerunsort")
                   .print();


        // =========== Experiment 3A: Mergesort with different input

        data = Handler.generate(50_000, j -> j);

        ex = new Experiment<>(
            data, TopDownMergeSort::sort, j -> j);

        Experiments.measure(ex, 0.25).analyze("sorted")
                   .put(Key.PARAMETER, (double) 50_000)
                   .print();


        // =========== Experiment 3B: Mergesort with different input

        ex = new Experiment<>(
            data, TopDownMergeSort::sort, j -> Handler.randomize(j, 20));

        Experiments.measure(ex, 0.25)
                   .analyze("20pRandom")
                   .put(Key.PARAMETER, (double) 50_000)
                   .print();


        // =========== Experiment 3C: Mergesort with different input

        ex = new Experiment<>(
            data, TopDownMergeSort::sort, Handler::invert);

        Experiments.measure(ex, 0.25).analyze("inverted")
                   .put(Key.PARAMETER, (double) 50_000)
                   .print();


        // =========== Experiment 4: Complex mergesort [parameterized, multiple runs]

        Experiments.measure(gen, 0.25, 10, 100, 100_000, 2.0)
                   .analyze("sort")
                   .print();


        // ============ Experiment 5: Comparison with benchmarking library [parameterized, multiple runs]

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
    }
}
