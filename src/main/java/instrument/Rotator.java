package instrument;

import utils.ResultHelper;

public interface Rotator extends Instrument {

    int getAz();

    int getEl();

    ResultHelper goToAz(int az) throws InterruptedException;

    ResultHelper goToEl(int el) throws InterruptedException;

    ResultHelper goToAzEl(int az, int el) throws InterruptedException;

}
