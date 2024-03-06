package instrument;

import utils.Log;
import utils.ResultHelper;
import utils.Serial;

import java.util.concurrent.TimeUnit;

public class StubRotatorGS232B implements Rotator {

    private int currAz;
    private int currEl;


    public StubRotatorGS232B() {
        Log.debug("Created StubRotatorGS232B");
        currAz = 0;
        currEl = 0;
        readInstrument();
    }

    public ResultHelper readInstrument() {
        Log.debug("Read from StubRotatorGS232B. Az " + this.currAz + ", El " + this.currEl);
        return ResultHelper.createSuccessfulResult();
    }

    public ResultHelper testConnect() {
        Log.debug("Test connect from StubRotatorGS232B");
        return ResultHelper.createSuccessfulResult();
    }

    public int getAz() {
        return currAz;
    }

    public int getEl() {
        return currEl;
    }

    public ResultHelper goToAz(int az) {
        Log.debug("Set Az to " + az + " from StubRotatorGS232B");
        this.currAz = az;
        return ResultHelper.createSuccessfulResult();
    }

    public ResultHelper goToEl(int el) {
        Log.debug("Set El to " + el + " from StubRotatorGS232B");
        this.currEl = el;
        return ResultHelper.createSuccessfulResult();
    }

    public ResultHelper goToAzEl(int az, int el) {
        ResultHelper azRst = goToAz(az);
        ResultHelper elRst = goToEl(el);
        return azRst.and(elRst);
    }
}
