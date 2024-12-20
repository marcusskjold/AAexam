package scripts;

import java.util.Arrays;
import java.util.Random;
import java.util.function.IntFunction;

import data.Handler;
import experiments.Experiment;
import experiments.Experiments;
import experiments.Result;
import sorting.BinomialSort;
import sorting.BinomialSortAdaptive;
import sorting.InsertionSort;
import sorting.LevelSort;
import sorting.LevelSortAdaptive;
import sorting.TopDownMergeSort;
import sorting.Util;

public class Part2 {

    static final double LONGTIME = 1.0;
    static final double MEDIUMTIME = 0.5;
    static final double SHORTTIME = 0.2;
    static int n = 100_000;
    static int bign = 1_000_000;

    public static void print(String s) { System.out.println(s); }
    public static void print()         { System.out.println(); }

    public static void main(String[] args) {
        task8();
        task9();
        task10();


    }

//----------------------------------------------
//TASK 8
//----------------------------------------------
    public static void task8() {
        print("=====================================================================");
        print("Task 8: Implement adpative and non-adaptive variants of Level Sort and Binomial Sort.");
        print("=====================================================================");
        print();
        Integer[] expected = Handler.generate(100_000, i -> i);

        // Basic correctness checks.
        // Util can check if an array is sorted.
        if (!Util.isSorted(expected)) throw new AssertionError(
            "Precondition failed: Expected data is not sorted!");
        Integer[] actualLvlNonAdaptive   = Handler.randomize(expected);
        if (Util.isSorted(actualLvlNonAdaptive)) throw new AssertionError(
            "Precondition failed: Input data is already sorted!");
        Integer[] actualLvlAdaptive = actualLvlNonAdaptive.clone();
        Integer[] actualBinomialNonAdaptive = actualLvlNonAdaptive.clone();
        Integer[] actualBinomialAdaptive = actualLvlNonAdaptive.clone();

        
        // Using LevelSort non-adaptive on array.
        int comparisons = LevelSort.sort(actualLvlNonAdaptive, 5);

        if (!Util.isSorted(actualLvlNonAdaptive)) throw new AssertionError(
            "Postcondition failed: Returned data is not sorted!");

        print("Non-adaptive Level-sort sorted a random integer array of size 100000, using "
                + comparisons + " comparisons");
        print();

         // Using LevelSort adaptive on array.
         comparisons = LevelSortAdaptive.sort(actualLvlAdaptive, 5);

         if (!Util.isSorted(actualLvlAdaptive)) throw new AssertionError(
             "Postcondition failed: Returned data is not sorted!");
 
         print("Adaptive Level-sort sorted a random integer array of size 100000, using "
                 + comparisons + " comparisons");
         print();

         // Using BinomialSort Non-adaptive on array.
         comparisons = BinomialSort.sort(actualBinomialNonAdaptive, 5);

         if (!Util.isSorted(actualBinomialNonAdaptive)) throw new AssertionError(
             "Postcondition failed: Returned data is not sorted!");
 
         print("Non-adaptive Binomial-sort sorted a random integer array of size 100000, using "
                 + comparisons + " comparisons");
         print();

         // Using BinomialSort adaptive on array.
         comparisons = BinomialSortAdaptive.sort(actualBinomialAdaptive, 5);

         if (!Util.isSorted(actualBinomialAdaptive)) throw new AssertionError(
             "Postcondition failed: Returned data is not sorted!");
 
         print("Adaptive Binomial-sort sorted a random integer array of size 100000, using "
                 + comparisons + " comparisons");
         print();
    }
//----------------------------------------------
//----------------------------------------------

    public static void task9() {
        print("=====================================================================");
        print("Task 9: Design and perform an experiment that investigates the");
        print("        influence of the value of c and the presortedness of the");
        print("        input on the running time and the number of comparissons.");
        print("=====================================================================");
        print();
        print("Running all sorting algorithms on random input of size: " + n);
        t9Random("t9random", n);
        print("Running all sorting algorithms on min-run, alternating input of size: " + n);
        t9MinRuns("t9minrun", n);
        int r = 100;
        print("Running all sorting algorithms on " + r + " runs, on array of size " + bign);
        t9MoreRuns("t9runs", bign, r);
        print("Running all sorting algorithms on a size " + n + " array with c-value " + 10 + "for various"); 
        print("amount of runs in array");
        t9DifferentRuns("t9differentRuns", n, 10);
    }


