package instrument;

import utils.ResultUtils;

public interface Rotator extends Instrument {

    int getAz();

    int getEl();

    ResultUtils goToAz(int az) throws InterruptedException;

    ResultUtils goToEl(int el) throws InterruptedException;

    ResultUtils goToAzEl(int az, int el) throws InterruptedException;

}
