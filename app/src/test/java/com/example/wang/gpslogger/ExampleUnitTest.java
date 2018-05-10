package com.example.wang.gpslogger;

import org.junit.Test;

import java.text.DecimalFormat;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testList() {

        float distance = 11222f;

        Float distanceInt = new Float(distance) / 1000;
        if (distance >= 9999) {
            distance = 0;
            distanceInt = 0f;
        }
        if (distance < 10) {
            String format1 = new DecimalFormat("#0.000").format(distanceInt);
            System.out.println(format1);
        } else if (distance > 10 && distance < 100) {
            String format1 = new DecimalFormat("#0.00").format(distanceInt);
            System.out.println(format1);
        } else if (distance >= 100 && distance < 1000) {
            String format1 = new DecimalFormat("#0.01").format(distanceInt);
            System.out.println(format1);
        } else if (distance >= 1000) {
            String format1 = new DecimalFormat("#").format(distanceInt);
            System.out.println(format1);
        }
    }
}