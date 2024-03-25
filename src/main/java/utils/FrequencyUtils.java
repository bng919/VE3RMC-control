package utils;

public class FrequencyUtils {

    private FrequencyUtils() {}

    public static boolean isUHF(long freq) {
        return freq >= 144*MHzToHz && freq <= 147.990*MHzToHz; // See RAC 2m band plan
    }

    public static boolean isVHF(long freq) {
        return freq >= 430.025*MHzToHz && freq <= 450*MHzToHz; // See RAC 70cm band plan
    }

    public static final double kHzToHz = 1e3;
    public static final double HzTokHz = 1e-3;
    public static final double MHzToHz = 1e6;
    public static final double HzToMHz = 1e-6;

}
