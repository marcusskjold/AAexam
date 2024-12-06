import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import sorting.TopDownMergeSort;

public class TopDownMergeSortTest {

    //Case: Empty array
    @Test
    void givenEmptyArray_whenMergeSort_thenReturnEmptyArray() {
        Integer[] emptyArray = new Integer[0];
        TopDownMergeSort.sort(emptyArray);
        assertArrayEquals(new Integer[]{}, emptyArray);
    }

    @Test
    void givenEmptyArray_whenMergeSort_thenReturnZeroCompares() {
        Integer[] emptyArray = new Integer[0];
        assertEquals(0, TopDownMergeSort.sort(emptyArray));
    }

    //Case: Only one element
    @Test
    void givenSizeOneArray_whenMergeSort_thenReturnIdenticalArray() {
        Integer[] sizeOneArray = new Integer[]{1};
        TopDownMergeSort.sort(sizeOneArray);
        assertArrayEquals(new Integer[]{1}, sizeOneArray);
    }

    @Test
    void givenSizeOneArray_whenMergeSort_thenReturnZeroCompares() {
        Integer[] sizeOneArray = new Integer[]{1};
        assertEquals(0, TopDownMergeSort.sort(sizeOneArray));
    }

    //Case: Descending order rather than ascending
    @Test
    void givenDescendingArray_whenMergeSort_thenReturnAscendingArray() {
        Integer[] descendingArray = new Integer[]{2,1,0,-1};
        TopDownMergeSort.sort(descendingArray);
        assertArrayEquals(new Integer[]{-1,0,1,2}, descendingArray);
    }

    @Test
    void givenDescendingArray_whenMergeSort_thenReturnNumberOfCompares() {
        Integer[] descendingArray = new Integer[]{2,1,0,-1};
        assertEquals(4, TopDownMergeSort.sort(descendingArray));
    }

    //Case: Odd amount of elements
    @Test
    void givenOddSizeArray_whenMergeSort_thenReturnSortedArray() {
        Integer[] oddSizeArray = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
        TopDownMergeSort.sort(oddSizeArray);
        assertArrayEquals(new Integer[]{0,1,2,3,4,5,6,7,8,9,10}, oddSizeArray);
    }

    @Test
    void givenOddSizeArray_whenMergeSort_thenReturnNumberOfCompares() {
        Integer[] oddSizeArray = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
        assertEquals(29, TopDownMergeSort.sort(oddSizeArray));
    }


    //TODO: Test for stability with TestData



    

}
