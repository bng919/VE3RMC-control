package instrument;

public class InstrumentFactory {
    public static Instrument createInstrument(String instrument) throws InterruptedException {
        if (instrument == null || instrument.isEmpty()) {
            throw new RuntimeException("InstrumentFactory could not create instrument with null or empty string");
        } else if (instrument.equalsIgnoreCase("RotatorGS232B")) {
            return new RotatorGS232B();
        } else if (instrument.equalsIgnoreCase("TransceiverIC9100")) {
            return new TransceiverIC9100();
        } else {
            throw new RuntimeException("InstrumentFactory could not create instrument with ID " + instrument);
        }
    }
}
