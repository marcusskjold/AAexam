package scripts;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

import data.Handler;
import experiments.Experiment;
import experiments.Experiments;
import experiments.Result;
import experiments.Result.Key;
import sorting.BinomialSort;
import sorting.BinomialSortAdaptive;
import sorting.LevelSort;
import sorting.LevelSortAdaptive;
import sorting.TopDownMergeSort;
import sorting.Util;

public class Part2 {

    static final double LONGTIME = 1.0;
    static final double MEDIUMTIME = 0.5;
    static final double SHORTTIME = 0.2;
    static int n = 100_000;

    public static void print(String s) { System.out.println(s); }
    public static void print()         { System.out.println(); }

    public static void main(String[] args) {
        task8();
        task9();


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
        //TODO: Is random input identical for each element?
        print("Running all sorting algorithms on random input of size: " + n);
        //t9Random("t9random", n);
        print("Running all sorting algorithms on min-run, alternating input of size: " + n);
        t9MinRuns("t9minrun", n);
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
        double pScale= 1.5;
        double time= MEDIUMTIME;

        measurePart2Variants(title, lvlNonAdaptiveRandom, lvlAdaptiveRandom, binomNonAdaptiveRandom, binomAdaptiveRandom, pMin, pMax, time, 0.0);
        //print(Result.resultHeaders());
        //Experiments.measure(lvlNonAdaptiveRandom, time, pMin, pMax).analyze(title + 1).saveAsCSV().print();
        //Experiments.measure(lvlAdaptiveRandom, time, pMin, pMax).analyze(title + 2).saveAsCSV().print();
        //Experiments.measure(binomNonAdaptiveRandom, time, pMin, pMax).analyze(title + 3).saveAsCSV().print();
        //Experiments.measure(binomAdaptiveRandom, time, pMin, pMax).analyze(title + 4).saveAsCSV().print();

        print();
        print("Compare-wise it seems that the adaptive versions perform more compares for smaller values of c (about up to 10)");
        print("afterwards they perform about the same, as could be expected ( since cost of exploring runs is lower compared to insertionsort");
        print("since probability of finding runs with that length is lower)");
        print("Time-wise, results follow about the same pattern, but results are more volatile");
        print("In general it seems that low values of c around 5-10 could give some improvements, but not noticeable much");
        print("This makes sense to us, as the length of runs in a random input usually isn't very high");
        print("There are certain spikes, especially for the Binomial-sort-variants, around c-values that roughly amounts to n");
        print("when multiplied by a factor of 2. This could pertain to the skewedness of the resulting binary merge-tree");


    }

    public static void t9MinRuns(String title, int n) {
        
        Integer[] alternatingMinRuns = Handler.generate(n, i -> (int) Math.pow(-1, i) * (n - i));
        //Non-adaptive LevelSort
        IntFunction<Experiment<Integer[]>> lvlNonAdaptiveRandom = c -> new Experiment<>(
            alternatingMinRuns.clone(),
            i -> LevelSort.sort(i, c),
            Handler::randomize
        );

        //Adaptive LevelSort
        IntFunction<Experiment<Integer[]>> lvlAdaptiveRandom = c -> new Experiment<>(
            alternatingMinRuns.clone(),
            i -> LevelSortAdaptive.sort(i, c),
            Handler::randomize
        );

        //Non-adaptive BinomialSort
        IntFunction<Experiment<Integer[]>> binomNonAdaptiveRandom = c -> new Experiment<>(
            alternatingMinRuns.clone(),
            i -> BinomialSort.sort(i, c),
            Handler::randomize
        );

        //Adaptive BinomialSort
        IntFunction<Experiment<Integer[]>> binomAdaptiveRandom = c -> new Experiment<>(
            alternatingMinRuns.clone(),
            i -> BinomialSortAdaptive.sort(i, c),
            Handler::randomize
        );

        //The experiment run:
        int pMin= 1;
        int pMax= 30;
        double pScale= 1.5;
        double time= SHORTTIME;

        measurePart2Variants(title, lvlNonAdaptiveRandom, lvlAdaptiveRandom, binomNonAdaptiveRandom, binomAdaptiveRandom, pMin, pMax, time, 0.0);

        print();
        print("This experiment uses an input of alternating sequences, where each run is no longer than 2");
        print("The results are very similar to our results from random sequences, which confirms our suspicions that");
        print("run lengths will generally be low and not determine much for random inputs");

    }

