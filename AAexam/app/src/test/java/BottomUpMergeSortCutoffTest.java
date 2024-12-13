import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import data.Handler;
import data.TestData;
import sorting.BottomUpMergeSort;
import sorting.BottomUpMergeSortCutoff;
import sorting.InsertionSort;

public class BottomUpMergeSortCutoffTest {
    

    //-----------------------------------
    //Case: Invalid cutoff value
    //-----------------------------------
    @Test
    void givenInvalidCutoffvalue_whenMergeSortCutoff_thenThrowsException() {
        Exception e = assertThrows(IllegalArgumentException.class, 
        () -> BottomUpMergeSortCutoff.sort(new Integer[]{1,2,3},-2));
        assertEquals("Cutoff value must be at least 1.", e.getMessage());
    }



     //-----------------------------------
    //Case: Descending order rather than ascending
    //Also: just one run in stack after initial computation of runs
    //------------------------------------
    @Test
    void givenDescendingArray_whenMergeSortCutoff_thenAscendingArray() {
        Integer[] descendingArray = new Integer[]{2,1,0,-1};
        BottomUpMergeSortCutoff.sort(descendingArray, 2);
        assertArrayEquals(new Integer[]{-1,0,1,2}, descendingArray);
    }

    @Test
    void givenDescendingArray_whenMergeSortCutoff_ReturnNumberOfCompares() {
        Integer[] descendingArray = new Integer[]{2,1,0,-1};
        assertEquals(4, BottomUpMergeSortCutoff.sort(descendingArray, 2));
    }

    //-----------------------------------
    //Case: Odd amount of elements
    //-----------------------------------
    @Test
    void givenOddSizeArray_whenMergeSortCutoff_thenSortsArray() {
        Integer[] oddSizeArray = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
        BottomUpMergeSortCutoff.sort(oddSizeArray, 3);
        assertArrayEquals(new Integer[]{0,1,2,3,4,5,6,7,8,9,10}, oddSizeArray);
    }

      //(Uses c=1, which yields same number of compares than from no cutoff (29))  
    @Test
    void givenOddSizeArray_whenMergeSortCutoff1_thenReturnSameComparesAsNoCutoff() {
        Integer[] oddSizeArray = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
        Integer[] oddSizeArray2 = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
        assertEquals(BottomUpMergeSort.sort(oddSizeArray), BottomUpMergeSortCutoff.sort(oddSizeArray2,1));
    }

        //case: different cutoff-values give different comparrison numbers
    @Test
    void givenOddSizeArray_whenMergeSortCutoff3_thenReturnSameComparesAsNoCutoff() {
        Integer[] oddSizeArray = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
        assertEquals(29, BottomUpMergeSortCutoff.sort(oddSizeArray,3));
    }
        
    @Test
    void givenOddSizeArray_whenMergeSortCutoff5_thenReturnSameComparesAsNoCutoff() {
        Integer[] oddSizeArray = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
        assertEquals(33, BottomUpMergeSortCutoff.sort(oddSizeArray,5));
    }


    //-----------------------------------
    //Case: c greater or equal to array length
    //-----------------------------------

    @Test
    void givenArray_whenMergeSortCutoffExceedingValue_thenSortsArray() {
        Integer[] mediumArray = new Integer[]{-5,2,2,7,6,4,5,1,2,45,7,88,-23,54};
        BottomUpMergeSortCutoff.sort(mediumArray, 50);
        assertArrayEquals(new Integer[]{-23,-5,1,2,2,2,4,5,6,7,7,45,54,88}, mediumArray);
    }

    @Test
    void givenArray_whenMergeSortCutoffExceedingValue_thenReturnNumberOfComparesEqualToInsertionSort() {
        Integer[] mediumArray = new Integer[]{-5,2,2,7,6,4,5,1,2,45,7,88,-23,54};
        Integer[] mediumArray2 = new Integer[]{-5,2,2,7,6,4,5,1,2,45,7,88,-23,54};
        assertEquals(InsertionSort.sort(mediumArray), BottomUpMergeSortCutoff.sort(mediumArray2, 50));
    }


