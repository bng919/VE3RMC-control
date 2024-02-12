package instrument;

import utils.ResultHelper;

public interface Instrument {

    //ResultHelper connect();

    //ResultHelper disconnect();

    void readInstrument() throws InterruptedException;

    ResultHelper testConnect();

}
