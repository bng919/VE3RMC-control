package decode;

import java.util.List;

public interface Decoder extends Runnable {

    public void startDecoder();

    public List<byte[]> getDecodedData();

}
