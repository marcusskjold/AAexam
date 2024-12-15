package sorting;

import java.util.Arrays;

public class LevelSort {
    private LevelSort(){}


    /**
     * Rearranges the array in ascending order, using the natural order
     * Works iteratively through by computing runs of next {@code c} elements in {@code a} 
     * (or less when not enough elements left in array), and merges them
     * @param a the array to be sorted
     * @param c the initial length of runs to merge, when enough left; must be at least 1
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

    //Trying to create non-adaptive variant (always create new runs of length c)
    private static int sort(Comparable[] a, Comparable[] aux, int c) {
        //Counter for compare and length defined
        int compares = 0;
        int n = a.length;
        //check if any compares necessary at all
        if(n<=1) return compares;

        //TODO: capacity depends on skewedness? (can't exceed MSB of n-2+n-1 ish?)
        //Compute the capacity of our stack of runs (as they seem to be doubled each time, this value should be okay?) //TODO: check that this is okay when in levelsort rather than powersort and/or just use list instead?
        int stackCapacity = (int)(Math.log(n) / Math.log(2)) + 2;
        //create stacks for determining start and ends of runs, depending on their power
        //Should work as level-stack is increasing monotonically and no consecutive runs are equal
        int[] runStart = new int[stackCapacity];
        int[] runEnd = new int[stackCapacity];
        
        //stack maintaining levels (Might turn out to be better with counter)
        int levelStack = 0;

        //TODO: maybe defining this initially might just be necessary for adaption?


        //TODO: should any start and end points in array be updated here?
        //create initial run from 0 of length c (or lesser if constrained by array size)
        //This is our first merging candidate (L), which will immediately be put in stack
        int startL = 0;
        int endL = Math.min(startL+c-1, n-1);
        compares += InsertionSort.sort(a, startL,endL);

        //Start and end index for next run, N, to compare with
        //endN is set to endL in case of just one run in whole stack (to be used below this loop)
        int startN;
        int endN = endL;

        //starting from the end of first run
        //iterates through array in intervals of length c, run by run
        for(int i=endL+1; i+c-1<n; i+=c) {
            //find new run N of length c: 
            startN = i;
            endN = startN+c-1;
            compares += InsertionSort.sort(a,startN,endN);
            //compute level of boundary between run L and run N
            int currentLevel =  level(startL, endL, startN, endN);
            //update end-point of run L (its level is the level of right boundary):
            
            runEnd[currentLevel] = endL;
            
            //If this level greater than level of run at top of stack (which must be the position of the LSB, as stack is monotonic):
            //also won't be equal since no consecutive boundaries have same level
            //1: while levelStack is non-empty (!=0), and current level bigger than top level in stack
            while(levelStack!=0 && currentLevel > Integer.numberOfTrailingZeros(levelStack) + 1) {
                //define level of the run in top of the stack (must be position of LSB, due to stack invariant of monotonic and decreasing):
                int topLevel = Integer.numberOfTrailingZeros(levelStack) + 1;
                //merge L with run in top of stack
                int lo = runStart[topLevel];
                int mid = runEnd[topLevel];
                int hi = endL;
                //debug
                System.out.println("Now merging: a[" + lo + " .. " + mid + "] + a [" + mid + " + 1 .. " + hi + "]");
                compares += Merge.merge(a, aux, lo, mid, hi);
                //remove the run from the levelStack (by AND with 1 leftshifted by toplevel - 1) (and update starting-point of L)
                levelStack &= ~(1 << (topLevel - 1));
                levelStack-=topLevel;
                //runStart[currentLevel] = lo;
                startL = lo;
            }
            //When l > currentLevel, put L on stack (Bitwise or with 1 leftshifted lvl -1 times), and set start of run (updated in loop if entered):
            levelStack|=1 << (currentLevel -1 );
            runStart[currentLevel] = startL;
            //Set N as the new L
            startL = startN;
            endL = endN;
        }
        //if run with length < c left (no runs of length >=c should be left after this iteration)
        if(endN + 1 < n) {
            //get indeces for residual run and sort it
            startN = endN + 1;
            endN = n - 1;
            compares += InsertionSort.sort(a, startN,endN);
        }
        //set endpoint for finishing merges (will just be right end of array)
        int hi = endN;
        //Then pop elements from stack one by one, and merge it with top run:
        while(levelStack !=0) {
            int topLevel = Integer.numberOfTrailingZeros(levelStack) + 1;
            int lo = runStart[topLevel];
            int mid = runEnd[topLevel];
            compares += Merge.merge(a, aux, lo, mid, hi);
            //debug
            System.out.println("Now merging: a[" + lo + " .. " + mid + "] + a [" + mid + " + 1 .. " + hi + "]");
            //remove the run from the levelStack (and update starting-point of L)
            //TODO: remove toplevelth bit rather than toplevel, as such
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
