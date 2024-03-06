package sattrack;

public class SatTrackFactory {

    private SatTrackFactory() {}

    public static SatTrack createSatTrack(String satTrack) {
        if (satTrack == null || satTrack.isEmpty()) {
            throw new RuntimeException("SatTrackFactory could not create instrument with null or empty string");
        } else if (satTrack.equalsIgnoreCase("Predict4Java")) {
            return new SatTrackPredict4Java();
        } else {
            throw new RuntimeException("SatTrackFactory could not create instrument with ID " + satTrack);
        }
    }
}
