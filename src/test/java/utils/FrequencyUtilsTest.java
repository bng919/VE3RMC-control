package utils;

import org.testng.Assert;
import org.testng.annotations.Test;

public class FrequencyUtilsTest {

    @Test
    public void testIsFreqUHF() {
        Assert.assertFalse(FrequencyUtils.isUHF(142000000L));
        Assert.assertTrue(FrequencyUtils.isUHF(144000000L));
        Assert.assertTrue(FrequencyUtils.isUHF(145000000L));
        Assert.assertTrue(FrequencyUtils.isUHF(147990000L));
        Assert.assertFalse(FrequencyUtils.isUHF(148000000L));
    }

    @Test
    public void testIsFreqVHF() {
        Assert.assertFalse(FrequencyUtils.isVHF(430000000L));
        Assert.assertTrue(FrequencyUtils.isVHF(430025000L));
        Assert.assertTrue(FrequencyUtils.isVHF(445000000L));
        Assert.assertTrue(FrequencyUtils.isVHF(450000000L));
        Assert.assertFalse(FrequencyUtils.isVHF(452000000L));
    }
}