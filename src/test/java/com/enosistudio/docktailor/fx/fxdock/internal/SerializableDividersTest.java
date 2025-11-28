package com.enosistudio.docktailor.fx.fxdock.internal;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SerializableDividersTest {

    @Test
    void testToString() {
        // Create using reflection or direct construction isn't possible,
        // so we test the fromString instead
        SerializableDividers sd = SerializableDividers.fromString("DividerData[0.5,0.75]");
        String result = sd.toString();

        assertEquals("DividerData[0.5,0.75]", result);
    }

    @Test
    void testFromStringSingleDivider() {
        SerializableDividers sd = SerializableDividers.fromString("DividerData[0.5]");

        assertEquals(1, sd.getPositions().size());
        assertEquals(0.5, sd.getPositions().getFirst());
    }

    @Test
    void testFromStringMultipleDividers() {
        SerializableDividers sd = SerializableDividers.fromString("DividerData[0.25,0.5,0.75]");

        assertEquals(3, sd.getPositions().size());
        assertEquals(0.25, sd.getPositions().get(0));
        assertEquals(0.5, sd.getPositions().get(1));
        assertEquals(0.75, sd.getPositions().get(2));
    }

    @Test
    void testFromStringEmpty() {
        SerializableDividers sd = SerializableDividers.fromString("DividerData[]");

        assertEquals(0, sd.getPositions().size());
        assertTrue(sd.getPositions().isEmpty());
    }

    @Test
    void testFromStringInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () ->
                SerializableDividers.fromString("invalid")
        );
    }

    @Test
    void testFromStringMissingBracket() {
        assertThrows(IllegalArgumentException.class, () ->
                SerializableDividers.fromString("DividerData[0.5")
        );
    }

    @Test
    void testFromStringMissingPrefix() {
        assertThrows(IllegalArgumentException.class, () ->
                SerializableDividers.fromString("[0.5]")
        );
    }

    @Test
    void testFromStringInvalidNumber() {
        assertThrows(NumberFormatException.class, () ->
                SerializableDividers.fromString("DividerData[abc]")
        );
    }

    @Test
    void testRoundTrip() {
        String original = "DividerData[0.3,0.6,0.9]";
        SerializableDividers sd = SerializableDividers.fromString(original);
        String serialized = sd.toString();

        assertEquals(original, serialized);
    }

    @Test
    void testImmutability() {
        SerializableDividers sd = SerializableDividers.fromString("DividerData[0.5]");
        List<Double> positions = sd.getPositions();

        assertThrows(UnsupportedOperationException.class, () ->
                positions.add(0.75)
        );
    }

    @Test
    void testPositionsPrecision() {
        SerializableDividers sd = SerializableDividers.fromString("DividerData[0.333333,0.666666]");

        assertEquals(0.333333, sd.getPositions().get(0), 0.000001);
        assertEquals(0.666666, sd.getPositions().get(1), 0.000001);
    }

    @Test
    void testNegativePositions() {
        // Although unlikely in practice, test that negative values work
        SerializableDividers sd = SerializableDividers.fromString("DividerData[-0.1,1.1]");

        assertEquals(2, sd.getPositions().size());
        assertEquals(-0.1, sd.getPositions().get(0));
        assertEquals(1.1, sd.getPositions().get(1));
    }

    @Test
    void testZeroPosition() {
        SerializableDividers sd = SerializableDividers.fromString("DividerData[0.0]");

        assertEquals(1, sd.getPositions().size());
        assertEquals(0.0, sd.getPositions().getFirst());
    }

    @Test
    void testOnePosition() {
        SerializableDividers sd = SerializableDividers.fromString("DividerData[1.0]");

        assertEquals(1, sd.getPositions().size());
        assertEquals(1.0, sd.getPositions().getFirst());
    }
}
