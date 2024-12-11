import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import data.Handler;
import data.TestData;
import sorting.TopDownMergeSort;
import sorting.TopDownMergeSortCutoff;

public class TopDownMergeSortCutoffTest {
    
    //-----------------------------------
    //Case: Empty array
    //-----------------------------------
    @Test
    void givenEmptyArray_whenMergeSortCutoff_thenEmptyArray() {
        Integer[] emptyArray = new Integer[0];
        TopDownMergeSortCutoff.sort(emptyArray);
        assertArrayEquals(new Integer[]{}, emptyArray);
    }

    @Test
    void givenEmptyArray_whenMergeSortCutoff_thenReturnZeroCompares() {
        Integer[] emptyArray = new Integer[0];
        assertEquals(0, TopDownMergeSortCutoff.sort(emptyArray));
    }

    //-----------------------------------
    //Case: Only one element
    //-----------------------------------
    @Test
    void givenSizeOneArray_whenMergeSortCutoff_thenIdenticalArray() {
        Integer[] sizeOneArray = new Integer[]{1};
        TopDownMergeSortCutoff.sort(sizeOneArray);
        assertArrayEquals(new Integer[]{1}, sizeOneArray);
    }

    @Test
    void givenSizeOneArray_whenMergeSortCutoff_thenReturnZeroCompares() {
        Integer[] sizeOneArray = new Integer[]{1};
        assertEquals(0, TopDownMergeSortCutoff.sort(sizeOneArray));
    }

    //-----------------------------------
    //Case: Invalid cutoff value
    //-----------------------------------
    @Test
    void givenInvalidCutoffvalue_whenMergeSortCutoff_thenThrowsException() {
        Exception e = assertThrows(IllegalArgumentException.class, 
        () -> TopDownMergeSortCutoff.sort(new Integer[]{1,2,3},-2));
        assertEquals("Cutoff value must be at least 1.", e.getMessage());
    }

    //-----------------------------------
    //Case: Odd amount of elements 
    //-----------------------------------
    @Test
    void givenOddSizeArray_whenMergeSortCutoff_thenSortsArray() {
        Integer[] oddSizeArray = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
        TopDownMergeSortCutoff.sort(oddSizeArray,5);
        assertArrayEquals(new Integer[]{0,1,2,3,4,5,6,7,8,9,10}, oddSizeArray);
    }
        //(Uses c=5, which yields different number of compares than from no cutoff (29))
    @Test
    void givenOddSizeArray_whenMergeSortCutoff5_thenReturnNumberOfCompares() {
        Integer[] oddSizeArray = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
        assertEquals(27, TopDownMergeSortCutoff.sort(oddSizeArray,5));
    }

      //(Uses c=1, which yields same number of compares than from no cutoff (29))  
    @Test
    void givenOddSizeArray_whenMergeSortCutoff1_thenReturnSameComparesAsNoCutoff() {
        Integer[] oddSizeArray = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
        Integer[] oddSizeArray2 = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
        assertEquals(TopDownMergeSort.sort(oddSizeArray), TopDownMergeSortCutoff.sort(oddSizeArray2,1));
    }


    //-----------------------------------
    //Case: all elements equal value (using Testdata tuples with identical values and distinct id's)
    //As such, if not identical would imply that some tuples with equal value were switched (e.g. try value = -i)
    //-----------------------------------
    @Test
    void givenIdenticalElementsArray_whenMergeSortCutoff_thenPreserveStability() {
        TestData[] identicalElementsArray = Handler.generate(10, i -> new TestData(i,0));
        TopDownMergeSortCutoff.sort(identicalElementsArray,4);
        assertArrayEquals(Handler.generate(10, i -> new TestData(i,0)), identicalElementsArray);
    }

    @Test
    void givenIdenticalElementsArray_whenMergeSortCutoff_thenReturnNumberOfCompares() {
        TestData[] identicalElementsArray = Handler.generate(10, i -> new TestData(i,0));
        assertEquals(17, TopDownMergeSortCutoff.sort(identicalElementsArray,4));
    }

    //-----------------------------------
    //Case: array with duplicate elements
    //-----------------------------------
    @Test
    void givenDuplicateElementsArrayMedium_whenMergeSortCutoff_thenSortsArrayStably() {
        TestData[] duplicateElementsArray = Handler.readData(Handler.streamFile("unittest/duplicateElemsMedium.TestData.in"), 
        TestData::from); 
        TestData[] expectedArray = Handler.readData(Handler.streamFile("unittest/duplicateElemsMedium.TestData.out"), 
        TestData::from); 
        TopDownMergeSortCutoff.sort(duplicateElementsArray,5);
        assertArrayEquals(expectedArray, duplicateElementsArray);
    }    

    @Test
    void givenDuplicateElementsArrayMedium_whenMergeSortCutoff_thenReturnNumberOfCompares() {
        TestData[] duplicateElementsArray = Handler.readData(Handler.streamFile("unittest/duplicateElemsMedium.TestData.in"), 
        TestData::from);  
        assertEquals(21, TopDownMergeSortCutoff.sort(duplicateElementsArray,5));
    }  









}
