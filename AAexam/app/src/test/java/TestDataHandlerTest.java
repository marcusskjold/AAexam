import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * DataGeneratorTest
 */
public class TestDataHandlerTest {
    List<TestData> actual, expected;
    
    @BeforeEach void reset() { 
        actual   = null; 
        expected = null; 
    }
    
    @Test void seqGen_generatesCorrectAmountOfTestData() {
        actual = TestDataHandler.seqGen(0, 18_000_000);
        assertEquals(18_000_000, actual.size());
    }

    @Test void seqGen_generatesSortedTestData() {
        actual = TestDataHandler.seqGen(-10, 16_000_000);
        expected = new ArrayList<>(actual);
        Collections.sort(expected);
        assertEquals(expected, actual);
    }

    @Test void randomize_randomizes() {
        expected = TestDataHandler.seqGen(-1000, 16_000_000);
        actual =  TestDataHandler.randomizeData(expected);
        assertNotEquals(expected, actual);
    }

    /** This also implicitly tests all other methods */
    @Test void fileIO_works() {
        expected = TestDataHandler.seqGen(-2, 40);
        TestDataHandler.writeToFile("test.data", expected);
        actual = TestDataHandler.readFile("test.data");
        assertEquals(expected, actual);
    }

}
