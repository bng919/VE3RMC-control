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

import java.io.UnsupportedEncodingException;

public class HexadecimalUtils {

    private HexadecimalUtils() {}

    public static String hexDump(byte[] d) {
        StringBuilder b = new StringBuilder();
        int nCols = 16;
        b.append("       ");
        for (int i = 0 ; i < nCols; i++) { // Print top address
            b.append(String.format("%02X ", i));
        }
        b.append("\n\n");

        for (int i = 0; i < d.length; i += nCols) { // Print rows of nCols bytes
            b.append(String.format("%04X:  ", i)); // Left address

            for (int j = 0; j < nCols; j++) { // Print each byte in row
                if (i+j < d.length) {
                    b.append(String.format("%02X ", d[i+j]));
                } else {
                    b.append("   ");
                }
            }

            if (i < d.length) {
                int asciiWidth = Math.min(nCols, d.length - i);
                b.append("  |  ");
                try {
                    b.append(new String(d, i, asciiWidth, "ASCII")
                            .replaceAll("\\R+", " ")); // Replace any newline
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
            b.append("\n");
        }
        return b.toString();
    }


}
