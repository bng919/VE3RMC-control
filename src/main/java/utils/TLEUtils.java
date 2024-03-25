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
