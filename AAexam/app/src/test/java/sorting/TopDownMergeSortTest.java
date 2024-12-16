package sorting;

import static data.Handler.randomString;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sorting.SortUtils.isSorted;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import data.Handler;
import data.TestData;
import sorting.TopDownMergeSort;

public class TopDownMergeSortTest {

    // ====================================
    // String tests
    // ====================================

    @Test void
    givenRandomStrings_whenSort_thenSorted() {
        int n = 100, m = 20;
        String[] in = Handler.generate(n, i -> randomString(m));
        assertFalse(isSorted(in));
        TopDownMergeSort.sort(in);
        assertTrue(isSorted(in));
    }

    //-----------------------------------
    //Case: Empty array
    //-----------------------------------
    @Test
    void givenEmptyArray_whenMergeSort_thenEmptyArray() {
        Integer[] emptyArray = new Integer[0];
        TopDownMergeSort.sort(emptyArray);
        assertArrayEquals(new Integer[]{}, emptyArray);
    }

    @Test
    void givenEmptyArray_whenMergeSort_thenReturnZeroCompares() {
        Integer[] emptyArray = new Integer[0];
        assertEquals(0, TopDownMergeSort.sort(emptyArray));
    }

    //-----------------------------------
    //Case: Only one element
    //-----------------------------------
    @Test
    void givenSizeOneArray_whenMergeSort_thenIdenticalArray() {
        Integer[] sizeOneArray = new Integer[]{1};
        TopDownMergeSort.sort(sizeOneArray);
        assertArrayEquals(new Integer[]{1}, sizeOneArray);
    }

    @Test
    void givenSizeOneArray_whenMergeSort_thenReturnZeroCompares() {
        Integer[] sizeOneArray = new Integer[]{1};
        assertEquals(0, TopDownMergeSort.sort(sizeOneArray));
    }

    //-----------------------------------
    //Case: Descending order rather than ascending
    //-----------------------------------
    @Test
    void givenDescendingArray_whenMergeSort_thenAscendingArray() {
        Integer[] descendingArray = new Integer[]{2,1,0,-1};
        TopDownMergeSort.sort(descendingArray);
        assertArrayEquals(new Integer[]{-1,0,1,2}, descendingArray);
    }

    @Test
    void givenDescendingArray_whenMergeSort_thenReturnNumberOfCompares() {
        Integer[] descendingArray = new Integer[]{2,1,0,-1};
        assertEquals(4, TopDownMergeSort.sort(descendingArray));
    }

    //-----------------------------------
    //Case: Odd amount of elements
    //-----------------------------------
    @Test
    void givenOddSizeArray_whenMergeSort_thenSortsArray() {
        Integer[] oddSizeArray = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
        TopDownMergeSort.sort(oddSizeArray);
        assertArrayEquals(new Integer[]{0,1,2,3,4,5,6,7,8,9,10}, oddSizeArray);
    }

    @Test
    void givenOddSizeArray_whenMergeSort_thenReturnNumberOfCompares() {
        Integer[] oddSizeArray = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
        assertEquals(29, TopDownMergeSort.sort(oddSizeArray));
    }


    //-----------------------------------
    //Case: all elements equal value (using Testdata tuples with identical values and distinct id's)
    //As such, if not identical would imply that some tuples with equal value were switched (e.g. try value = -i)
    //-----------------------------------
    @Test
    void givenIdenticalElementsArray_whenMergeSort_thenPreserveStability() {
        TestData[] identicalElementsArray = Handler.generate(10, i -> new TestData(i,0));
        TopDownMergeSort.sort(identicalElementsArray);
        assertArrayEquals(Handler.generate(10, i -> new TestData(i,0)), identicalElementsArray);
    }

    @Test
    void givenIdenticalElementsArray_whenMergeSort_thenReturnNumberOfCompares() {
        TestData[] identicalElementsArray = Handler.generate(10, i -> new TestData(i,0));
        assertEquals(19, TopDownMergeSort.sort(identicalElementsArray));
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
        TopDownMergeSort.sort(duplicateElementsArray);
        assertArrayEquals(expectedArray, duplicateElementsArray);
    }

    @Test
    void givenDuplicateElementsArray_whenMergeSort_thenReturnNumberOfCompares() {
        TestData[] duplicateElementsArray = Handler.readData(Handler.streamFile("unittest/duplicateElems.TestData.in"), 
        TestData::from); 
        assertEquals(8, TopDownMergeSort.sort(duplicateElementsArray));
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
        System.out.println("Input Array: " + Arrays.toString(duplicateElementsArray));
        System.out.println("Expected Array: " + Arrays.toString(expectedArray));

        TopDownMergeSort.sort(duplicateElementsArray);

        // Debug: sorted input
        System.out.println("Sorted Array: " + Arrays.toString(duplicateElementsArray));

        assertArrayEquals(expectedArray, duplicateElementsArray);
    }    

    @Test
    void givenDuplicateElementsArrayMedium_whenMergeSort_thenReturnNumberOfCompares() {
        TestData[] duplicateElementsArray = Handler.readData(Handler.streamFile("unittest/duplicateElemsMedium.TestData.in"), 
        TestData::from); 

        assertEquals(23, TopDownMergeSort.sort(duplicateElementsArray));
    }    

}
