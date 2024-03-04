package instrument;

public class InstrumentFactory {

    public static Rotator createRotator(String rotator) throws InterruptedException {
        if (rotator == null || rotator.isEmpty()) {
            throw new RuntimeException("InstrumentFactory could not create instrument with null or empty string");
        } else if (rotator.equalsIgnoreCase("RotatorGS232B")) {
            return new RotatorGS232B();
        } else {
            throw new RuntimeException("InstrumentFactory could not create instrument with ID " + rotator);
        }
    }
    public static Transceiver createTransceiver(String transceiver) throws InterruptedException {
        if (transceiver == null || transceiver.isEmpty()) {
            throw new RuntimeException("InstrumentFactory could not create instrument with null or empty string");
        } else if (transceiver.equalsIgnoreCase("TransceiverIC9100")) {
            return new TransceiverIC9100();
        } else {
            throw new RuntimeException("InstrumentFactory could not create instrument with ID " + transceiver);
        }
    }
}
