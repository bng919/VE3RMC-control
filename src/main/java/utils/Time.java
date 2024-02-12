package utils;

public class Time {

    public static void delayMillis(long millis) {
        long endTime = System.currentTimeMillis() + millis;
        while (System.currentTimeMillis() < endTime) {}
    }
}
