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

    /** Generates a TestData object from a plain text description.
     * @param string must contain first the ID as an integer, then an integer value.
     * @throws IllegalArgumentException if the string does not contain exactly two tokens.
     * @throws NumberFormatException if either of the tokens are not readable as integers.
     */
    public static TestData from(String string) {
        String[] split = string.split(" ");
        if (!(split.length == 2)) throw new IllegalArgumentException("Wrong string content");
        return new TestData(Integer.parseInt(split[0]), Integer.parseInt(split[1])); }


}
