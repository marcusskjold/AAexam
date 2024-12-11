import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import sorting.SortUtils;

public class SortUtilsTest {
    

    //-----------------------------------
    //Case: Empty array
    //-----------------------------------
    @Test
    void givenEmptyArray_whenIsSorted_thenReturnTrue() {
        assertTrue(SortUtils.isSorted(new Integer[0]));
    }

    //-----------------------------------
    //Case: Only one element
    //-----------------------------------
    @Test
    void givenSizeOneArray_whenIsSorted_thenReturnTrue() {
        assertTrue(SortUtils.isSorted(new Integer[]{1}));
    }

    @Test
    void givenSizeOneSubArray_whenMergeSort_thenReturnZeroCompares() {
        assertTrue(SortUtils.isSorted(new Integer[]{1},0,0));
    }


    //-----------------------------------
    //Case: sorted array
    //-----------------------------------
    @Test
    void givenSortedArray_whenIsSorted_thenReturnsTrue() {
        assertTrue(SortUtils.isSorted(new Integer[]{-1,0,1,1,3,7}));
    }


    //-----------------------------------
    //Case: sorted subarray of an unsorted array
    //-----------------------------------
    @Test
    void givenSortedSubArray_whenIsSorted_thenReturnsTrue() {
        assertTrue(SortUtils.isSorted(new Integer[]{7,0,1,1,-7,-10},1,3));
    }

}
