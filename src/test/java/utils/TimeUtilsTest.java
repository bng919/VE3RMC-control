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