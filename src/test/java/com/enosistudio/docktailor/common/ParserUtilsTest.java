package com.enosistudio.docktailor.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserUtilsTest {

    @Test
    void testParseDoubleFromNumber() {
        assertEquals(42.5, ParserUtils.parseDouble(42.5));
        assertEquals(100.0, ParserUtils.parseDouble(100));
    }

    @Test
    void testParseDoubleFromString() {
        assertEquals(3.14, ParserUtils.parseDouble("3.14"));
        assertEquals(42.0, ParserUtils.parseDouble("42"));
        assertEquals(-5.5, ParserUtils.parseDouble("-5.5"));
    }

    @Test
    void testParseDoubleWithWhitespace() {
        assertEquals(10.0, ParserUtils.parseDouble("  10.0  "));
    }

    @Test
    void testParseDoubleInvalid() {
        assertNull(ParserUtils.parseDouble("invalid"));
        assertNull(ParserUtils.parseDouble("abc123"));
    }

    @Test
    void testParseDoubleNull() {
        assertNull(ParserUtils.parseDouble(null));
    }

    @Test
    void testParseDoubleWithDefault() {
        assertEquals(3.14, ParserUtils.parseDouble("3.14", 0.0));
        assertEquals(99.9, ParserUtils.parseDouble("invalid", 99.9));
        assertEquals(-1.0, ParserUtils.parseDouble(null, -1.0));
    }

    @Test
    void testParseIntFromNumber() {
        assertEquals(42, ParserUtils.parseInt(42, 0));
        assertEquals(100, ParserUtils.parseInt(100.7, 0));
    }

    @Test
    void testParseIntFromString() {
        assertEquals(123, ParserUtils.parseInt("123", 0));
        assertEquals(-50, ParserUtils.parseInt("-50", 0));
    }

    @Test
    void testParseIntWithWhitespace() {
        assertEquals(42, ParserUtils.parseInt("  42  ", 0));
    }

    @Test
    void testParseIntInvalid() {
        assertEquals(999, ParserUtils.parseInt("invalid", 999));
        assertEquals(-1, ParserUtils.parseInt("abc", -1));
    }

    @Test
    void testParseIntNull() {
        assertEquals(100, ParserUtils.parseInt(null, 100));
    }

    @Test
    void testParseBooleanFromBoolean() {
        assertTrue(ParserUtils.parseBoolean(true));
        assertFalse(ParserUtils.parseBoolean(false));
    }

    @Test
    void testParseBooleanFromString() {
        assertTrue(ParserUtils.parseBoolean("true"));
        assertTrue(ParserUtils.parseBoolean("TRUE"));
        assertTrue(ParserUtils.parseBoolean("True"));
        assertTrue(ParserUtils.parseBoolean("y"));
        assertTrue(ParserUtils.parseBoolean("Y"));
        assertTrue(ParserUtils.parseBoolean("1"));
    }

    @Test
    void testParseBooleanFalse() {
        assertFalse(ParserUtils.parseBoolean("false"));
        assertFalse(ParserUtils.parseBoolean("FALSE"));
        assertFalse(ParserUtils.parseBoolean("0"));
        assertFalse(ParserUtils.parseBoolean("no"));
        assertFalse(ParserUtils.parseBoolean("random"));
    }

    @Test
    void testParseBooleanNull() {
        assertFalse(ParserUtils.parseBoolean(null));
    }

    @Test
    void testParseBooleanEmptyString() {
        assertFalse(ParserUtils.parseBoolean(""));
    }
}
