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
import sorting.BottomUpMergeSort;
import sorting.BottomUpMergeSortCutoff;
import sorting.TopDownMergeSort;
import sorting.TopDownMergeSortCutoff;
import sorting.Util;

public class Part1 {

    static final double LONGTIME = 1.0;
    static final double MEDIUMTIME = 0.5;
    static final double SHORTTIME = 0.2;
    static int n = 100_000;

    public static void print(String s) { System.out.println(s); }
    public static void print()         { System.out.println(); }
    public static void main(String[] args) {

        // ========== Setup

        System.out.println("System info: ");
        Experiments.systemInfo();
        System.out.println();

        // Todo: maybe add a global switch to set the scope of our experiments (e.g. by doing multiple runs, with a longer max time)
        //       This is so we can run the experiments quickly and well enough, but at the end we can just let the computer cook
        //       and hopefully get better results.
        task1();
        task2();
        task2_1();
        task2_2();
        task2_3();
        task2_4();
        task3();
        task4();
        task5();
        task6();
        task7();
    }

    // ==================================================================
    // Task 1
    // ==================================================================

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
        // Util can check if an array is sorted.
        if (!Util.isSorted(expected)) throw new AssertionError(
            "Precondition failed: Expected data is not sorted!");
        Integer[] actual   = Handler.randomize(expected);
        if (Util.isSorted(actual)) throw new AssertionError(
            "Precondition failed: Input data is already sorted!");

        // TopDownMergeSort is our classic recursive mergesort implementation.
        int comparisons = TopDownMergeSort.sort(actual);

        if (!Util.isSorted(actual)) throw new AssertionError(
            "Postcondition failed: Returned data is not sorted!");

