package sorting;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import data.Handler;
import data.TestData;

public class BinomialSortTest {

    //-----------------------------------
    //Case: Invalid cutoff value
    //-----------------------------------
    @Test void givenInvalidCutoffvalue_whenSort_thenThrowsException() {
        Exception e = assertThrows(IllegalArgumentException.class, 
        () -> BinomialSort.sort(new Integer[]{1,2,3},-2));
        assertEquals("Cutoff value must be at least 1.", e.getMessage());
    }

    //-----------------------------------
    //Case: Empty array
    //-----------------------------------
    @Test void givenEmptyArray_whenSort_thenEmptyArray() {
        Integer[] emptyArray = new Integer[0];
        BinomialSort.sort(emptyArray, 1);
        assertArrayEquals(new Integer[]{}, emptyArray);
    }

    @Test void givenEmptyArray_whenSort_thenReturnNumberOfCompares() {
        Integer[] emptyArray = new Integer[0];
        assertEquals(0, BinomialSort.sort(emptyArray, 1));
    }

    //-----------------------------------
    //Case: Only one element
    //-----------------------------------
    @Test void givenSizeOneArray_whenSort_thenIdenticalArray() {
        Integer[] sizeOneArray = {1};
        BinomialSort.sort(sizeOneArray, 1);
        assertArrayEquals(new Integer[]{1}, sizeOneArray);
    }

    @Test void givenSizeOneArray_whenSort_thenReturnNumberOfCompares() {
        Integer[] sizeOneArray = {1};
        assertEquals(0, BinomialSort.sort(sizeOneArray,1));
    }

    //-----------------------------------
    // Case: Filling stack completely (when n is a power of 2 - 1)
    // This catches the error of forgetting to add 1 to compensate for dummy value.
    // ID: |1| in BinomaialSort
    //-----------------------------------
    @Test void given2toTheXminus1sizeArray_whenSortc1_thenSortsArray() {
        for (int i = 2; i < 2048; i *= 2) {
            Integer[] expected = Handler.generate(i - 1, j -> j);
            Integer[] actual = Handler.randomize(expected);
            BinomialSort.sort(actual, 1);
            assertArrayEquals(expected, actual);
        }
    }

    //-----------------------------------
    //Case: Just one run put in stack during initial iteration
    //------------------------------------
    @Test void givenDescendingArray_Sortc2_thenAscendingArray() {
        Integer[] descendingArray = new Integer[]{2,1,0,-1};
        BinomialSort.sort(descendingArray, 2);
        assertArrayEquals(new Integer[]{-1,0,1,2}, descendingArray);
    }

    @Test void givenDescendingArray_Sortc2_thenReturnNumberOfCompares() {
        Integer[] descendingArray = new Integer[]{2,1,0,-1};
        assertEquals(4, BinomialSort.sort(descendingArray, 2));
    }

    //-----------------------------------
    //Case: array length not evenly divisible by run lengths (last run will not be of length c)
    //-----------------------------------
    @Test void givenOddSizeArray_whenSortc3_thenSortsArray() {
        Integer[] oddSizeArray = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
        BinomialSort.sort(oddSizeArray, 3);
        assertArrayEquals(new Integer[]{0,1,2,3,4,5,6,7,8,9,10}, oddSizeArray);
    }

    @Test void givenOddSizeArray_whenSortc3_thenReturnNumberOfCompares() {
        Integer[] oddSizeArray = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
        assertEquals(29, BinomialSort.sort(oddSizeArray, 3));
    }

    //-----------------------------------
    //Case: all elements equal value (using Testdata tuples with identical values and distinct id's)
    //As such, if not identical would imply that some tuples with equal value were switched (e.g. try value = -i)
    //-----------------------------------
    @Test void givenIdenticalElementsArray_whenSort_thenPreserveStability() {
        TestData[] identicalElementsArray = Handler.generate(10, i -> new TestData(i,0));
        int compares = BinomialSort.sort(identicalElementsArray,4);
        assertArrayEquals(Handler.generate(10, i -> new TestData(i,0)), identicalElementsArray);
        assertEquals(19, compares);
    }

    //-----------------------------------
    //Case: small array contains duplicates (for easier readability compared to medium array)
    //-----------------------------------
    @Test void givenDuplicateElementsArray_whenSort_thenSortsArrayStably() {
        TestData[] actual = Handler.readData(
            Handler.streamFile("unittest/duplicateElems.TestData.in"), TestData::from); 
        TestData[] expected = Handler.readData(
            Handler.streamFile("unittest/duplicateElems.TestData.out"), TestData::from); 
        int compares = BinomialSort.sort(actual,2);
        assertArrayEquals(expected, actual);
        assertEquals(9, compares);
    }

    //-----------------------------------
    //Case: medium array contains duplicates
    //-----------------------------------
    //changing name and back can solve this ish
    @Test void givenDuplicateElementsArrayMedium_whenSort_thenSortsArrayStably() {
        TestData[] actual = Handler.readData(
            Handler.streamFile("unittest/duplicateElemsMedium.TestData.in"), TestData::from); 
        TestData[] expected = Handler.readData(
            Handler.streamFile("unittest/duplicateElemsMedium.TestData.out"), TestData::from); 
        int compares = BinomialSort.sort(actual, 1);
        assertArrayEquals(expected, actual);
        assertEquals(25, compares);
    }  

}
