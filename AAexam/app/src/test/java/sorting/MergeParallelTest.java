package sorting;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import data.Handler;
import data.TestData;
import static sorting.MergeParallel.merge;;

public class MergeParallelTest {


    //-----------------------------------
    //Case: Subarrays of equal size
    //-----------------------------------
    @Test void givenEvenSubarrays_whenMerge_thenMerge() {
        Integer[] a   = new Integer[]{3,4,1,2};
        Integer[] aux = new Integer[4];
        merge(a, aux, 0, 1, 3, 2);
        assertArrayEquals(aux, new Integer[]{1,2,3,4});
    }

    @Test void givenEvenSubarrays_whenMerge_thenReturnNumberOfCompares() {
        Integer[] a   = new Integer[]{3,4,1,2};
        Integer[] aux = new Integer[4];
        assertEquals(2, merge(a, aux, 0, 1, 3, 2));
    }


    //-----------------------------------
    //Case: One of the subarrays are bigger than the other ([1..3] + [4 ..5])
    //-----------------------------------
    @Test void givenUnevenSubarrays_whenMerge_thenMerge() {
        Integer[] a = new Integer[]{7,2,3,5,4,6,5,4,3,2,1};
        Integer[] aux = a.clone();
        Integer[] expected = new Integer[]{7,2,3,4,5,6,5,4,3,2,1};
        merge(a, aux, 1, 3, 5, 2);
        assertArrayEquals(expected, aux);
    }

    @Test void givenUnevenSubarrays_whenMerge_thenReturnNumberOfCompares() {
        Integer[] a = new Integer[]{7,2,3,5,4,6,5,4,3,2,1};
        int compares = merge(a, new Integer[11], 1, 3, 5, 2);
        assertEquals(4, compares);
    }

    //-----------------------------------
    //Case array with duplicate elements in subarrays
    //-----------------------------------
    @Test void givenDuplicateElements_whenMerge_thenMergesStably() {
        TestData[] a = Handler.readData(
            Handler.streamFile("unittest/duplicateInSubarrays.TestData.in"), TestData::from); 
        TestData[] expected = Handler.readData(
            Handler.streamFile("unittest/duplicateInSubarrays.TestData.out"), TestData::from); 
        TestData[] aux =  new TestData[8];
        merge(a, aux, 0, 3, 7, 2);
        assertArrayEquals(expected, aux);
    }

    @Test void givenDuplicateElements_whenMerge_thenReturnNumberOfCompares() {
        TestData[] a = Handler.readData(
            Handler.streamFile("unittest/duplicateInSubarrays.TestData.in"), TestData::from); 
        int compares = merge(a, new TestData[8], 0, 3, 7, 2);
        assertEquals(7, compares);
    }
}