    public static void t9MoreRuns(String title, int n, int r) {
        
        //Integer[] moreRuns = new Integer[n];
        //int runs = n/r;
        //int remainder = n % r;
        //for(int i=0;i<n;i+=runs) {
        //    for(int j=i; j< i+runs)
        //}

        Integer[] alternatingMinRuns = Handler.generate(n, i -> (int) Math.pow(-1, i) * (n - i));
        //Non-adaptive LevelSort
        IntFunction<Experiment<Integer[]>> lvlNonAdaptiveRandom = c -> new Experiment<>(
            alternatingMinRuns.clone(),
            i -> LevelSort.sort(i, c),
            Handler::randomize
        );

        //Adaptive LevelSort
        IntFunction<Experiment<Integer[]>> lvlAdaptiveRandom = c -> new Experiment<>(
            alternatingMinRuns.clone(),
            i -> LevelSortAdaptive.sort(i, c),
            Handler::randomize
        );

        //Non-adaptive BinomialSort
        IntFunction<Experiment<Integer[]>> binomNonAdaptiveRandom = c -> new Experiment<>(
            alternatingMinRuns.clone(),
            i -> BinomialSort.sort(i, c),
            Handler::randomize
        );

        //Adaptive BinomialSort
        IntFunction<Experiment<Integer[]>> binomAdaptiveRandom = c -> new Experiment<>(
            alternatingMinRuns.clone(),
            i -> BinomialSortAdaptive.sort(i, c),
            Handler::randomize
        );

        //The experiment run:
        int pMin= 1;
        int pMax= 30;
        double pScale= 1.5;
        double time= SHORTTIME;

        measurePart2Variants(title, lvlNonAdaptiveRandom, lvlAdaptiveRandom, binomNonAdaptiveRandom, binomAdaptiveRandom, pMin, pMax, time, 0.0);

        print();
        print("This experiment uses an input of alternating sequences, where each run is no longer than 2");
        print("The results are very similar to our results from random sequences, which confirms our suspicions that");
        print("run lengths will generally be low and not determine much for random inputs");

    }

    ///**
    // * Helper function to generate parameterized experiments
    // * of the four part 2 variants based on the input.
    // * Each experiment will have parameter c and will run
    // * on random input-arrays of size n, that are shuffled between 
    // * each try
    // * @param n the size of input arrays to conduct experiments on
    // * @param input the type of input to return experiments on
    // */
    //public static IntFunction<Experiment<Integer[]>>[] makeCExperiments(Integer[] input, int n) {
    //    //input:
    //    Random r = new Random(298092841098572l);
    //    
    //    //Non-adaptive LevelSort
    //    IntFunction<Experiment<Integer[]>> lvlNonAdaptiveRandom = c -> new Experiment<>(
    //        Handler.generate(n, i -> r.nextInt()),
    //        i -> LevelSort.sort(i, c),
    //        Handler::randomize
    //    );
//
    //    //Adaptive LevelSort
    //    IntFunction<Experiment<Integer[]>> lvlAdaptiveRandom = c -> new Experiment<>(
    //        Handler.generate(n, i -> r.nextInt()),
    //        i -> LevelSortAdaptive.sort(i, c),
    //        Handler::randomize
    //    );
//
    //    //Non-adaptive BinomialSort
    //    IntFunction<Experiment<Integer[]>> binomNonAdaptiveRandom = c -> new Experiment<>(
    //        Handler.generate(n, i -> r.nextInt()),
    //        i -> BinomialSort.sort(i, c),
    //        Handler::randomize
    //    );
//
    //    //Adaptive BinomialSort
    //    IntFunction<Experiment<Integer[]>> binomAdaptiveRandom = c -> new Experiment<>(
    //        Handler.generate(n, i -> r.nextInt()),
    //        i -> BinomialSortAdaptive.sort(i, c),
    //        Handler::randomize
    //    );
//
    //    return new ArrayList<IntFunction<Experiment<Integer[]>>>(lvlNonAdaptiveRandom,lvlAdaptiveRandom, binomNonAdaptiveRandom,binomAdaptiveRandom);
    //}
   
    //TODO: Could make clauses for null with experiments so that only non-null are used
    /**
     * Helper method to run an experiment with the adaptive vs. non-adaptive variant
     * of Levelsort and BinomialSort. The results will be printed and saved to a separate
     * csv-file for each algorithm, corresponding to the title followed by a number corresponding to:
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

    }
}