    //EXPERIMENT COMPARING ALGORITHMS ON RANDOM ARRAYS
    //  We expect adptiveness to play a negliable role
    public static void t9Random(String title, int n) {
        //input:
        Random r = new Random(298092841098572l);
        
        //Non-adaptive LevelSort
        IntFunction<Experiment<Integer[]>> lvlNonAdaptiveRandom = c -> new Experiment<>(
            Handler.generate(n, i -> r.nextInt()),
            i -> LevelSort.sort(i, c),
            Handler::randomize
        );

        //Adaptive LevelSort
        IntFunction<Experiment<Integer[]>> lvlAdaptiveRandom = c -> new Experiment<>(
            Handler.generate(n, i -> r.nextInt()),
            i -> LevelSortAdaptive.sort(i, c),
            Handler::randomize
        );

        //Non-adaptive BinomialSort
        IntFunction<Experiment<Integer[]>> binomNonAdaptiveRandom = c -> new Experiment<>(
            Handler.generate(n, i -> r.nextInt()),
            i -> BinomialSort.sort(i, c),
            Handler::randomize
        );

        //Adaptive BinomialSort
        IntFunction<Experiment<Integer[]>> binomAdaptiveRandom = c -> new Experiment<>(
            Handler.generate(n, i -> r.nextInt()),
            i -> BinomialSortAdaptive.sort(i, c),
            Handler::randomize
        );

        //The experiment run:
        int pMin= 1;
        int pMax= 30;
        double time= SHORTTIME;

        measurePart2Variants(title, lvlNonAdaptiveRandom, lvlAdaptiveRandom, binomNonAdaptiveRandom, binomAdaptiveRandom, pMin, pMax, time, 0.0);
        

        print();
        print("Compare-wise it seems that the adaptive versions perform more compares for smaller values of c (about up to 10)");
        print("afterwards they perform about the same, as could be expected ( since cost of exploring runs is lower compared to insertionsort");
        print("since probability of finding runs with that length is lower)");
        print("Time-wise, results follow about the same pattern, but results are more volatile");
        print("In general it seems that low values of c around 5-20 could give some improvements (depending on the algorithm), but not noticeable much");
        print("This makes sense to us, as the length of runs in a random input usually isn't very high");
        print("There are certain spikes, especially for the Binomial-sort-variants, around c-values that roughly amounts to n");
        print("when multiplied by a factor of 2. This could pertain to the skewedness of the resulting binary merge-tree");


    }

    //EXPERIMENT COMPARING ARRAYS WITH RUNS OF MINIMUM SIZES (2 EXCEPT MAYBE FOR LAST ELEMENT)
    public static void t9MinRuns(String title, int n) {
        
        Integer[] alternatingMinRuns = Handler.generate(n, i -> (int) Math.pow(-1, i) * (n - i));
        //Non-adaptive LevelSort
        IntFunction<Experiment<Integer[]>> lvlNonAdaptiveRandom = c -> new Experiment<>(
            alternatingMinRuns.clone(),
            i -> LevelSort.sort(i, c),
            data -> alternatingMinRuns.clone()
        );

        //Adaptive LevelSort
        IntFunction<Experiment<Integer[]>> lvlAdaptiveRandom = c -> new Experiment<>(
            alternatingMinRuns.clone(),
            i -> LevelSortAdaptive.sort(i, c),
            data -> alternatingMinRuns.clone()
        );

        //Non-adaptive BinomialSort
        IntFunction<Experiment<Integer[]>> binomNonAdaptiveRandom = c -> new Experiment<>(
            alternatingMinRuns.clone(),
            i -> BinomialSort.sort(i, c),
            data -> alternatingMinRuns.clone()
        );

        //Adaptive BinomialSort
        IntFunction<Experiment<Integer[]>> binomAdaptiveRandom = c -> new Experiment<>(
            alternatingMinRuns.clone(),
            i -> BinomialSortAdaptive.sort(i, c),
            data -> alternatingMinRuns.clone()
        );

        //The experiment run:
        int pMin= 1;
        int pMax= 30;
        double time= SHORTTIME;

        measurePart2Variants(title, lvlNonAdaptiveRandom, lvlAdaptiveRandom, binomNonAdaptiveRandom, binomAdaptiveRandom, pMin, pMax, time, 0.0);

        print();
        print("This experiment uses an input of alternating sequences, where each run is no longer than 2");
        print("The results are very similar to our results from random sequences, which confirms our suspicions that");
        print("run lengths will generally be low and not determine much for random inputs");
        print("However, in this more controlled case, a more clear pattern emerges regarding an optimal value for c");
        print("It seems to lie around 5-15 for the different algorithms");

    }

