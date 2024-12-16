package sorting;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import data.Handler;
import data.TestData;

public class MergeTest {


    //-----------------------------------
    //Case: Subarrays of equal size
    //-----------------------------------
    @Test
    void givenEvenSubarrays_whenMerge_thenMerge() {
        Integer[] evenSubarrays = new Integer[]{3,4,1,2};
        Merge.merge(evenSubarrays,  new Integer[4], 0, 1, 3);
        assertArrayEquals(evenSubarrays, new Integer[]{1,2,3,4});
    }

    @Test
    void givenEvenSubarrays_whenMerge_thenReturnNumberOfCompares() {
        Integer[] evenSubarrays = new Integer[]{3,4,1,2};
        assertEquals(2, Merge.merge(evenSubarrays,  new Integer[4], 0, 1, 3));
    }


    //-----------------------------------
    //Case: One of the subarrays are bigger than the other ([1..3] + [4 ..5])
    //-----------------------------------
    @Test
    void givenUnevenSubarrays_whenMerge_thenMerge() {
        Integer[] unevenSubarrays = new Integer[]{7,2,3,5,4,6,5,4,3,2,1};
        Merge.merge(unevenSubarrays, new Integer[11], 1, 3, 5);
        assertArrayEquals(unevenSubarrays, new Integer[]{7,2,3,4,5,6,5,4,3,2,1});
    }

    @Test
    void givenUnevenSubarrays_whenMerge_thenReturnNumberOfCompares() {
        Integer[] unevenSubarrays = new Integer[]{7,2,3,5,4,6,5,4,3,2,1};
        assertEquals(4, Merge.merge(unevenSubarrays, new Integer[11], 1, 3, 5));
    }

    //-----------------------------------
    //Case array with duplicate elements in subarrays
    //-----------------------------------
    @Test
    void givenDuplicateElements_whenMerge_thenMergesStably() {
        TestData[] duplicateElementsArray = Handler.readData(Handler.streamFile("unittest/duplicateInSubarrays.TestData.in"), 
        TestData::from); 
        TestData[] expectedArray = Handler.readData(Handler.streamFile("unittest/duplicateInSubarrays.TestData.out"), 
        TestData::from); 
        Merge.merge(duplicateElementsArray, new TestData[8], 0, 3, 7);
        assertArrayEquals(expectedArray, duplicateElementsArray);
    }

    @Test
    void givenDuplicateElements_whenMerge_thenReturnNumberOfCompares() {
        TestData[] duplicateElementsArray = Handler.readData(Handler.streamFile("unittest/duplicateInSubarrays.TestData.in"), 
        TestData::from); 
        assertEquals(7, Merge.merge(duplicateElementsArray, new TestData[8], 0, 3, 7));
    }


}
