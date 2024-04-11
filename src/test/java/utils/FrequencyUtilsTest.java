/*
 * Copyright (C) 2024  Benjamin Graham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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