    //Varying values of c on same array split into r runs
    public static void t9MoreRuns(String title, int n, int r) {
        
        //We split the array into r runs equal size (except for last run in some cases)
        Integer[] moreRuns = new Integer[n];
        int runSize = n/r;
        for(int i=0;i<n; i+=runSize) {
            int counter = 0;
            int runLength = Math.min(n,i+runSize);
            for(int j=i; j< runLength;j++) {
                counter+=(runLength+7)/3;
                moreRuns[j] = 1234-counter;
            }
        }


        //Non-adaptive LevelSort
        IntFunction<Experiment<Integer[]>> lvlNonAdaptiveRandom = c -> new Experiment<>(
            moreRuns.clone(),
            i -> LevelSort.sort(i, c),
            data -> moreRuns.clone()
        );

        //Adaptive LevelSort
        IntFunction<Experiment<Integer[]>> lvlAdaptiveRandom = c -> new Experiment<>(
            moreRuns.clone(),
            i -> LevelSortAdaptive.sort(i, c),
            data -> moreRuns.clone()
        );

        //Non-adaptive BinomialSort
        IntFunction<Experiment<Integer[]>> binomNonAdaptiveRandom = c -> new Experiment<>(
            moreRuns.clone(),
            i -> BinomialSort.sort(i, c),
            data -> moreRuns.clone()
        );

        //Adaptive BinomialSort
        IntFunction<Experiment<Integer[]>> binomAdaptiveRandom = c -> new Experiment<>(
            moreRuns.clone(),
            i -> BinomialSortAdaptive.sort(i, c),
            data -> moreRuns.clone()
        );

        //The experiment run:
        int pMin= 1;
        int pMax= 30;
        double pScale= 1.5;
        double time= SHORTTIME;

        measurePart2Variants(title, lvlNonAdaptiveRandom, lvlAdaptiveRandom, binomNonAdaptiveRandom, binomAdaptiveRandom, pMin, pMax, time, 0.0);

        print();
        print("For this experiment we have split the input array into runs of decreasing sequences.");
        print("The improvement of the adaptive algorithms over the non-adaptive ones are very apparent");
        print("both in terms of running time and compares. The adaptive variants doesn't seem to change");
        print("much in their performance, when their c-values change");

    }

    //Varying amount of runs in array, sorted with different values for c
    public static void t9DifferentRuns(String title, int n, int c) {
        Integer[] a = new Integer[n];
        //Non-adaptive LevelSort
        IntFunction<Experiment<Integer[]>> lvlNonAdaptiveRandom = r -> new Experiment<>(
            a.clone(),
            i -> LevelSort.sort(i, c),
            data -> createRunsArray(data, r)
        );

        //Adaptive LevelSort
        IntFunction<Experiment<Integer[]>> lvlAdaptiveRandom = r -> new Experiment<>(
            a.clone(),
            i -> LevelSortAdaptive.sort(i, c),
            data -> createRunsArray(data, r)
        );

        //Non-adaptive BinomialSort
        IntFunction<Experiment<Integer[]>> binomNonAdaptiveRandom = r -> new Experiment<>(
            a.clone(),
            i -> BinomialSort.sort(i, c),
            data -> createRunsArray(data, r)
        );

        //Adaptive BinomialSort
        IntFunction<Experiment<Integer[]>> binomAdaptiveRandom = r -> new Experiment<>(
            a.clone(),
            i -> BinomialSortAdaptive.sort(i, c),
            data -> createRunsArray(data, r)
        );

        //The experiment run:
        int pMin= 1;
        int pMax= 10_000;
        double pScale= 2;
        double time= SHORTTIME;

        measurePart2Variants(title, lvlNonAdaptiveRandom, lvlAdaptiveRandom, binomNonAdaptiveRandom, binomAdaptiveRandom, pMin, pMax, time, pScale);

        print();
        print("Now we keep a constant c value of 10 and split the array into increasingly many runs");
        print("As can be seen both with regards to compares and running time, it the adaptive algorithms");
        print("gets increasingly better, as their c-value approaches the size of the runs in the input");
    }

