package sorting;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


import org.junit.jupiter.api.Test;

import data.Handler;
import data.TestData;

public class LevelSortAdaptiveTest {


    //-----------------------------------
    //Case: Invalid cutoff value
    //-----------------------------------
    @Test
    void givenInvalidCutoffvalue_whenLevelSortAdaptive_thenThrowsException() {
        Exception e = assertThrows(IllegalArgumentException.class, 
        () -> LevelSortAdaptive.sort(new Integer[]{1,2,3},-2));
        assertEquals("Cutoff value must be at least 1.", e.getMessage());
    }

    //-----------------------------------
    //Case: Empty array
    //-----------------------------------
    @Test
    void givenEmptyArray_LevelSortAdaptive_thenEmptyArray() {
        Integer[] emptyArray = new Integer[0];
        LevelSortAdaptive.sort(emptyArray,1);
        assertArrayEquals(new Integer[]{}, emptyArray);
    }

    @Test
    void givenEmptyArray_LevelSortAdaptive_thenReturnNumberOfCompares() {
        Integer[] emptyArray = new Integer[0];
        assertEquals(0, LevelSortAdaptive.sort(emptyArray,1));
    }

    //-----------------------------------
    //Case: Only one element
    //-----------------------------------
    @Test
    void givenSizeOneArray_whenLevelSortAdaptive_thenIdenticalArray() {
        Integer[] sizeOneArray = new Integer[]{1};
        LevelSortAdaptive.sort(sizeOneArray,1);
        assertArrayEquals(new Integer[]{1}, sizeOneArray);
    }

    @Test
    void givenSizeOneArray_whenLevelSortAdaptive_thenReturnNumberOfCompares() {
        Integer[] sizeOneArray = new Integer[]{1};
        assertEquals(0, LevelSortAdaptive.sort(sizeOneArray,1));
    }

    //-----------------------------------
    //Case: Input is just one run (when reversed), and c < array length
    //  Here we expect it to just explore the run and then finishing up
    //------------------------------------
    @Test
    void givenOneRunArray_LevelSortAdaptiveLesserc_thenAscendingArray() {
        Integer[] descendingArray = new Integer[]{2,1,0,-1};
        LevelSortAdaptive.sort(descendingArray, 2);
        assertArrayEquals(new Integer[]{-1,0,1,2}, descendingArray);
    }

    @Test
    void givenOneRunArray_LevelSortAdaptiveLesserc_thenReturnNumberOfCompares() {
        Integer[] oneRunArray = new Integer[]{2,1,0,-1};
        int expectedCompares = oneRunArray.length -1;
        assertEquals(expectedCompares, LevelSortAdaptive.sort(oneRunArray, 2));
    }

    //-----------------------------------
    //Case: Input is just one run (when reversed), and c >= array length
    //  Here we expect it to explore the run (and reverse it), then using Insertion sort on it
    //------------------------------------
    @Test
    void givenOneRunArray_LevelSortAdaptiveGreaterc_thenAscendingArray() {
        Integer[] oneRunArray = new Integer[]{2,1,0,-1};
        LevelSortAdaptive.sort(oneRunArray, 10);
        assertArrayEquals(new Integer[]{-1,0,1,2}, oneRunArray);
    }

    @Test
    void givenOneRunArray_LevelSortAdaptiveGreaterc_thenReturnNumberOfCompares() {
        Integer[] oneRunArray = new Integer[]{2,1,0,-1};
        int expectedCompares = oneRunArray.length -1 + InsertionSort.sort(new Integer[]{-1,0,1,2});
        assertEquals(expectedCompares, LevelSortAdaptive.sort(oneRunArray, 10));
    }

    //-----------------------------------
    //Case: All runs in array are strictly increasing and of length <= c
    //  Here we expect the algorithm to behave like the non-adaptive variant
    //  with the addition of exploring run lengths (additional compares)
    //-----------------------------------
    @Test
    void givenSmallIncreasingRunsArray_whenLevelSortAdaptive_thenSortsArray() {
        Integer[] smallIncreasingRunsArray = new Integer[]{0,2,4,3,3,3,0,1,1,0,10};
        LevelSortAdaptive.sort(smallIncreasingRunsArray, 3);
        assertArrayEquals(new Integer[]{0,0,0,1,1,2,3,3,3,4,10}, smallIncreasingRunsArray);
    }

