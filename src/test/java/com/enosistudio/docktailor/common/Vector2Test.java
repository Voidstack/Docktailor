package com.enosistudio.docktailor.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Vector2Test {

    @Test
    void testConstructor() {
        Vector2 v = new Vector2(3.0, 4.0);
        assertEquals(3.0, v.getX());
        assertEquals(4.0, v.getY());
    }

    @Test
    void testConstants() {
        assertEquals(0.0, Vector2.ZERO.getX());
        assertEquals(0.0, Vector2.ZERO.getY());

        assertEquals(1.0, Vector2.ONE.getX());
        assertEquals(1.0, Vector2.ONE.getY());
    }

    @Test
    void testSetters() {
        Vector2 v = new Vector2(0, 0);
        v.setX(5.0);
        v.setY(10.0);

        assertEquals(5.0, v.getX());
        assertEquals(10.0, v.getY());
    }

    @Test
    void testSetMethod() {
        Vector2 v = new Vector2(0, 0);
        v.set(7.0, 9.0);

        assertEquals(7.0, v.getX());
        assertEquals(9.0, v.getY());
    }

    @Test
    void testCalculDistance() {
        Vector2 v1 = new Vector2(0, 0);
        Vector2 v2 = new Vector2(3, 4);

        double distance = v1.calculDistance(v2);
        assertEquals(5.0, distance, 0.0001); // 3-4-5 triangle
    }

    @Test
    void testCalculDistanceSamePoint() {
        Vector2 v1 = new Vector2(5, 5);
        Vector2 v2 = new Vector2(5, 5);

        assertEquals(0.0, v1.calculDistance(v2), 0.0001);
    }

    @Test
    void testCalculDistanceNegative() {
        Vector2 v1 = new Vector2(-3, -4);
        Vector2 v2 = new Vector2(0, 0);

        assertEquals(5.0, v1.calculDistance(v2), 0.0001);
    }

    @Test
    void testCalculMilieu() {
        Vector2 v1 = new Vector2(0, 0);
        Vector2 v2 = new Vector2(10, 20);

        Vector2 mid = v1.calculMilieu(v2);
        assertEquals(5.0, mid.getX());
        assertEquals(10.0, mid.getY());
    }

    @Test
    void testCalculMilieuSamePoint() {
        Vector2 v1 = new Vector2(7, 9);
        Vector2 v2 = new Vector2(7, 9);

        Vector2 mid = v1.calculMilieu(v2);
        assertEquals(7.0, mid.getX());
        assertEquals(9.0, mid.getY());
    }

    @Test
    void testCalculMilieuNegative() {
        Vector2 v1 = new Vector2(-10, -20);
        Vector2 v2 = new Vector2(10, 20);

        Vector2 mid = v1.calculMilieu(v2);
        assertEquals(0.0, mid.getX());
        assertEquals(0.0, mid.getY());
    }

    @Test
    void testToString() {
        Vector2 v = new Vector2(3.5, 7.2);
        String str = v.toString();

        assertTrue(str.contains("3.5"));
        assertTrue(str.contains("7.2"));
        assertTrue(str.contains("x:"));
        assertTrue(str.contains("y:"));
    }

    @Test
    void testDistanceCommutative() {
        Vector2 v1 = new Vector2(1, 2);
        Vector2 v2 = new Vector2(4, 6);

        double d1 = v1.calculDistance(v2);
        double d2 = v2.calculDistance(v1);

        assertEquals(d1, d2, 0.0001);
    }

    @Test
    void testMilieuCommutative() {
        Vector2 v1 = new Vector2(2, 3);
        Vector2 v2 = new Vector2(8, 11);

        Vector2 mid1 = v1.calculMilieu(v2);
        Vector2 mid2 = v2.calculMilieu(v1);

        assertEquals(mid1.getX(), mid2.getX(), 0.0001);
        assertEquals(mid1.getY(), mid2.getY(), 0.0001);
    }
}
