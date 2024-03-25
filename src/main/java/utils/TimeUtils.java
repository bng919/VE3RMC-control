package utils;

public class TimeUtils {

    private TimeUtils() {}

    public static void delayMillis(long millis) {
        long endTime = System.currentTimeMillis() + millis;
        while (System.currentTimeMillis() < endTime) {}
    }
}