    public static Integer[] createRunsArray(Integer[] a, int r) {
        int n = a.length;
        int runSize = n/r;
        for(int i=0;i<n; i+=runSize) {
            int counter = 0;
            int runLength = Math.min(n,i+runSize);
            for(int j=i; j< runLength;j++) {
                counter+=(runLength+7)/3;
                a[j] = 1234-counter;
            }
        }
        return a;
    }

   
    /**
     * Helper method to run an experiment with the adaptive vs. non-adaptive variant
     * of Levelsort and BinomialSort. The results will be printed and saved to a separate
     * csv-file for each algorithm, corresponding to the title followed by a number corresponding to their order.
     * For task 9, this order is:
     * <p>
     * (1): Non-adaptive LevelSort
     * <p>
     * (2): Adaptive LevelSort
     * <p>
     * (3): Non-adaptive BinomialSort
     * <p>
     * (4): Adaptive BinomialSort
     * @param title name of the csv-file
     * @param lvlNonExp the parameterized experiment on the non-adaptive levelsort
     * @param lvlAdaptExp the parameterized experiment on the adaptive levelsort
     * @param binomNonExp the parameterized experiment on the non-adaptive binomialsort
     * @param binomAdaptExp the parameterized experiment on the adaptive binomialsort
     * @param pMin min-value of parameter
     * @param pMax max-value of parameter (inclusive)
     * @param time timeout value for measurements
     * @param pScale scaling of parameter for each run (if increments rather than scaling, choose 0)
     */
    public static void measurePart2Variants(String title, 
    IntFunction<Experiment<Integer[]>> lvlNonExp,
    IntFunction<Experiment<Integer[]>> lvlAdaptExp, 
    IntFunction<Experiment<Integer[]>> binomNonExp,
    IntFunction<Experiment<Integer[]>> binomAdaptExp,
    int pMin,
    int pMax,
    double time,
    double pScale) {
        if(pScale == 0.0) {
            print(Result.resultHeaders());
            Experiments.measure(lvlNonExp, time, pMin, pMax).analyze(title + 1).saveAsCSV().print();
            Experiments.measure(lvlAdaptExp, time, pMin, pMax).analyze(title + 2).saveAsCSV().print();
            Experiments.measure(binomNonExp, time, pMin, pMax).analyze(title + 3).saveAsCSV().print();
            Experiments.measure(binomAdaptExp, time, pMin, pMax).analyze(title + 4).saveAsCSV().print();
        }
        else {
            print(Result.resultHeaders());
            Experiments.measure(lvlNonExp, time, pMin, pMax, pScale).analyze(title + 1).saveAsCSV().print();
            Experiments.measure(lvlAdaptExp, time, pMin, pMax, pScale).analyze(title + 2).saveAsCSV().print();
            Experiments.measure(binomNonExp, time, pMin, pMax, pScale).analyze(title + 3).saveAsCSV().print();
            Experiments.measure(binomAdaptExp, time, pMin, pMax, pScale).analyze(title + 4).saveAsCSV().print();
        }
    }
    

//----------------------------------------------
//----------------------------------------------

    public static void task10() {
        print("=====================================================================");
        print("Task 10: Include InsertionSort, plain MergeSort and Arrays.sort()");
        print("into the comparison. How do the algorithms compare in a horserace?"); 
        print("=====================================================================");
        print();
        print("Here is a comparisson for all algorithms (except insertion sort) for random arrays of various sizes: ");
        t10random("t10random");
        print("In general the algorithms scale similarly (Insertion-sort was too slow to be benchmarked here)");
        print("In general the java-library version performs better, which is expected, as it is unstable");
        print();
        print("Here is a comparisson for all algorithms (except insertion sort) for arrays split into different amounts of runs");
        t10MoreRuns("t10runs", 2_000_000, 10);
    }

