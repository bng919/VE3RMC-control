package instrument;

import utils.ResultUtils;

public interface Instrument {

    //ResultHelper connect();

    //ResultHelper disconnect();

    ResultUtils readInstrument() throws InterruptedException;

    ResultUtils testConnect() throws InterruptedException;

}
