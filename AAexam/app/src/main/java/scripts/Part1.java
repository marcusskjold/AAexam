package scripts;

import experiments.Experiments;
import experiments.Experiment;
import experiments.Result;
import experiments.Result.Key;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;

import data.Handler;
import data.TestData;
import sorting.SortUtils;
import sorting.TopDownMergeSort;

public class Part1 {

    static final double LONGTIME = 0.5;
    static final double MEDIUMTIME = 0.2;
    static final double SHORTTIME = 0.1;
    static int n = 100_000;

    public static void print(String s) { System.out.println(s); }
    public static void print()         { System.out.println(); }
    public static void main(String[] args) {

        // ========== Setup

        System.out.println("System info: ");
        Experiments.systemInfo();
        System.out.println();


        task1();
        task2();
    }

    public static void task1() {
        print("=====================================================================");
        print("Task 1: Implement classical MergeSort recursively.");
        print("=====================================================================");
        print();

        // The Handler.generate method is a generic test data generator.
        // It returns an object array.
        // The size of the array is given as the first parameter.
        // The function to generate the i'th element of the array is
        // given as the second parameter.
        Integer[] expected = Handler.generate(100_000, i -> i);

        // Basic correctness checks.
        // SortUtils can check if an array is sorted.
        if (!SortUtils.isSorted(expected)) throw new AssertionError(
            "Precondition failed: Expected data is not sorted!");
        Integer[] actual   = Handler.randomize(expected);
        if (SortUtils.isSorted(actual)) throw new AssertionError(
            "Precondition failed: Input data is already sorted!");

        // TopDownMergeSort is our classic recursive mergesort implementation.
        int comparisons = TopDownMergeSort.sort(actual);

        if (!SortUtils.isSorted(actual)) throw new AssertionError(
            "Postcondition failed: Returned data is not sorted!");

        print("A classic recursive top-down mergesort sorted an integer array of size 100000, using "
                + comparisons + " comparisons");
        print();
    }

    public static void task2_1() {
        print("Experiment 1: Varying the number of comparisons by varying input size");
        print("              Perform a single run, dynamically find repetitions");
        print("Result is the number of comparisons. Param is the size of the input. Time is in nanoseconds.");
        print();


        // First experiment, see if number of comparisons is proportional to the running time by
        // varying the input size, assuming the input size is proportional to the number of comparisons.
        // This is a parameterized experiment.
        IntFunction<Experiment<Integer[]>> ex = parameterValue ->
            new Experiment<Integer[]>(
                Handler.generate(parameterValue, i -> i),
                TopDownMergeSort::sort,
                Handler::randomize
        );

        // Run, analyze and print the results of the experiment.
        print(Result.resultHeaders());
        Experiments.measure(ex, SHORTTIME, 100, 1_000_000, 2.0)
                   .analyze("t2e1")
                   .saveAsCSV()
                   .print();

        print();
        print("Results have been saved as CSV in the data/results folder.");
        print();
    }

