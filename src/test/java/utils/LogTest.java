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