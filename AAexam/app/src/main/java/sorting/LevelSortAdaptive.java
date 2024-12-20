package sorting;

public class LevelSortAdaptive {
    private LevelSortAdaptive() {}

    /** Rearranges the array in ascending order, using the natural order
     * Works iteratively through the array by computing runs of next {@code c} elements in {@code a} 
     * (or less when not enough elements left in array), and merges them.
     * This version is adaptible in that it checks for runs in the input array and,
     * if either a weakly increasing or strictly decreasing run of length > c is found, we 
     * use that run (otherwise computes runs of length {@code c} with insertion sort).
     * @param a the array to be sorted
     * @param c the least length of runs to merge, when enough left; must be at least 1
     * @return the number of compares performed during the sort
     * @throws IllegalArgumentException if {@code c} is less than 1 */
    public static <T extends Comparable<? super T>> int sort(T[] a, int c) {
        if (c < 1) throw new IllegalArgumentException("Cutoff value must be at least 1.");
        T[] aux = a.clone();
        int compares = sort(a, aux, c);
        assert Util.isSorted(a);
        return compares;
    }
    
    //adaptive variant
    private static <T extends Comparable<? super T>> int sort(T[] a, T[] aux, int c) {
        int compares = 0, n = a.length;
        if(n<=1) return compares;

        //define capacity for stacks of runs, indexed by level
        int stackCapacity = 64 - Long.numberOfLeadingZeros(((long) n + (long) (n - 1))) + 1;
        //create stacks for determining start and ends of runs, depending on their power
        //Should work as level-stack is increasing monotonically and no consecutive runs are equal
        int[] runStart    = new int[stackCapacity];
        int[] runEnd      = new int[stackCapacity];
        int levelStack    = 0;
        int startL        = 0; //This is our first merging candidate (L), which will immediately be put in stack
        int endL          = Util.exploreRun(a, startL); //find last index of next run
        assert (endL >= startL);

        int lengthL = endL - startL + 1; // add length of run to compares
        compares   += lengthL;
        if (lengthL <= c) { //If next run not longer than c, compute run of length c with insertion sort
            endL      = Math.min(startL+c-1, n-1);
            compares += InsertionSort.sort(a, startL,endL);
        }
        
        //starting from the end of first run iterates through array in intervals of length c, run by run
        while (endL < n - 1) {
            int startN  = endL + 1;
            int endN    = Util.exploreRun(a, startN); //find new run N of length c or more: 
            int lengthN = endN - startN + 1;
            compares   += lengthN;
            if(lengthN <= c) { endN      = Math.min(startN+c-1, n-1);
                               compares += InsertionSort.sort(a, startN,endN); }

            int currentLevel = level(startL, endL, startN, endN); //compute level of boundary between run L and run N

            //update end-point of run L in end-stack, corresponding to its power
            //(its level is the level of right boundary):
            //To be used for potential merges in stack (if top level < currentLevel)
            runEnd[currentLevel] = endL;
            
            //define level of the run in top of the stack 
            //(must be position of LSB, due to stack invariant of monotonic and decreasing):
            int topLevel = Integer.numberOfTrailingZeros(levelStack) + 1;

            //If this level greater than level of run at top of stack (which must be the position of the LSB, as stack is monotonic):
            //also won't be equal since no consecutive boundaries have same level
            //1: while levelStack is non-empty (!=0), and current level bigger than top level in stack
            while (levelStack != 0 && currentLevel > topLevel) { // merge L with run in top of stack
                int lo      = runStart[topLevel];
                int mid     = runEnd[topLevel];
                int hi      = endL;
                compares   += Merge.merge(a, aux, lo, mid, hi);
                levelStack &= ~(1 << (topLevel - 1)); // remove the (now merged) top run from the levelStack
                topLevel    = Integer.numberOfTrailingZeros(levelStack) + 1; //Update level of top run:
                startL      = lo; //update starting-point of L (according to merge)
            }

            //When l > currentLevel, put L on stack, and set start of run (updated in loop if entered):
            levelStack            |= 1 << (currentLevel - 1);
            runStart[currentLevel] = startL; //Update start of run in stack with the level of L 
            startL                 = startN; //Set N as the new L
            endL                   = endN;
        }

        int hi = endL; //set endpoint for finishing merges (will just be right end of array)
        while(levelStack !=0) {
            int topLevel = Integer.numberOfTrailingZeros(levelStack) + 1;
            int lo = runStart[topLevel];
            int mid = runEnd[topLevel];
            compares += Merge.merge(a, aux, lo, mid, hi);
            levelStack &= ~(1 << (topLevel - 1)); // remove the run from the levelStack
        }
        return compares - 1; // deduct 1 from compares to account for one less compare when exploring last run in array
    }

    public static int level(int startL, int endL, int startN, int endN) {
        return 64 - Long.numberOfLeadingZeros(((long) startL + (long) (endL + 1))^((long) startN+ (long) (endN + 1)));
    }
}
