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

import java.io.FileReader;
import java.io.IOException;
import java.util.HexFormat;
import java.util.Properties;

public class ConfigurationUtils {

    private static final Properties config = new Properties();

    static {
        FileReader configFile;
        try {
            configFile = new FileReader("config.properties");
            config.load(configFile);
        } catch (IOException e) {
            System.out.println("Cannot open config.properties file!! Terminating.");
            throw new RuntimeException(e);
        }
    }

    private ConfigurationUtils() {}

    public static String getStrProperty(String key) {
        return config.getProperty(key);
    }

    public static int getIntProperty(String key) {
        return Integer.parseInt(config.getProperty(key));
    }

    public static byte getByteProperty(String key) {
        return HexFormat.of().parseHex(config.getProperty(key))[0];
    }

}