    //-----------------------------------
    //Case: Seven elements
    //-----------------------------------

    @Test
    void givenOddSize7Array_whenMergeSortCutoff_thenSortsArray() {
        Integer[] oddSizeArray = new Integer[]{7,6,5,4,3,2,1};
        BottomUpMergeSortCutoff.sort(oddSizeArray, 2);
        assertArrayEquals(new Integer[]{1,2,3,4,5,6,7}, oddSizeArray);
    }

    @Test
    void givenOddSize7Array_whenMergeSortCutoff3_thenReturnNumberOfCompares() {
        Integer[] oddSizeArray = new Integer[]{7,6,5,4,3,2,1};
        assertEquals(10, BottomUpMergeSortCutoff.sort(oddSizeArray, 3));
    }


    //-----------------------------------
    //Case: all elements equal value (using Testdata tuples with identical values and distinct id's)
    //As such, if not identical would imply that some tuples with equal value were switched (e.g. try value = -i)
    //-----------------------------------
    @Test
    void givenIdenticalElementsArray_whenMergeSortCutoff_thenPreserveStability() {
        TestData[] identicalElementsArray = Handler.generate(10, i -> new TestData(i,0));
        BottomUpMergeSortCutoff.sort(identicalElementsArray,4);
        assertArrayEquals(Handler.generate(10, i -> new TestData(i,0)), identicalElementsArray);
    }

    @Test
    void givenIdenticalElementsArray_whenMergeSortCutoff4_thenReturnNumberOfCompares() {
        TestData[] identicalElementsArray = Handler.generate(10, i -> new TestData(i,0));
        assertEquals(19, BottomUpMergeSortCutoff.sort(identicalElementsArray,4));
    }

    //-----------------------------------
    //Case: small array contains duplicates (for easier readability compared to medium array)
    //-----------------------------------
    @Test
    void givenDuplicateElementsArray_whenMergeSortCutoff_thenSortsArrayStably() {
        TestData[] duplicateElementsArray = Handler.readData(Handler.streamFile("unittest/duplicateElems.TestData.in"), 
        TestData::from); 
        TestData[] expectedArray = Handler.readData(Handler.streamFile("unittest/duplicateElems.TestData.out"), 
        TestData::from); 
        BottomUpMergeSortCutoff.sort(duplicateElementsArray,2);
        assertArrayEquals(expectedArray, duplicateElementsArray);
    }

    @Test
    void givenDuplicateElementsArray_whenMergeSortCutoff3_thenReturnNumberOfCompares() {
        TestData[] duplicateElementsArray = Handler.readData(Handler.streamFile("unittest/duplicateElems.TestData.in"), 
        TestData::from); 
        assertEquals(7, BottomUpMergeSortCutoff.sort(duplicateElementsArray,3));
    }

    //-----------------------------------
    //Case: medium array contains duplicates
    //-----------------------------------
    //TODO: Important! When one of the files are modified, it doesn't seem to be updated in the read array (a cache seems to be used) (Maybe okay and because it has to be built also?)
    //changing name and back can solve this ish
    @Test
    void givenDuplicateElementsArrayMedium_whenMergeSortCutoff_thenSortsArrayStably() {
        TestData[] duplicateElementsArray = Handler.readData(Handler.streamFile("unittest/duplicateElemsMedium.TestData.in"), 
        TestData::from); 
        TestData[] expectedArray = Handler.readData(Handler.streamFile("unittest/duplicateElemsMedium.TestData.out"), 
        TestData::from); 
        BottomUpMergeSortCutoff.sort(duplicateElementsArray, 4);
        assertArrayEquals(expectedArray, duplicateElementsArray);
    }    

    @Test
    void givenDuplicateElementsArrayMedium_whenMergeSortCutoff_thenReturnNumberOfCompares() {
        TestData[] duplicateElementsArray = Handler.readData(Handler.streamFile("unittest/duplicateElemsMedium.TestData.in"), 
        TestData::from); 
        assertEquals(23, BottomUpMergeSortCutoff.sort(duplicateElementsArray, 3));
    }    

}
