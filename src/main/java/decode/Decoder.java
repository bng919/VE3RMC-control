package decode;

import java.util.List;

public interface Decoder extends Runnable {

    void startDecoder();
    List<byte[]> getDecodedData();
    void setDireWolfDir(String direWolfDir); //TODO: This breaks modularity. This cannot be in Decoder.
    void setDurationS(int durationS);

}
