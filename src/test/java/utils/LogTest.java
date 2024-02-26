package utils;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class LogTest {

    @Test
    public void testLogCreation() {
        Log log = new Log(".\\logs\\");
        log.info("Info message");
        log.warn("Warn message");
        log.error("Error message");

    }
}