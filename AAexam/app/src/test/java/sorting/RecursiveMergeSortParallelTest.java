package sorting;

import static data.Handler.randomString;
import static data.Handler.generate;
import static data.Handler.randomize;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sorting.Util.isSorted;
import static sorting.RecursiveMergeSortParallel.sort;

import java.util.Arrays;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import data.Handler;
import data.TestData;

public class RecursiveMergeSortParallelTest {

    // ====================================
    // String tests
    // ====================================

    @Test void
    givenRandomStrings_whenSort_thenSorted() {
        int n = 100, m = 20;
        String[] in = generate(n, i -> randomString(m));
        assertFalse(isSorted(in));
        sort(in, 10);
        System.out.println(Arrays.toString(in));
        assertTrue(isSorted(in));
    }

    //-----------------------------------
    //Case: Empty array
    //-----------------------------------
    @Test
    void givenEmptyArray_whenMergeSort_thenEmptyArray() {
        Integer[] emptyArray = new Integer[0];
        sort(emptyArray, 4);
        assertArrayEquals(new Integer[]{}, emptyArray);
    }

    @Test
    void givenEmptyArray_whenMergeSort_thenReturnZeroCompares() {
        Integer[] emptyArray = new Integer[0];
        assertEquals(0, sort(emptyArray, 4));
    }

    //-----------------------------------
    //Case: Only one element
    //-----------------------------------
    @Test
    void givenSizeOneArray_whenMergeSort_thenIdenticalArray() {
        Integer[] sizeOneArray = new Integer[]{1};
        sort(sizeOneArray, 4);
        assertArrayEquals(new Integer[]{1}, sizeOneArray);
    }

    @Test
    void givenSizeOneArray_whenMergeSort_thenReturnZeroCompares() {
        Integer[] sizeOneArray = new Integer[]{1};
        assertEquals(0, sort(sizeOneArray, 4));
    }

    //-----------------------------------
    //Case: Descending order rather than ascending
    //-----------------------------------
    @Test
    void givenDescendingArray_whenMergeSort_thenAscendingArray() {
        Integer[] descendingArray = new Integer[]{2,1,0,-1};
        sort(descendingArray, 4);
        assertArrayEquals(new Integer[]{-1,0,1,2}, descendingArray);
    }

    @Test
    void givenDescendingArray_whenMergeSort_thenReturnNumberOfCompares() {
        Integer[] descendingArray = new Integer[]{2,1,0,-1};
        assertEquals(4, sort(descendingArray, 4));
    }

    //-----------------------------------
    //Case: Odd amount of elements
    //-----------------------------------
    @Test
    void givenOddSizeArray_whenMergeSort_thenSortsArray() {
        Integer[] oddSizeArray = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
        sort(oddSizeArray, 4);
        assertArrayEquals(new Integer[]{0,1,2,3,4,5,6,7,8,9,10}, oddSizeArray);
    }

    @Test
    void givenOddSizeArray_whenMergeSort_thenReturnNumberOfCompares() {
        Integer[] oddSizeArray = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
        assertEquals(29, sort(oddSizeArray, 4));
    }


    //-----------------------------------
    //Case: all elements equal value (using Testdata tuples with identical values and distinct id's)
    //As such, if not identical would imply that some tuples with equal value were switched (e.g. try value = -i)
    //-----------------------------------
    @Test
    void givenIdenticalElementsArray_whenMergeSort_thenPreserveStability() {
        TestData[] identicalElementsArray = generate(10, i -> new TestData(i,0));
        sort(identicalElementsArray, 4);
        assertArrayEquals(generate(10, i -> new TestData(i,0)), identicalElementsArray);
    }

    @Test
    void givenIdenticalElementsArray_whenMergeSort_thenReturnNumberOfCompares() {
        TestData[] identicalElementsArray = generate(10, i -> new TestData(i,0));
        assertEquals(19, sort(identicalElementsArray, 6));
    }

    //-----------------------------------
    //Case: small array contains duplicates (for easier readability compared to medium array)
    //-----------------------------------
    @Test
    void givenDuplicateElementsArray_whenMergeSort_thenSortsArrayStably() {
        TestData[] duplicateElementsArray = Handler.readData(Handler.streamFile("unittest/duplicateElems.TestData.in"), 
        TestData::from); 
        TestData[] expectedArray = Handler.readData(Handler.streamFile("unittest/duplicateElems.TestData.out"), 
        TestData::from); 
        sort(duplicateElementsArray, 4);
        assertArrayEquals(expectedArray, duplicateElementsArray);
    }

    @Test
    void givenDuplicateElementsArray_whenMergeSort_thenReturnNumberOfCompares() {
        TestData[] duplicateElementsArray = Handler.readData(Handler.streamFile("unittest/duplicateElems.TestData.in"), 
        TestData::from); 
        assertEquals(8, sort(duplicateElementsArray, 4));
    }

    //-----------------------------------
    //Case: medium array contains duplicates
    //-----------------------------------
    //TODO: Important! When one of the files are modified, it doesn't seem to be updated in the read array (a cache seems to be used) (Maybe okay and because it has to be built also?)
    //changing name and back can solve this ish
    @Test
    void givenDuplicateElementsArrayMedium_whenMergeSort_thenSortsArrayStably() {
        TestData[] duplicateElementsArray = Handler.readData(Handler.streamFile("unittest/duplicateElemsMedium.TestData.in"), 
        TestData::from); 
        TestData[] expectedArray = Handler.readData(Handler.streamFile("unittest/duplicateElemsMedium.TestData.out"), 
        TestData::from); 

        // Debug: Log input and expected output
        //System.out.println("Input Array: " + Arrays.toString(duplicateElementsArray));
        //System.out.println("Expected Array: " + Arrays.toString(expectedArray));

        sort(duplicateElementsArray, 4);

        // Debug: sorted input
        //System.out.println("Sorted Array: " + Arrays.toString(duplicateElementsArray));

        assertArrayEquals(expectedArray, duplicateElementsArray);
    }    

    @Test
    void givenDuplicateElementsArrayMedium_whenMergeSort_thenReturnNumberOfCompares() {
        TestData[] duplicateElementsArray = Handler.readData(Handler.streamFile("unittest/duplicateElemsMedium.TestData.in"), 
        TestData::from); 

        assertEquals(23, sort(duplicateElementsArray, 4));
    }    

    @RepeatedTest(200)
    @Test void givenLargeArray_whenSort_thenIdenticalResultToSequential() {
        Integer[] actual   = randomize(generate(10_000, i -> i));
        Integer[] expected = actual.clone();
        TopDownMergeSort.sort(expected);
        sort(actual, 32);
        assertArrayEquals(expected, actual);
        
    }

}
