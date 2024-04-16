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

/**
 * Utility for parsing the config.properties file.
 */
public class ConfigurationUtils {

    private static final String configPath = "./config/config.properties";
    private static final Properties config = new Properties();

    // Class initializer. Reads the configuration file as soon as program begins.
    static {
        FileReader configFile;
        try {
            configFile = new FileReader(configPath);
            config.load(configFile);
        } catch (IOException e) {
            System.out.println("Cannot open config.properties file!! Terminating.");
            throw new RuntimeException(e);
        }
    }

    /**
     * Private constructor to prevent instantiation. All methods static.
     */
    private ConfigurationUtils() {}

    /**
     * Retrieve a String type property.
     * @param key Name of property.
     * @return Value of the property.
     */
    public static String getStrProperty(String key) {
        return config.getProperty(key);
    }

    /**
     * Retrieve an int type property.
     * @param key Name of property.
     * @return Value of the property.
     */
    public static int getIntProperty(String key) {
        return Integer.parseInt(config.getProperty(key));
    }

    /**
     * Retrieve a double type property.
     * @param key Name of property.
     * @return Value of the property.
     */
    public static double getDoubleProperty(String key) {
        return Double.parseDouble(config.getProperty(key));
    }

    /**
     * Retrieve a byte type property.
     * @param key Name of property.
     * @return Value of the property.
     */
    public static byte getByteProperty(String key) {
        return HexFormat.of().parseHex(config.getProperty(key))[0];
    }

    /**
     * Get the relative path to the config file.
     * @return Config file path.
     */
    public static String getConfigPath() {
        return configPath;
    }

}
