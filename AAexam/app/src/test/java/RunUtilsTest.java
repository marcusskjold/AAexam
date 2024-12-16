import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import data.Handler;
import data.TestData;
import sorting.RunUtils;

public class RunUtilsTest {
    
    //Case: Arrays of size 1
    @Test
    void givenSize1Array_whenExploreRun_thenReturnLastIndex() {
        Integer[] sizeOneArray = new Integer[]{1};
        assertEquals(0, RunUtils.exploreRun(sizeOneArray, 0));
    }

    //Case: run is of size 2:
    @Test
    void givenSizeTwoIncreasingSequence_whenExploreRun_thenReturnNumberOfCompares() {
        Integer[] sizeTwoArray = new Integer[]{1,2};
        assertEquals(1, RunUtils.exploreRun(sizeTwoArray, 0));
    }

    @Test
    void givenSizeTwoDecreasingSequence_whenExploreRun_thenReturnNumberOfCompares() {
        Integer[] sizeTwoArray = new Integer[]{2,1};
        assertEquals(1, RunUtils.exploreRun(sizeTwoArray, 0));
    }

    @Test
    void givenSizeTwoDecreasingSequence_whenExploreRun_thenReversesArray() {
        Integer[] sizeTwoArray = new Integer[]{2,1};
        RunUtils.exploreRun(sizeTwoArray, 0);
        assertArrayEquals(new Integer[]{1,2}, sizeTwoArray);
    }

    //Case: run is in the middle of the array
    @Test
    void givenWeaklyIncreasingSeq_whenExploreRun_thenReturnLastIndex() {
        Integer[] weaklyIncreasingSeq = new Integer[]{10,1,2,2,3,7,11,2,15};
        assertEquals(6, RunUtils.exploreRun(weaklyIncreasingSeq, 1));
    }

    @Test
    void givenStrictlyDecreasingSeq_whenExploreRun_thenReturnLastIndex() {
        Integer[] strictlyDecreasingSeq = new Integer[]{5,10,9,8,5,4,4,10};
        assertEquals(5, RunUtils.exploreRun(strictlyDecreasingSeq, 1));
    }

    @Test
    void givenOddStrictlyDecreasingSeq_whenExploreRun_thenReverseRun() {
        Integer[] strictlyDecreasingSeq = new Integer[]{5,10,9,8,5,4,4,10};
        RunUtils.exploreRun(strictlyDecreasingSeq, 1);
        assertArrayEquals(new Integer[]{5,4,5,8,9,10,4,10}, strictlyDecreasingSeq);
    }

    @Test
    void givenEvenStrictlyDecreasingSeq_whenExploreRun_thenReverseRun() {
        Integer[] strictlyDecreasingSeq = new Integer[]{5,10,9,8,5,4,4,10};
        RunUtils.exploreRun(strictlyDecreasingSeq, 2);
        assertArrayEquals(new Integer[]{5,10,4,5,8,9,4,10}, strictlyDecreasingSeq);
    }

    //Case exploring a run that goes from/to the bounds of the array
    
    @Test
    void givenWeaklyIncreasingSequenceAtBounds_whenExploreRun_thenReturnLastIndex() {
        Integer[] weaklyIncreasingSeq = new Integer[]{1,2,4,4,7};
        assertEquals(4, RunUtils.exploreRun(weaklyIncreasingSeq, 0));
    }

    @Test
    void givenStrictlyDecreasingSequenceAtBounds_whenExploreRun_thenReturnLastIndex() {
        Integer[] strictlyDecreasingSeq = new Integer[]{10,9,8,5,4};
        assertEquals(4, RunUtils.exploreRun(strictlyDecreasingSeq, 0));
    }

    @Test
    void givenStrictlyDecreasingSequenceAtBounds_whenExploreRun_thenReverseRun() {
        Integer[] strictlyDecreasingSeq = new Integer[]{10,9,8,5,4};
        RunUtils.exploreRun(strictlyDecreasingSeq, 0);
        assertArrayEquals(new Integer[]{4,5,8,9,10}, strictlyDecreasingSeq);
    }

    //Case: Run is size 2
    @Test
    void givenWeaklyIncreasingSize2Sequence_whenExploreRun_thenReturnLastIndex() {
        Integer[] weaklyIncreasingSize2Seq = new Integer[]{1,2,4,-6,7};
        assertEquals(2, RunUtils.exploreRun(weaklyIncreasingSize2Seq, 1));
    }

    @Test
    void givenStrictlyDecreasingSize2Sequence_whenExploreRun_thenReturnLastIndex() {
        Integer[] strictlyDecreasingSize2Seq = new Integer[]{10,9,8,8,4};
        assertEquals(2, RunUtils.exploreRun(strictlyDecreasingSize2Seq, 1));
    }

    @Test
    void givenStrictlyDecreasingSize2Sequence_whenExploreRun_thenReverseRun() {
        Integer[] strictlyDecreasingSize2Seq = new Integer[]{10,9,8,8,4};
        RunUtils.exploreRun(strictlyDecreasingSize2Seq, 1);
        assertArrayEquals(new Integer[]{10,8,9,8,4}, strictlyDecreasingSize2Seq);
    }

    //Case: starting at end of array
    @Test
    void givenWeaklyIncreasingSequence_whenExploreRunFromEnd_thenReturnLastIndex() {
        Integer[] weaklyIncreasingSeq = new Integer[]{1,2,4,4,7};
        assertEquals(4, RunUtils.exploreRun(weaklyIncreasingSeq, 4));
    }

    @Test
    void givenStrictlyDecreasingSequenceAtBounds_whenExploreRunFromEnd_thenReturnLastIndex() {
        Integer[] strictlyDecreasingSeq = new Integer[]{10,9,8,5,4};
        assertEquals(4, RunUtils.exploreRun(strictlyDecreasingSeq, 4));
    }

    @Test
    void givenStrictlyDecreasingSequenceAtBounds_whenExploreRunFromEnd_thenReturnSamePositions() {
        Integer[] strictlyDecreasingSeq = new Integer[]{10,9,8,5,4};
        RunUtils.exploreRun(strictlyDecreasingSeq, 4);
        assertArrayEquals(new Integer[]{10,9,8,5,4}, strictlyDecreasingSeq);
    }

    //Case: array of identical elements (we don't want any elements to be reversed):
    @Test
    void givenSizeOneIncreasingSequence_whenExploreRun_thenReturnsLastIndex() {
        TestData[] identicalElementsArray = Handler.generate(5, i -> new TestData(i,0));
        assertEquals(identicalElementsArray.length -1, RunUtils.exploreRun(identicalElementsArray, 1));
    }

    @Test
    void givenSizeOneIncreasingSequence_whenExploreRun_thenReversesNothing() {
        TestData[] identicalElementsArray = Handler.generate(5, i -> new TestData(i,0));
        RunUtils.exploreRun(identicalElementsArray, 1);
        assertArrayEquals(Handler.generate(5, i -> new TestData(i,0)), identicalElementsArray);
    }

    

}