    public static void task2_2() {

        // Setup

        List<UnaryOperator<Integer[]>> setupInts = new ArrayList<>();
        setupInts.add(i -> i);
        setupInts.add(Handler::invert);
        setupInts.add(i -> Handler.randomize(i, 1));
        setupInts.add(i -> Handler.randomize(i, 10));
        setupInts.add(i -> Handler.randomize(i, 50));
        setupInts.add(i -> Handler.randomize(i, 80));
        setupInts.add(Handler::randomize);
        setupInts.add(Handler::mergeSortWorstCase);

        List<UnaryOperator<TestData[]>> setupTDs = new ArrayList<>();
        setupTDs.add(i -> i);
        setupTDs.add(Handler::invert);
        setupTDs.add(i -> Handler.randomize(i, 1));
        setupTDs.add(i -> Handler.randomize(i, 10));
        setupTDs.add(i -> Handler.randomize(i, 50));
        setupTDs.add(i -> Handler.randomize(i, 80));
        setupTDs.add(Handler::randomize);
        setupTDs.add(Handler::mergeSortWorstCase);

        int methods = setupTDs.size();

        // Go

        print("Experiment 2: Varying the number of comparisons by varying input state");
        print("              Array size is kept constant at " + n + " elements");
        print("              Parameter corresponds to different kinds of array order. Parameter key:");
        print("                  0. Ordered");
        print("                  1. Inverted");
        print("                  2. 1%  of elements shuffled");
        print("                  3. 10% of elements shuffled");
        print("                  4. 50%  of elements shuffled");
        print("                  5. 80%  of elements shuffled");
        print("                  6. all elements shuffled");
        print("                  7. worst case: see Handler class for implementation");
        print();


        print("Experiment 2.1: Baseline experiment. Demonstrates that the techniques ");
        print("                perform equally well on all input (of type Integer[]).");

        print();
        print(Result.resultHeaders());

        IntFunction<Experiment<Integer[]>> ex = parameterValue -> 
            new Experiment<Integer[]>(
                Handler.generate(n, i -> 1),
                TopDownMergeSort::sort,
                setupInts.get(parameterValue)
        );

        Experiments.measure(ex, MEDIUMTIME, methods)
                   .analyze("t2e2_1")
                   .saveAsCSV()
                   .print();

        print();
        print("Experiment 2.2: Baseline experiment. Demonstrates that the techniques ");
        print("                perform equally well on all input (of type TestData[]).");
        print();
        print(Result.resultHeaders());
        IntFunction<Experiment<TestData[]>> ex2 = parameterValue -> 
            new Experiment<TestData[]>(
                Handler.generate(n, i -> new TestData(1, 1)),
                TopDownMergeSort::sort,
                setupTDs.get(parameterValue)
        );

        Experiments.measure(ex2, MEDIUMTIME, methods)
                   .analyze("t2e2_2")
                   .saveAsCSV()
                   .print();

        print("Experiment 2.3: Baseline experiment. Demonstrates that the techniques");
        print("                perform equally well on all input (of type TestData[] with random ID).");
        print();
        print(Result.resultHeaders());
        ex2 = parameterValue -> 
            new Experiment<TestData[]>(
                Handler.generate(n, i -> new TestData(i, 1)),
                TopDownMergeSort::sort,
                setupTDs.get(parameterValue)
        );

        Experiments.measure(ex2, MEDIUMTIME, methods)
                   .analyze("t2e2_3")
                   .saveAsCSV()
                   .print();

        print();
        print("Experiment 2.4: Demonstrates that the experiments work as intended – more comparisons");
        print("                translate to longer running time.");
        print("                Also, best case and worst case use the same number of comparisons each time (sdev = 0)");
        print("                Note two interesting things:");
        print("                1) Worst case input produces a higher number of comparisons but a running time");
        print("                comparable to the sorted input.");
        print("                2) Inverted arrays produce a lower number of comparisons.");
        print();
        print(Result.resultHeaders());
        ex = parameterValue -> 
            new Experiment<Integer[]>(
                Handler.generate(n, i -> i),
                TopDownMergeSort::sort,
                setupInts.get(parameterValue)
        );

        Experiments.measure(ex, MEDIUMTIME, methods)
                   .analyze("t2e2_4")
                   .saveAsCSV()
                   .print();
        print();
        print("The following experiments investigate (1) further");
        print();
        print("Experiment 2.5: Demonstrate the the speedup does not come from the fact that it is the same");
        print("                input array each repetitions.");
        print();
        print(Result.resultHeaders());
        ex = parameterValue -> 
            new Experiment<Integer[]>(
                r -> Handler.generate(n, i -> i + (r * 1000)),
                TopDownMergeSort::sort,
                setupInts.get(parameterValue)
        );

        Experiments.measure(ex, MEDIUMTIME, methods)
                   .analyze("t2e2_5")
                   .saveAsCSV()
                   .print();
        print();
        print("Experiment 2.6: Demonstrate that the speedup does not come from multiple repetitions.");
        print();
        print(Result.resultHeaders());
        Experiment<Integer[]> ex3 = new Experiment<>(
            Handler.generate(n, i -> i),
            TopDownMergeSort::sort,
            setupInts.get(7));

        Experiments.measure(ex3, 1)
                   .analyze("t2e2_6")
                   .saveAsCSV()
                   .print();
        print();


    }

    public static void task2_3() {

        print("The following experiments will investigate how sorting reacts to string input");

        IntFunction<Experiment<String[]>> exString = parameterValue -> new Experiment<>(
            Handler.generate(n, i -> Handler.randomString(parameterValue)),
            TopDownMergeSort::sort
        );

        print();
        print("Experiment 3: Random string input of size " + n);
        print("              The parameter is the length of the string.");
        print();
        print(Result.resultHeaders());

        Experiments.measure(exString, LONGTIME, 20)
                   .analyze("t2e3")
                   .saveAsCSV()
                   .print();
        print();
        print("I do not yet understand why the result (number of compares) is identical to sorted input");
        print("in all cases. It might be issues with the random generator?");
    }

    public static void task2() {
        print("=====================================================================");
        print("Task 2: Design and perform an experiment that investigates if the");
        print("        running time of an implementation is proportional to the");
        print("        number of comparisons.");
        print("=====================================================================");
        print();

        task2_1();
        task2_2();
        task2_3();

        
    }

}
