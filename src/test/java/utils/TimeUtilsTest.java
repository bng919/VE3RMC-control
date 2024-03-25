package utils;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class TimeUtilsTest {

    @Test
    public void testDelayMillis() {
        int tolerance = 2;
        int len = 1000;
        long start = System.currentTimeMillis();
        TimeUtils.delayMillis(len);
        long end = System.currentTimeMillis();

        assertFalse(start+len < end-tolerance || start+len > end+tolerance);
    }
}