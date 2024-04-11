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
import utils.enums.Verbosity;

import static org.testng.Assert.*;

public class LogTest {

    @Test
    public void testLogCreation() {
        Log l = new Log(".\\logs\\", Verbosity.DEBUG);

        for (Verbosity v : Verbosity.values()) {
            Log.verbose = v;
            Log.debug("Debug message");
            Log.info("Info message");
            Log.warn("Warn message");
            Log.error("Error message");
        }
    }
}