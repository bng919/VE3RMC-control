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
