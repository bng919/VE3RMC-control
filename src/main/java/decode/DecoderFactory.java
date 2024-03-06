package decode;

public class DecoderFactory {

    private DecoderFactory() {}

    public static Decoder createDecoder(String decoder) {
        if (decoder == null || decoder.isEmpty()) {
            throw new RuntimeException("DecoderFactory could not create instrument with null or empty string");
        } else if (decoder.equalsIgnoreCase("DireWolf")) {
            return new DecoderDireWolf();
        } else {
            throw new RuntimeException("DecoderFactory could not create instrument with ID " + decoder);
        }
    }
}
