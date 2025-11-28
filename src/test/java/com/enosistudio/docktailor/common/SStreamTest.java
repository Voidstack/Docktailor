package com.enosistudio.docktailor.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SStreamTest {

    private SStream stream;

    @BeforeEach
    void setUp() {
        stream = new SStream();
    }

    @Test
    void testEmptyStream() {
        assertEquals(0, stream.size());
        assertNull(stream.nextString());
    }

    @Test
    void testAddString() {
        stream.add("test");
        stream.add("value");

        assertEquals(2, stream.size());
        assertEquals("test", stream.getValue(0));
        assertEquals("value", stream.getValue(1));
    }

    @Test
    void testAddNull() {
        stream.add((Object) null);
        assertEquals(1, stream.size());
        assertNull(stream.getValue(0));
    }

    @Test
    void testAddDouble() {
        stream.add(3.14);
        stream.add(42.0);
        stream.add(5.5);

        assertEquals(3, stream.size());
        assertEquals("3.14", stream.getValue(0));
        assertEquals("42", stream.getValue(1)); // Should be integer format
        assertEquals("5.5", stream.getValue(2));
    }

    @Test
    void testAddAllDoubles() {
        double[] values = {1.0, 2.5, 3.0};
        stream.addAll(values);

        assertEquals(3, stream.size());
        assertEquals("1", stream.getValue(0));
        assertEquals("2.5", stream.getValue(1));
        assertEquals("3", stream.getValue(2));
    }

    @Test
    void testNextString() {
        stream.add("first");
        stream.add("second");
        stream.add("third");

        assertEquals("first", stream.nextString());
        assertEquals("second", stream.nextString());
        assertEquals("third", stream.nextString());
        assertNull(stream.nextString()); // Beyond end
    }

    @Test
    void testNextStringWithDefault() {
        stream.add("value");

        assertEquals("value", stream.nextString("default"));
        assertEquals("default", stream.nextString("default")); // Beyond end
    }

    @Test
    void testNextDouble() {
        stream.add("3.14");
        stream.add("42");
        stream.add("invalid");

        assertEquals(3.14, stream.nextDouble(0.0), 0.001);
        assertEquals(42.0, stream.nextDouble(0.0), 0.001);
        assertEquals(-1.0, stream.nextDouble()); // Default is -1.0
    }

    @Test
    void testNextInt() {
        stream.add("42");
        stream.add("100");
        stream.add("invalid");

        assertEquals(42, stream.nextInt(0));
        assertEquals(100, stream.nextInt(0));
        assertEquals(-1, stream.nextInt()); // Default is -1
    }

    @Test
    void testToArray() {
        stream.add("a");
        stream.add("b");
        stream.add("c");

        String[] array = stream.toArray();
        assertArrayEquals(new String[]{"a", "b", "c"}, array);
    }

    @Test
    void testConstructorWithArray() {
        String[] input = {"x", "y", "z"};
        SStream newStream = new SStream(input);

        assertEquals(3, newStream.size());
        assertEquals("x", newStream.getValue(0));
        assertEquals("y", newStream.getValue(1));
        assertEquals("z", newStream.getValue(2));
    }

    @Test
    void testConstructorWithNullArray() {
        SStream newStream = new SStream(null);
        assertEquals(0, newStream.size());
    }

    @Test
    void testIterator() {
        stream.add("1");
        stream.add("2");
        stream.add("3");

        int count = 0;
        for (String s : stream) {
            assertNotNull(s);
            count++;
        }
        assertEquals(3, count);
    }

    @Test
    void testToString() {
        stream.add("a");
        stream.add("b");

        String result = stream.toString();
        assertTrue(result.contains("a"));
        assertTrue(result.contains("b"));
    }
}
