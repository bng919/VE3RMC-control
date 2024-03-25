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
