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

public class TLEUtils {

    private TLEUtils() {}

    public static String[] fileToStrArray(String path) {
        BufferedReader tleReader;
        String[] tle = new String[3];
        try {
            tleReader = new BufferedReader(new FileReader(path));
            String line = tleReader.readLine();
            int count = 1;
            while (line != null) {
                if (count > 3) {
                    throw new RuntimeException("Too many lines in tle");
                }
                if (line.charAt(0) == '1') {
                    tle[1] = line;
                } else if (line.charAt(0) == '2') {
                    tle[2] = line;
                } else {
                    tle[0] = line;
                }
                line = tleReader.readLine();
                count++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return tle;
    }
}
