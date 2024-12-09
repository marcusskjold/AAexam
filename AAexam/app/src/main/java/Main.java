import static experiments.Benchmark.*;
import benchmarking.*;

import java.util.Map;
import java.util.function.IntFunction;

import data.Handler;
import sorting.TopDownMergeSort;

public class Main {
    public static void main(String[] args) {
        double timeLimit = 0.25;
        systemInfo();
        System.out.println();
        System.out.printf( "%-20s%10s%20s%13s%13s%13s%n", 
            "# title", "param", "mean", "stdev", "runs", "reps"
        );

        printResult(
            "multiply",
            performExperiment(10, 0.25, new Experiment<Integer>(i -> i, experiments.Benchmark::multiply, i -> i))
        );
        

        // ===========

        IntFunction<Experiment<Integer[]>> gen = i -> {
            Integer[] data = Handler.generate(i, j -> j);
            return new Experiment<Integer[]>(j -> data, TopDownMergeSort::sort, Handler::randomize);
        };

        Map<Integer, Result> x = parameterizedExperiment(100, 100_000, 2, 5, timeLimit, gen);

        x.forEach((k,v) -> printResult("randomIntSort", v, k));


        // ================= 


        for (int size = 100; size <= 2_000_000; size *= 2) { 
            final Integer[] intArray = Handler.generate(size, i -> i);
            Benchmark.Mark8Setup(
                "rIntSort", 
                String.format("%8d", size),
                new Benchmarkable() {
                    public void setup() { Handler.inPlaceRandomize(intArray);}
                    public double applyAsDouble(int i) {
                        return (double) TopDownMergeSort.sort(intArray);
                    }
                });
        }
    }
}