    @Test
    void givenSmallIncreasingRunsArray_whenLevelSortAdaptivec3_thenReturnNumberOfCompares() {
        Integer[] smallIncreasingRunsArray = new Integer[]{0,2,4,3,3,3,0,1,1,0,10};
        int exploringCost = (3 + 3 + 3 + 2) - 1;
        int expectedCost = LevelSort.sort(smallIncreasingRunsArray.clone(), 3) + exploringCost;
        assertEquals(expectedCost, LevelSortAdaptive.sort(smallIncreasingRunsArray, 3));
    }


    //-----------------------------------
    //Case: All runs in array are of length > c
    //  Here we expect the algorithm to use runs longer than c 
    //  rather than insertion-sorting in those cases:
    //-----------------------------------

    @Test
    void givenVariousRunsArray_whenLevelSortAdaptive_thenSortsArray() {
        Integer[] variousRunsArray = new Integer[]{9,2,1,0,7,4,3,8,5,6,10};
        LevelSortAdaptive.sort(variousRunsArray, 2);
        assertArrayEquals(new Integer[]{0,1,2,3,4,5,6,7,8,9,10}, variousRunsArray);
    }

    @Test
    void givenVariousRunsArray_whenLevelSortAdaptive_thenReturnNumberOfCompares() {
        Integer[] variousRunsArray = new Integer[]{9,2,1,0,7,4,3,8,5,6,10};
        assertEquals(31, LevelSortAdaptive.sort(variousRunsArray, 2));
    }


//--------------------------
//Extra tests that just shows equality with Non-adaptible levelsort
//--------------------------


    //@Test
    //void giveVariousRunsArray_whenLevelSortAdaptivec4_thenReturnNumberOfCompares() {
    //    Integer[] variousRunsArray = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
    //    assertEquals(29, LevelSortAdaptive.sort(variousRunsArray, 4));
    //}


    //-----------------------------------
    //Case: Highest possible level relative to stack-size computed (Five elements with c= 1) 
    // n + (n-1) yields higher level than (n - 1) + (n - 2)
    //Array built different here than in non-adaptive version, 
    //but showcase that this bound is still relevant
    //-----------------------------------

    @Test
    void givenSize5Array_whenLevelSortAdaptivec1_thenSortsArray() {
        Integer[] size5Array = new Integer[]{4,3,4,1,4};
        LevelSortAdaptive.sort(size5Array, 2);
        assertArrayEquals(new Integer[]{1,3,4,4,4}, size5Array);
    }

    //-----------------------------------
    //Case: Two runs in stack after initial iteration.
    //In initial iteration, both a merge going to end of stack
    //and a merge stopped by run with larger level
    //-----------------------------------
    @Test
    void givenSize7Array_whenLevelSortAdaptivec1_thenSortsArray() {
        Integer[] size7Array = new Integer[]{7,6,5,4,3,2,1};
        LevelSortAdaptive.sort(size7Array, 1);
        assertArrayEquals(new Integer[]{1,2,3,4,5,6,7}, size7Array);
    }

    //@Test
    //void givenSize7Array_whenLevelSortAdaptivec1_thenReturnNumberOfCompares() {
    //    Integer[] size7Array = new Integer[]{7,6,5,4,3,2,1};
    //    assertEquals(9, LevelSortAdaptive.sort(size7Array, 1));
    //}

    //-----------------------------------
    //Case: One run in stack after initial iteration
    //-----------------------------------
    @Test
    void givenSize12Array_whenLevelSortAdaptivec3_thenSortsArray() {
        Integer[] size12Array = new Integer[]{4,3,2,1,1,2,3,4,4,3,2,1};
        LevelSortAdaptive.sort(size12Array, 3);
        assertArrayEquals(new Integer[]{1,1,1,2,2,2,3,3,3,4,4,4}, size12Array);
    }

