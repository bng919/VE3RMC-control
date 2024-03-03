package utils;

import org.testng.Assert;
import org.testng.annotations.Test;

public class FreqHelperTest {

    @Test
    public void testIsFreqUHF() {
        Assert.assertFalse(FreqHelper.isUHF(142000000L));
        Assert.assertTrue(FreqHelper.isUHF(144000000L));
        Assert.assertTrue(FreqHelper.isUHF(145000000L));
        Assert.assertTrue(FreqHelper.isUHF(147990000L));
        Assert.assertFalse(FreqHelper.isUHF(148000000L));
    }

    @Test
    public void testIsFreqVHF() {
        Assert.assertFalse(FreqHelper.isVHF(430000000L));
        Assert.assertTrue(FreqHelper.isVHF(430025000L));
        Assert.assertTrue(FreqHelper.isVHF(445000000L));
        Assert.assertTrue(FreqHelper.isVHF(450000000L));
        Assert.assertFalse(FreqHelper.isVHF(452000000L));
    }
}