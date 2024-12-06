import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import data.Handler;
import data.TestData;
import sorting.InsertionSort;
import sorting.TopDownMergeSort;

public class InsertionSortTest {

    //Case: Empty array
    @Test
    void givenEmptyArray_whenInsertionSort_thenEmptyArray() {
        Integer[] emptyArray = new Integer[0];
        InsertionSort.sort(emptyArray);
        assertArrayEquals(new Integer[]{}, emptyArray);
    }

    @Test
    void givenEmptyArray_whenInsertionSort_thenReturnZeroCompares() {
        Integer[] emptyArray = new Integer[0];
        assertEquals(0, InsertionSort.sort(emptyArray));
    }

    //Case: Only one element
    @Test
    void givenSizeOneArray_whenInsertionSort_thenIdenticalArray() {
        Integer[] sizeOneArray = new Integer[]{1};
        InsertionSort.sort(sizeOneArray);
        assertArrayEquals(new Integer[]{1}, sizeOneArray);
    }

    @Test
    void givenSizeOneArray_whenInsertionSort_thenReturnZeroCompares() {
        Integer[] sizeOneArray = new Integer[]{1};
        assertEquals(0, InsertionSort.sort(sizeOneArray));
    }

    //Case: Descending order rather than ascending (worst case)
    @Test
    void givenDescendingArray_whenInsertionSort_thenAscendingArray() {
        Integer[] descendingArray = new Integer[]{2,1,0,-1};
        InsertionSort.sort(descendingArray);
        assertArrayEquals(new Integer[]{-1,0,1,2}, descendingArray);
    }

    @Test
    void givenDescendingArray_whenInsertionSort_thenReturnNumberOfCompares() {
        Integer[] descendingArray = new Integer[]{2,1,0,-1};
        assertEquals(6, InsertionSort.sort(descendingArray));
    }

    @Test
    void givenDescendingArray_whenInsertionSort_thenAscendingSubArray() {
        Integer[] descendingArray = new Integer[]{2,1,0,-1};
        InsertionSort.sort(descendingArray, 0, 2);
        assertArrayEquals(new Integer[]{0,1,2,-1}, descendingArray);
    }


    //case: already sorted (best case)
    @Test
    void givenAlreadySortedArray_whenInsertionSort_thenReturnNumberOfCompares() {
        Integer[] sortedArray = new Integer[]{0,1,2,3,4,5,6};
        assertEquals(6, InsertionSort.sort(sortedArray));
    }


    //Case: Odd amount of elements
    @Test
    void givenOddSizeArray_whenInsertionSort_thenSortsArray() {
        Integer[] oddSizeArray = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
        InsertionSort.sort(oddSizeArray);
        assertArrayEquals(new Integer[]{0,1,2,3,4,5,6,7,8,9,10}, oddSizeArray);
    }

    @Test
    void givenOddSizeArray_whenInsertionSort_thenReturnNumberOfCompares() {
        Integer[] oddSizeArray = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
        assertEquals(26, InsertionSort.sort(oddSizeArray));
    }

    @Test
    void givenOddSizeArray_whenInsertionSort_thenSortsSubArray() {
        Integer[] oddSizeArray = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
        InsertionSort.sort(oddSizeArray,2,6);
        assertArrayEquals(new Integer[]{9,1,0,2,3,4,7,8,5,6,10}, oddSizeArray);
    }


    //Case all elements equal value (using Testdata tuples with identical values and distinct id's)
    @Test
    void givenIdenticalElementsArray_whenInsertionSort_thenPreserveStability() {
        TestData[] identicalElementsArray = Handler.generate(10, i -> new TestData(i,0));
        InsertionSort.sort(identicalElementsArray);
        assertArrayEquals(Handler.generate(10, i -> new TestData(i,0)), identicalElementsArray);
    }

    @Test
    void givenIdenticalElementsArray_whenInsertionSort_thenReturnNumberOfCompares() {
        TestData[] identicalElementsArray = Handler.generate(10, i -> new TestData(i,0));
        assertEquals(9, InsertionSort.sort(identicalElementsArray));
    }

    //case: small array contains duplicates (for easier readability compared to medium array)
    @Test
    void givenDuplicateElementsArray_whenInsertionSort_thenSortsArrayStably() {
        TestData[] identicalElementsArray = Handler.readData(Handler.streamFile("unittest/duplicateElems.TestData.in"), 
        TestData::from); 
        TestData[] expectedArray = Handler.readData(Handler.streamFile("unittest/duplicateElems.TestData.out"), 
        TestData::from); 
        InsertionSort.sort(identicalElementsArray);
        assertArrayEquals(expectedArray, identicalElementsArray);
    }

    //case: medium array contains duplicates
    //TODO: Important! When one of the files are modified, it doesn't seem to be updated in the read array (a cache seems to be used) (Maybe okay and because it has to be built also?)
    //changing name and back can solve this ish
    @Test
    void givenDuplicateElementsArrayMedium_whenInsertionSort_thenSortsArrayStably() {
        TestData[] identicalElementsArray = Handler.readData(Handler.streamFile("unittest/duplicateElemsMedium.TestData.in"), 
        TestData::from); 
        TestData[] expectedArray = Handler.readData(Handler.streamFile("unittest/duplicateElemsMedium.TestData.out"), 
        TestData::from); 
        InsertionSort.sort(identicalElementsArray);
        assertArrayEquals(expectedArray, identicalElementsArray);
    } 

}
