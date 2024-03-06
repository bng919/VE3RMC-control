package instrument;

import utils.ResultHelper;

public interface Instrument {

    //ResultHelper connect();

    //ResultHelper disconnect();

    ResultHelper readInstrument() throws InterruptedException;

    ResultHelper testConnect() throws InterruptedException;

}
