package decode;

import java.util.List;

public interface Decoder {

    public void startDecoder(long duration);

    public List<byte[]> getDecodedData();

}
