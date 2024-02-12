package instrument;

import utils.ResultHelper;
import utils.enums.Modulation;

public interface Transceiver extends Instrument {

    long getFrequencyHz();

    Modulation getModulation();

    ResultHelper setFrequency(long freqHz) throws InterruptedException;

    ResultHelper setModulation(Modulation m) throws InterruptedException;

}
