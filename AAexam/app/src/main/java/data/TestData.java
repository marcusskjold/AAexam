package data;

/**
 * TestData
 * A simple data structure to test sorting algorithms.
 * TestData compares on the value field.
 * Sorting algorithms can be checked for stability property using the id field.
 */
public record TestData(int id, int value) implements Comparable<TestData> { 

    /** Compares on the basis of the {@code value} field only.
     * Is not consistent with equals or hashcode.
     * This is to make it testable if sorting algorithms are stable.
     */
    @Override public int compareTo(TestData t) {
        return Integer.compare(this.value, t.value);
    }

    @Override public String toString() {
        return id + " " + value;
    }

}
