import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import data.Handler;
import data.TestData;
import sorting.BottomUpMergeSort;
import sorting.BottomUpMergeSortCutoff;
import sorting.TopDownMergeSort;
import sorting.TopDownMergeSortCutoff;

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


    //-----------------------------------
    //Case: Seven elements (first three bits set in stack after initial merging of runs)
    //-----------------------------------

    @Test
    void givenOddSize7Array_whenMergeSortCutoff_thenSortsArray() {
        Integer[] oddSizeArray = new Integer[]{7,6,5,4,3,2,1};
        BottomUpMergeSortCutoff.sort(oddSizeArray, 2);
        assertArrayEquals(new Integer[]{1,2,3,4,5,6,7}, oddSizeArray);
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

}
