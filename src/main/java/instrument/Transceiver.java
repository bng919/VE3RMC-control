package instrument;

import utils.ResultUtils;
import utils.enums.Modulation;

public interface Transceiver extends Instrument {

    long getFrequencyHz();

    Modulation getModulation();

    ResultUtils setFrequency(long freqHz) throws InterruptedException;

    ResultUtils setModulation(Modulation m) throws InterruptedException;

}
