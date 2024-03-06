package decode;

import java.util.List;

public interface Decoder extends Runnable {

    void startDecoder();
    List<byte[]> getDecodedData();
    void setDireWolfDir(String direWolfDir);
    void setDurationS(int durationS);

}
