package sorting;

import java.util.Arrays;

public class LevelSortAdaptive {
    private LevelSortAdaptive() {}

    //TODO: Right now it doesn't include the compares from exploring runs in the returned number of compares!!
    /**
     * Rearranges the array in ascending order, using the natural order
     * Works iteratively through the array by computing runs of next {@code c} elements in {@code a} 
     * (or less when not enough elements left in array), and merges them.
     * This version is adaptible in that it checks for runs in the input array and,
     * if either a weakly increasing or strictly decreasing run of length > c is found, we 
     * use that run (otherwise computes runs of length {@code c} with insertion sort).
     * @param a the array to be sorted
     * @param c the least length of runs to merge, when enough left; must be at least 1
     * @return the number of compares performed during the sort
     * @throws IllegalArgumentException if {@code c} is less than 1
     */
    public static int sort(Comparable[] a, int c) {
        if (c < 1) {
            throw new IllegalArgumentException("Cutoff value must be at least 1.");
        }
        Comparable[] aux = new Comparable[a.length];
        int compares = sort(a, aux, c);
        assert SortUtils.isSorted(a);
        return compares;
    }

    
    //adaptive variant
    private static int sort(Comparable[] a, Comparable[] aux, int c) {

        //Counter for compare and length defined
        int compares = 0;
        int n = a.length;

        //check if sorting necessary at all
        if(n<=1) return compares;

        //define capacity for stacks of runs, indexed by level
        //Can't exceed the position of the most significant bit of the sum of n and n - 1 (max level) +1 (accounting for that first index is 0 rather than 1)
        int stackCapacity = 64 - Long.numberOfLeadingZeros(((long) n + (long) (n - 1))) + 1;

        //create stacks for determining start and ends of runs, depending on their power
        //Should work as level-stack is increasing monotonically and no consecutive runs are equal
        int[] runStart = new int[stackCapacity];
        int[] runEnd = new int[stackCapacity];
        
        //stack maintaining levels
        int levelStack = 0;

        //create initial run from 0 of length c (or lesser if constrained by array size)
        //This is our first merging candidate (L), which will immediately be put in stack
        int startL = 0;

        //find last index of next run
        int endL = RunUtils.exploreRun(a, startL);

        //If next run not longer than c, compute run of length c with insertion sort
        if(endL - startL + 1 <= c) {
            endL = Math.min(startL+c-1, n-1);
            compares += InsertionSort.sort(a, startL,endL);
        }
        
        //starting from the end of first run
        //iterates through array in intervals of length c, run by run
        //while the end of the array hasn't been reached
        while(endL < n - 1) {

            //find new run N of length c or more: 
            int startN = endL + 1;
            int endN = RunUtils.exploreRun(a, startN);
            if(endN - startN + 1 <= c) {
                endN = Math.min(startN+c-1, n-1);
                compares += InsertionSort.sort(a, startN,endN);
            }

            //compute level of boundary between run L and run N
            int currentLevel =  level(startL, endL, startN, endN);

            //update end-point of run L in end-stack, corresponding to its power
            //(its level is the level of right boundary):
            //To be used for potential merges in stack (if top level < currentLevel)
            runEnd[currentLevel] = endL;
            
            //If this level greater than level of run at top of stack (which must be the position of the LSB, as stack is monotonic):
            //also won't be equal since no consecutive boundaries have same level
            //1: while levelStack is non-empty (!=0), and current level bigger than top level in stack
            while(levelStack!=0 && currentLevel > Integer.numberOfTrailingZeros(levelStack) + 1) {

                //define level of the run in top of the stack 
                //(must be position of LSB, due to stack invariant of monotonic and decreasing):
                int topLevel = Integer.numberOfTrailingZeros(levelStack) + 1;

                //merge L with run in top of stack
                int lo = runStart[topLevel];
                int mid = runEnd[topLevel];
                int hi = endL;
                //debug
                System.out.println("Now merging: a[" + lo + " .. " + mid + "] + a [" + mid + " + 1 .. " + hi + "]");
                compares += Merge.merge(a, aux, lo, mid, hi);
                //remove the (now merged) top run from the levelStack (by AND with 1 leftshifted by toplevel - 1) 
                levelStack &= ~(1 << (topLevel - 1));
                //update starting-point of L (according to merge)
                startL = lo;
            }

            //When l > currentLevel, put L on stack (Bitwise or with 1 leftshifted lvl -1 times), and set start of run (updated in loop if entered):
            levelStack|= 1 << (currentLevel - 1);
            //Update start of run in stack with the level of L (only one with this level pr. stack property)
            runStart[currentLevel] = startL;

            //Set N as the new L
            startL = startN;
            endL = endN;
        }
        
        //debug:
        System.out.println("finishing stack:");

        //set endpoint for finishing merges (will just be right end of array)
        int hi = endL;
        //Then pop elements from stack one by one, and merge it with top run:
        //(Has less explanation, as this is largely a simpler version of the initial iteration)
        while(levelStack !=0) {
            int topLevel = Integer.numberOfTrailingZeros(levelStack) + 1;
            int lo = runStart[topLevel];
            int mid = runEnd[topLevel];
            compares += Merge.merge(a, aux, lo, mid, hi);
            //debug
            System.out.println("Now merging: a[" + lo + " .. " + mid + "] + a [" + mid + " + 1 .. " + hi + "]");
            //remove the run from the levelStack
            levelStack &= ~(1 << (topLevel - 1));
        }
        
        //debug
        System.out.println(Arrays.toString(a));

        return compares;
    }


    //TODO: is casting most efficient way?
    public static int level(int startL, int endL, int startN, int endN) {
        return 64 - Long.numberOfLeadingZeros(((long) startL + (long) (endL + 1))^((long) startN+ (long) (endN + 1)));
    }
}