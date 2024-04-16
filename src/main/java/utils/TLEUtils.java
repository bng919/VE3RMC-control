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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Utility for parsing a two line element set from the configuration file.
 */
public class TLEUtils {

    /**
     * Private constructor to prevent instantiation. All methods static.
     */
    private TLEUtils() {}

    /**
     * Open a file and parse the TLE from it. File must contain exactly 3 lines with the first being the satellite
     * name and the remaining the two lines of the Keplerian elements in TLE format.
     * @param path Path to the TLE configuration file. (Get this from ConfigurationUtils)
     * @return Array containing the three lines of the file.
     */
    public static String[] fileToStrArray(String path) {
        BufferedReader tleReader;
        String[] tle = new String[3];
        try {
            tleReader = new BufferedReader(new FileReader(path));
            String line = tleReader.readLine();
            int count = 1;
            while (line != null) {
                if (count > 3) {
                    throw new RuntimeException("Too many lines in TLE!");
                }
                if (line.charAt(0) == '1') { // TLE lines start with an id number
                    tle[1] = line;
                } else if (line.charAt(0) == '2') {
                    tle[2] = line;
                } else { // Otherwise, line must be the name of satellite
                    tle[0] = line;
                }
                line = tleReader.readLine();
                count++;
            }
            if (count < 3) {
                throw new RuntimeException("Too few lines in TLE!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return tle;
    }

}
