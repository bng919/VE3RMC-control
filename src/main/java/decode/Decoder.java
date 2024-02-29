package decode;

import java.util.List;

public interface Decoder {

    public void startDecoder(int durationS);

    public List<byte[]> getDecodedData();

}
