import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

import sorting.BottomUpMergeSort;
import sorting.TopDownMergeSort;

public class BottomUpMergeSortTest {
    
     //-----------------------------------
    //Case: Descending order rather than ascending
    //------------------------------------
    @Test
    void givenDescendingArray_whenMergeSort_thenAscendingArray() {
        Integer[] descendingArray = new Integer[]{2,1,0,-1};
        BottomUpMergeSort.sort(descendingArray);
        assertArrayEquals(new Integer[]{-1,0,1,2}, descendingArray);
    }


    //-----------------------------------
    //Case: Odd amount of elements
    //-----------------------------------
    @Test
    void givenOddSizeArray_whenMergeSort_thenSortsArray() {
        Integer[] oddSizeArray = new Integer[]{9,1,2,0,7,4,3,8,5,6,10};
        BottomUpMergeSort.sort(oddSizeArray);
        assertArrayEquals(new Integer[]{0,1,2,3,4,5,6,7,8,9,10}, oddSizeArray);
    }


}
