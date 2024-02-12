package utils;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class TimeTest {

    @Test
    public void testDelayMillis() {
        int tolerance = 2;
        int len = 1000;
        long start = System.currentTimeMillis();
        Time.delayMillis(len);
        long end = System.currentTimeMillis();

        assertFalse(start+len < end-tolerance || start+len > end+tolerance);
    }
}