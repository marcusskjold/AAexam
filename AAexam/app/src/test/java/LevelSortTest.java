import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import sorting.BottomUpMergeSort;
import sorting.BottomUpMergeSortCutoff;
import sorting.LevelSort;

public class LevelSortTest {
    //-----------------------------------
    //Case: Descending order rather than ascending
    //Also: just one run in stack after initial computation of runs
    //------------------------------------
    //Note: succesfully puts a run in the stack and subsequently merges with second, last run (that wasn't put in stack first)
    //"Also" no residues
    @Test
    void givenDescendingArray_LevelSortc2_thenAscendingArray() {
        Integer[] descendingArray = new Integer[]{2,1,0,-1};
        LevelSort.sort(descendingArray, 2);
        assertArrayEquals(new Integer[]{-1,0,1,2}, descendingArray);
    }

    //@Test
    //void givenDescendingArray_whenMergeSortCutoff_ReturnNumberOfCompares() {
    //    Integer[] descendingArray = new Integer[]{2,1,0,-1};
    //    assertEquals(4, LevelSort.sort(descendingArray, 2));
    //}

    //-----------------------------------
    //Case: Odd amount of elements
    //-----------------------------------
    @Test
    void givenOddSizeArray_whenLevelSortc3_thenSortsArray() {
        Integer[] oddSizeArray = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
        LevelSort.sort(oddSizeArray, 3);
        assertArrayEquals(new Integer[]{0,1,2,3,4,5,6,7,8,9,10}, oddSizeArray);
    }


    //    //case: different cutoff-values give different comparrison numbers
    //@Test
    //void givenOddSizeArray_whenMergeSortCutoff3_thenReturnSameComparesAsNoCutoff() {
    //    Integer[] oddSizeArray = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
    //    assertEquals(29, LevelSort.sort(oddSizeArray,3));
    //}
        
    //@Test
    //void givenOddSizeArray_whenMergeSortCutoff5_thenReturnSameComparesAsNoCutoff() {
    //    Integer[] oddSizeArray = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
    //    assertEquals(33, LevelSort.sort(oddSizeArray,5));
    //}
}