    public static void t10random(String title) {
        //input:
        Random r = new Random(298092841098572l);
        
        //Adaptive LevelSort
        IntFunction<Experiment<Integer[]>> lvlAdaptiveRandom = n -> new Experiment<>(
            Handler.generate(n, i -> r.nextInt()),
            i -> LevelSortAdaptive.sort(i, 10),
            Handler::randomize
        );

        //Adaptive BinomialSort
        IntFunction<Experiment<Integer[]>> binomAdaptiveRandom = n -> new Experiment<>(
            Handler.generate(n, i -> r.nextInt()),
            i -> BinomialSortAdaptive.sort(i, 10),
            Handler::randomize
        );

        ////Insertion-Sort
        //IntFunction<Experiment<Integer[]>> insSortRandom = n -> new Experiment<>(
        //    Handler.generate(n, i -> r.nextInt()),
        //    i -> InsertionSort.sort(i),
        //    Handler::randomize
        //);

        //Arrays.sort library method
        IntFunction<Experiment<Integer[]>> arraysSortRandom = n -> new Experiment<>(
            Handler.generate(n, i -> r.nextInt()),
            i -> {Arrays.sort(i); return i.hashCode();},
            Handler::randomize
        );

        //Plain Mergesort
        IntFunction<Experiment<Integer[]>> mergeSortRandom = n -> new Experiment<>(
            Handler.generate(n, i -> r.nextInt()),
            i -> TopDownMergeSort.sort(i),
            Handler::randomize
        );

        //The experiment run:
        int pMin= 500_000;
        int pMax= 3_000_000;
        double pScale= 1.1;
        double time= SHORTTIME;

        measureTask10Variants(title, lvlAdaptiveRandom, binomAdaptiveRandom, arraysSortRandom, mergeSortRandom, pMin, pMax, time, pScale);

    }

    public static void t10minRuns() {
        
    }

    public static void t10MoreRuns(String title, int n, int c) {
        Integer[] a = new Integer[n];

        //Adaptive LevelSort
        IntFunction<Experiment<Integer[]>> lvlAdaptiveRuns = r -> new Experiment<>(
            a.clone(),
            i -> LevelSortAdaptive.sort(i, c),
            data -> createRunsArray(data, r)
        );

        //Adaptive BinomialSort
        IntFunction<Experiment<Integer[]>> binomAdaptiveRuns = r -> new Experiment<>(
            a.clone(),
            i -> BinomialSortAdaptive.sort(i, c),
            data -> createRunsArray(data, r)
        );

        //Arrays.sort() function
        IntFunction<Experiment<Integer[]>> arraysSortRuns = r -> new Experiment<>(
            a.clone(),
            i -> {Arrays.sort(i); return i.hashCode();},
            data -> createRunsArray(data, r)
        );

        //Plain merge-sort function
        IntFunction<Experiment<Integer[]>> mergeSortRuns = r -> new Experiment<>(
            a.clone(),
            i -> BinomialSortAdaptive.sort(i, c),
            data -> createRunsArray(data, r)
        );

        //The experiment run:
        int pMin= 1;
        int pMax= 30;
        double pScale= 0.0;
        double time= SHORTTIME;

        measurePart2Variants(title, lvlAdaptiveRuns, binomAdaptiveRuns, arraysSortRuns, mergeSortRuns, pMin, pMax, time, pScale);
    }

    public static void measureTask10Variants(String title, 
    IntFunction<Experiment<Integer[]>> lvlAdaptExp, 
    IntFunction<Experiment<Integer[]>> binomAdaptExp,
    IntFunction<Experiment<Integer[]>> arraysSort,
    IntFunction<Experiment<Integer[]>> plainmergeSort,
    int pMin,
    int pMax,
    double time,
    double pScale) {
        if(pScale == 0.0) {
            print(Result.resultHeaders());
            Experiments.measure(lvlAdaptExp, time, pMin, pMax).analyze(title + 1).saveAsCSV().print();
            Experiments.measure(binomAdaptExp, time, pMin, pMax).analyze(title + 2).saveAsCSV().print();
            Experiments.measure(arraysSort, time, pMin, pMax).analyze(title + 3).saveAsCSV().print();
            Experiments.measure(plainmergeSort, time, pMin, pMax).analyze(title + 4).saveAsCSV().print();
        }
        else {
            print(Result.resultHeaders());
            Experiments.measure(lvlAdaptExp, time, pMin, pMax, pScale).analyze(title + 1).saveAsCSV().print();
            Experiments.measure(binomAdaptExp, time, pMin, pMax, pScale).analyze(title + 2).saveAsCSV().print();
            Experiments.measure(arraysSort, time, pMin, pMax, pScale).analyze(title + 3).saveAsCSV().print();
            Experiments.measure(plainmergeSort, time, pMin, pMax, pScale).analyze(title + 4).saveAsCSV().print();
        }
    }
}
