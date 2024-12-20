package sorting;

public class LevelSort {
    private LevelSort(){}

    /** Rearranges the array in ascending order, using the natural order
     * Works iteratively through the array by computing runs of next {@code c} elements in {@code a} 
     * (or less when not enough elements left in array), and merges them
     * this version is non-adaptible in that it computes runs of length {@code c}, whether they
     * are already sorted or not
     * @param a the array to be sorted
     * @param c the initial length of runs to merge, when enough left; must be at least 1
     * @return the number of compares performed during the sort
     * @throws IllegalArgumentException if {@code c} is less than 1 */
    public static <T extends Comparable<? super T>> int sort(T[] a, int c) {
        if (c < 1) throw new IllegalArgumentException("Cutoff value must be at least 1.");
        T[] aux = a.clone();
        int compares = sort(a, aux, c);
        assert Util.isSorted(a);
        return compares;
    }

    /** Non-adaptive version - always creates runs of length c by insertionsorting*/
    private static <T extends Comparable<? super T>> int sort(T[] a, T[] aux, int c) {
        int compares = 0, n = a.length;
        if(n<=1) return compares;

        //define capacity for stacks of runs, indexed by level
        //Can't exceed the position of the most significant bit of the sum of n and n - 1 (max level) +1
        int stackCapacity = 64 - Long.numberOfLeadingZeros(((long) n + (long) (n - 1))) + 1;

        int[] runStart = new int[stackCapacity];
        int[] runEnd   = new int[stackCapacity];
        int levelStack = 0; //stack maintaining levels

        //create initial run from 0 of length c (or lesser if constrained by array size)
        //This is our first merging candidate (L), which will immediately be put in stack
        int startL = 0;
        int endL = Math.min(startL+c-1, n-1);
        compares += InsertionSort.sort(a, startL,endL);

        //starting from the end of first run
        //iterates through array in intervals of length c, run by run
        //while the end of the array hasn't been reached
        while(endL < n - 1) {
            //find new run N of length c (or less, if constrained by array length): 
            int startN = endL + 1;
            int endN = Math.min(startN+c-1, n - 1);
            compares += InsertionSort.sort(a,startN,endN);

            int currentLevel =  level(startL, endL, startN, endN); //compute level of boundary between run L and run N

            //update end-point of run L in end-stack, corresponding to its power
            //(its level is the level of right boundary):
            //To be used for potential merges in stack (if top level < currentLevel)
            runEnd[currentLevel] = endL;
            
            //define level of the run in top of the stack 
            //(must be position of LSB, due to stack invariant of monotonic and decreasing):
            int topLevel = Integer.numberOfTrailingZeros(levelStack) + 1;
            //If this level greater than level of run at top of stack
            //also won't be equal since no consecutive boundaries have same level
            while (levelStack != 0 && currentLevel > topLevel) { // merge L with run in top of stack
                int lo      = runStart[topLevel];
                int mid     = runEnd[topLevel];
                int hi      = endL;
                compares   += Merge.merge(a, aux, lo, mid, hi);
                levelStack &= ~(1 << (topLevel - 1)); //remove the (now merged) top run from the levelStack
                topLevel    = Integer.numberOfTrailingZeros(levelStack) + 1;
                startL      = lo; //update starting-point of L (according to merge)
            }

            //When l > currentLevel, put L on stack, and set start of run (updated in loop if entered):
            //Update start of run in stack with the level of L (only one with this level pr. stack property)
            levelStack            |= 1 << (currentLevel - 1);
            runStart[currentLevel] = startL;
            startL                 = startN; //Set N as the new L
            endL                   = endN;
        }

        int hi = endL; //set endpoint for finishing merges (will just be right end of array)

        while(levelStack !=0) {
            int topLevel = Integer.numberOfTrailingZeros(levelStack) + 1;
            int lo       = runStart[topLevel];
            int mid      = runEnd[topLevel];
            compares    += Merge.merge(a, aux, lo, mid, hi);
            levelStack  &= ~(1 << (topLevel - 1));
        }
        return compares;
    }

    public static int level(int startL, int endL, int startN, int endN) {
        return 64 - Long.numberOfLeadingZeros(((long) startL + (long) (endL + 1))^((long) startN+ (long) (endN + 1)));
    }
}