        print("A classic recursive top-down mergesort sorted an integer array of size 100000, using "
                + comparisons + " comparisons");
        print();
    }

    // ==================================================================
    // Task 2
    // ==================================================================

    public static void task2() {
        print("=====================================================================");
        print("Task 2: Design and perform an experiment that investigates if the");
        print("        running time of an implementation is proportional to the");
        print("        number of comparisons.");
        print("=====================================================================");
        print();
    }

    public static void task2_1() {
        print("------------------------------------------------");
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
        setupInts.add(null);
        setupInts.add(i -> i);
        setupInts.add(Handler::invert);
        setupInts.add(i -> Handler.randomize(i, 1));
        setupInts.add(i -> Handler.randomize(i, 10));
        setupInts.add(i -> Handler.randomize(i, 50));
        setupInts.add(i -> Handler.randomize(i, 80));
        setupInts.add(Handler::randomize);
        setupInts.add(Handler::mergeSortWorstCase);

        List<UnaryOperator<TestData[]>> setupTDs = new ArrayList<>();
        setupTDs.add(null);
        setupTDs.add(i -> i);
        setupTDs.add(Handler::invert);
        setupTDs.add(i -> Handler.randomize(i, 1));
        setupTDs.add(i -> Handler.randomize(i, 10));
        setupTDs.add(i -> Handler.randomize(i, 50));
        setupTDs.add(i -> Handler.randomize(i, 80));
        setupTDs.add(Handler::randomize);
        setupTDs.add(Handler::mergeSortWorstCase);

        int methods = setupTDs.size() - 1;

        // Go


        print("------------------------------------------------");
        print("Experiment 2: Varying the number of comparisons by varying input order");
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

        print("------------------------------------------------");
        print("The following experiments will investigate how sorting reacts to string input");

        IntFunction<Experiment<String[]>> exString = parameterValue -> new Experiment<>(
            Handler.generate(n, i -> Handler.randomString(parameterValue)),
            TopDownMergeSort::sort,
            Handler::randomize
        );

        print();
        print("Experiment 3.1: Random string input of size " + n);
        print("                The parameter is the length of the string.");
        print();
        print(Result.resultHeaders());

        Experiments.measure(exString, MEDIUMTIME, 20)
                   .analyze("t2e3_1")
                   .saveAsCSV()
                   .print();
        print();
        print("Interestingly, while it is slower to sort longer strings, it does not require more comparisons.");
        print();

        print();
        print("Experiment 3.2: Random string input of size " + n + " with a common prefix of 'prefixprefix'");
        print("                The parameter is the length of the string following the prefix.");
        print();

        exString = parameterValue -> new Experiment<>(
            Handler.generate(n, i -> "prefixprefix" + Handler.randomString(parameterValue)),
            TopDownMergeSort::sort,
            Handler::randomize
        );

        Experiments.measure(exString, MEDIUMTIME, 20)
                   .analyze("t2e3_2")
                   .saveAsCSV()
                   .print();
        print();
    }

    public static void task2_4() {

        print("------------------------------------------------");
        print("The following experiments will investigate how different types of data and contents affect running time.");

        print("Experiment 4.1: This will run with completely random values");
        print();
        int n = 1_000_000;

        Experiment<Integer[]> exInt = new Experiment<>(
            Handler.generate(n, j -> Handler.random().nextInt()),
            TopDownMergeSort::sort,
            Handler::randomize);
        Experiment<String[]> exString = new Experiment<>(
            Handler.generate(n, j -> Handler.randomString(5)),
            TopDownMergeSort::sort,
            Handler::randomize);
        Experiment<TestData[]> exTD = new Experiment<>(
            Handler.generate(n, j -> new TestData(Handler.random().nextInt(), Handler.random().nextInt())),
            TopDownMergeSort::sort,
            Handler::randomize);
        Experiment<Double[]> exDouble = new Experiment<>(
            Handler.generate(n, i -> Handler.random().nextDouble()),
            TopDownMergeSort::sort,
            Handler::randomize);

        print(Result.resultHeaders());
        Experiments.measure(exInt,    MEDIUMTIME).analyze("t2e4_1-integers").print();
        Experiments.measure(exString, MEDIUMTIME).analyze("t2e4_1-strings").print();
        Experiments.measure(exDouble, MEDIUMTIME).analyze("t2e4_1-doubles").print();
        Experiments.measure(exTD,     MEDIUMTIME).analyze("t2e4_1-testdata").print();
        print();

        print("Experiment 4.2: This will run with completely identical values");
        print();

        exInt = new Experiment<>(
            Handler.generate(n, j -> 1),
            TopDownMergeSort::sort,
            Handler::randomize);
        exString = new Experiment<>(
            Handler.generate(n, j -> "s"),
            TopDownMergeSort::sort,
            Handler::randomize);
        exTD = new Experiment<>(
            Handler.generate(n, j -> new TestData(1, 1)),
            TopDownMergeSort::sort,
            Handler::randomize);
        exDouble = new Experiment<>(
            Handler.generate(n, i -> 0.1),
            TopDownMergeSort::sort,
            Handler::randomize);

        print(Result.resultHeaders());
        Experiments.measure(exInt,    MEDIUMTIME).analyze("t2e4_2-integers").print();
        Experiments.measure(exString, MEDIUMTIME).analyze("t2e4_2-strings").print();
        Experiments.measure(exDouble, MEDIUMTIME).analyze("t2e4_2-doubles").print();
        Experiments.measure(exTD,     MEDIUMTIME).analyze("t2e4_2-testdata").print();
        print();
    }

    // ==================================================================
    // Task 3
    // ==================================================================

    public static void task3() {
        print("=====================================================================");
        print("Task 3: Implement MergeSort with a base case of insertion sort.");
        print("=====================================================================");
        print();

        Integer[] expected = Handler.generate(100_000, i -> i);

        // Basic correctness checks.
        if (!Util.isSorted(expected)) throw new AssertionError(
            "Precondition failed: Expected data is not sorted!");
        Integer[] actual   = Handler.randomize(expected);
        if (Util.isSorted(actual)) throw new AssertionError(
            "Precondition failed: Input data is already sorted!");

        // TopDownMergeSortCutoff is our implementation.
        int comparisons = TopDownMergeSortCutoff.sort(actual, 4);

        if (!Util.isSorted(actual)) throw new AssertionError(
            "Postcondition failed: Returned data is not sorted!");

        print("A recursive top-down mergesort with a base case of insertion sort sorted an integer array of size 100000, using "
                + comparisons + " comparisons");
        print();

    }

    // ==================================================================
    // Task 4
    // ==================================================================

    public static void task4() {
        print("=====================================================================");
        print("Task 4: Design and perform an experiment to find a good value of the parameter c.");
        print("=====================================================================");
        print();

        t4e1("t4e1_1",       100);
        t4e1("t4e1_2",     1_000);
        t4e1("t4e1_3",    10_000);
        t4e1("t4e1_4",   100_000);
        t4e1("t4e1_5", 1_000_000);
        t4e1("t4e1_6", 2_000_000);

        print();
        print("It seems the optimal zone is around the 20s");
        print();

        print("Meassuring at finer scale for n=100_000");
        t4e2("t4e2_1",   100_000);
        print();
        print("Best value seems to lie around 25");

    }

    public static void t4e1(String title, int n) {

        IntFunction<Experiment<Integer[]>> ex = parameterValue -> new Experiment<>(
            Handler.generate(n, i -> i),
            data -> TopDownMergeSortCutoff.sort(data, parameterValue),
            Handler::randomize
        );

        Experiment<Integer[]> control = new Experiment<>(
            Handler.generate(n, i -> i), TopDownMergeSort::sort, Handler::randomize);

        System.out.printf("Size of array: %,d%n",n);

        print(Result.resultHeaders());
        Experiments.measure(ex, SHORTTIME, 1); // To get startup costs out of the way.
        Experiments.measure(control, SHORTTIME).analyze("control").print();
        Result r = Experiments.measure(ex, SHORTTIME, 5, 200, 1.3).analyze(title);
        Experiments.measure(ex, SHORTTIME, 4).analyze(title).add(r).saveAsCSV().print();

        print();

    }

    //More fine-grained version
    public static void t4e2(String title, int n) {


        //Define parameterized experiment (runs with different sizes of c for size n array, on random inputs)
        IntFunction<Experiment<Integer[]>> ex = parameterValue -> new Experiment<>(
            Handler.generate(n, i -> i),
            data -> TopDownMergeSortCutoff.sort(data, parameterValue),
            Handler::randomize
        );

        Experiment<Integer[]> control = new Experiment<>(
            Handler.generate(n, i -> i), TopDownMergeSort::sort, Handler::randomize);

        print("Size of array: " + n);

        print(Result.resultHeaders());
        // To get startup costs out of the way.
        Experiments.measure(ex, LONGTIME, 1); 
        //Our control meassure (Iterative Mergesort without cutoff-parameter)
        

        Result r = Experiments.measure(3, ex, MEDIUMTIME, 2, 40).analyze(title);
        //Start at first point, (add c=1 as column (with put) so that the control-sequence will correspond to that), and add other values to file
        Experiments.measure(3, control, MEDIUMTIME).analyze(title).put(Key.PARAMETER, 1.0).add(r).saveAsCSV().print();

        //Result r =Experiments.measure(ex, LONGTIME, 5, 200, 1.3).analyze(title);
        //Experiments.measure(ex, LONGTIME, 4).analyze(title).add(r).saveAsCSV().print();
         
        print();

    }

    // ==================================================================
    // Task 5
    // ==================================================================

    public static void task5() {
        print("=====================================================================");
        print("Task 5: Implement MergeSort iteratively using a stack of runs.");
        print("=====================================================================");
        print();

        Integer[] expected = Handler.generate(100_000, i -> i);

        // Basic correctness checks.
        // Util can check if an array is sorted.
        if (!Util.isSorted(expected)) throw new AssertionError(
            "Precondition failed: Expected data is not sorted!");
        Integer[] actual   = Handler.randomize(expected);
        if (Util.isSorted(actual)) throw new AssertionError(
            "Precondition failed: Input data is already sorted!");

        // BottomUpMergeSort is our iterative mergesort implementation.
        int comparisons = BottomUpMergeSort.sort(actual);

        if (!Util.isSorted(actual)) throw new AssertionError(
            "Postcondition failed: Returned data is not sorted!");

        print("An iterative top-down mergesort sorted an integer array of size 100000, using "
                + comparisons + " comparisons");
        print();
    }


    // ==================================================================
    // Task 6
    // ==================================================================

    public static void task6() {
        print("=====================================================================");
        print("Task 6: In the algorithm of Task 5, create new runs of length c.");
        print("=====================================================================");
        print();

        Integer[] expected = Handler.generate(100_000, i -> i);

        Integer[] actual   = Handler.randomize(expected);

        // BottomUpMergeSortCutoff is our iterative mergesort with run creation implementation.
        int comparisons = BottomUpMergeSortCutoff.sort(actual, 5);

        if (!Util.isSorted(actual)) throw new AssertionError(
            "Postcondition failed: Returned data is not sorted!");

        print("An iterative top-down mergesort with base case of runs of size 5 sorted an integer array of size 100000, using "
                + comparisons + " comparisons");
        print();
    }


    // ==================================================================
    // Task 7
    // ==================================================================

    public static void task7() {
        print("=====================================================================");
        print("Rerun your experiment of Task 4 for your iterative MergeSort");
        print("Compare your results for the recursive and iterative implementations.");
        print("=====================================================================");
        print();
        task7_1();
    }


    //Version corresponding to proposed version of task4:
    public static void t7e1(String title, int n) {


        //Define parameterized experiment (runs with different sizes of c for size n array, on random inputs)
        IntFunction<Experiment<Integer[]>> ex = parameterValue -> new Experiment<>(
            Handler.generate(n, i -> i),
            data -> BottomUpMergeSortCutoff.sort(data, parameterValue),
            Handler::randomize
        );

        Experiment<Integer[]> control = new Experiment<>(
            Handler.generate(n, i -> i), BottomUpMergeSort::sort, Handler::randomize);

        print("Size of array: " + n);

        print(Result.resultHeaders());
        // To get startup costs out of the way.
        Experiments.measure(ex, MEDIUMTIME, 1); 
        //Our control meassure (Iterative Mergesort without cutoff-parameter)
        Experiments.measure(control, MEDIUMTIME).analyze("control").print();

        Result r =Experiments.measure(ex, MEDIUMTIME, 5, 200, 1.3).analyze(title);
        Experiments.measure(ex, MEDIUMTIME, 4).analyze(title).add(r).saveAsCSV().print();

         
        print();

    }

    //More fine-grained version
    public static void t7e2(String title, int n) {

        //Define parameterized experiment (runs with different sizes of c for size n array, on random inputs)
        IntFunction<Experiment<Integer[]>> ex = parameterValue -> new Experiment<>(
            Handler.generate(n, i -> i),
            data -> BottomUpMergeSortCutoff.sort(data, parameterValue),
            Handler::randomize
        );

        Experiment<Integer[]> control = new Experiment<>(
            Handler.generate(n, i -> i), BottomUpMergeSort::sort, Handler::randomize);

        print("Size of array: " + n);

        print(Result.resultHeaders());
        // To get startup costs out of the way.
        Experiments.measure(ex, LONGTIME, 1); 
        Result r = Experiments.measure(3, ex, MEDIUMTIME, 2, 50).analyze(title);
        //Start at first point, (add c=1 as column (with put) so that the control-sequence will correspond to that), and add other values to file
        Experiments.measure(3, control, MEDIUMTIME).analyze(title).put(Key.PARAMETER, 1.0).add(r).saveAsCSV().print();

         
        print();

    }

    public static void task7_1() {
        ////Testing for general overview with different array sizes:

        print();
        print("Measuring large interval with multiple array sizes:");
        
        t7e1("t7e1_2",     1_000);
        t7e1("t7e1_3",    10_000);
        t7e1("t7e1_4",   100_000);
        


        print();
        print("Plot can be found in 't7p1.pdf'");
        print("It seems in general across input-sizes that the optimal zone is around 7-15");
        print();


        //Making test at more fine-grained interval
        print("Checking just for input size 100_000 for each c-value from 1-50");
        t7e2("t7e2_1",     100_000);

        print();
        print("Plot can be found in 't7p2.pdf'");
        print("While some deviations persist, around 10 seems to be a good value");
    
    }
}