    //-----------------------------------
    //Case: No merges during initial iteration
    //stack will just contain two runs of lvl 3 and 4, respectively
    //-----------------------------------
    @Test
    void givenSize9Array_whenLevelSortAdaptivec3_thenSortsArray() {
        Integer[] size9Array = new Integer[]{4,3,2,1,1,2,3,4,4};
        LevelSortAdaptive.sort(size9Array, 3);
        assertArrayEquals(new Integer[]{1,1,2,2,3,3,4,4,4}, size9Array);
    }

    //@Test
    //void givenSize9Array_whenLevelSortAdaptivec3_thenReturnNumberOfCompares() {
    //    Integer[] size9Array = new Integer[]{4,3,2,1,1,2,3,4,4};
    //    assertEquals(17, LevelSortAdaptive.sort(size9Array, 3));
    //}


    //-----------------------------------
    //Case: all elements equal value (using Testdata tuples with identical values and distinct id's)
    //As such, if not identical would imply that some tuples with equal value were switched (e.g. try value = -i)
    //-----------------------------------
    @Test
    void givenIdenticalElementsArray_whenLevelSortAdaptive_thenPreserveStability() {
        TestData[] identicalElementsArray = Handler.generate(10, i -> new TestData(i,0));
        LevelSortAdaptive.sort(identicalElementsArray,4);
        assertArrayEquals(Handler.generate(10, i -> new TestData(i,0)), identicalElementsArray);
    }

    //@Test
    //void givenIdenticalElementsArray_whenLevelSortAdaptive_thenReturnNumberOfCompares() {
    //    TestData[] identicalElementsArray = Handler.generate(10, i -> new TestData(i,0));
    //    assertEquals(19, LevelSortAdaptive.sort(identicalElementsArray,4));
    //}

    //-----------------------------------
    //Case: small array contains duplicates (for easier readability compared to medium array)
    //-----------------------------------
    @Test
    void givenDuplicateElementsArray_whenLevelSortAdaptive_thenSortsArrayStably() {
        TestData[] duplicateElementsArray = Handler.readData(Handler.streamFile("unittest/duplicateElems.TestData.in"), 
        TestData::from); 
        TestData[] expectedArray = Handler.readData(Handler.streamFile("unittest/duplicateElems.TestData.out"), 
        TestData::from); 
        LevelSortAdaptive.sort(duplicateElementsArray,2);
        assertArrayEquals(expectedArray, duplicateElementsArray);
    }

    //@Test
    //void givenDuplicateElementsArray_whenLevelSortAdaptive_thenReturnNumberOfCompares() {
    //    TestData[] duplicateElementsArray = Handler.readData(Handler.streamFile("unittest/duplicateElems.TestData.in"), 
    //    TestData::from); 
    //    assertEquals(9, LevelSortAdaptive.sort(duplicateElementsArray,2));
    //}


    //-----------------------------------
    //Case: medium array contains duplicates
    //-----------------------------------
    //TODO: Important! When one of the files are modified, it doesn't seem to be updated in the read array (a cache seems to be used) (Maybe okay and because it has to be built also?)
    //changing name and back can solve this ish
    @Test
    void givenDuplicateElementsArrayMedium_whenLevelSortAdaptive_thenSortsArrayStably() {
        TestData[] duplicateElementsArray = Handler.readData(Handler.streamFile("unittest/duplicateElemsMedium.TestData.in"), 
        TestData::from); 
        TestData[] expectedArray = Handler.readData(Handler.streamFile("unittest/duplicateElemsMedium.TestData.out"), 
        TestData::from); 
        LevelSortAdaptive.sort(duplicateElementsArray, 1);
        assertArrayEquals(expectedArray, duplicateElementsArray);
    }  

    //@Test
    //void givenDuplicateElementsArrayMedium_whenLevelSortAdaptive_thenReturnNumberOfCompares() {
    //    TestData[] duplicateElementsArray = Handler.readData(Handler.streamFile("unittest/duplicateElemsMedium.TestData.in"), 
    //    TestData::from); 
    //    assertEquals(27, LevelSortAdaptive.sort(duplicateElementsArray, 3));
    //} 
}
