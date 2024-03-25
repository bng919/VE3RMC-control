package instrument;

import utils.Log;
import utils.ResultUtils;

public class StubRotatorGS232B implements Rotator {

    private int currAz;
    private int currEl;


    public StubRotatorGS232B() {
        Log.debug("Created StubRotatorGS232B");
        currAz = 0;
        currEl = 0;
        readInstrument();
    }

    public ResultUtils readInstrument() {
        Log.debug("Read from StubRotatorGS232B. Az " + this.currAz + ", El " + this.currEl);
        return ResultUtils.createSuccessfulResult();
    }

    public ResultUtils testConnect() {
        Log.debug("Test connect from StubRotatorGS232B");
        return ResultUtils.createSuccessfulResult();
    }

    public int getAz() {
        return currAz;
    }

    public int getEl() {
        return currEl;
    }

    public ResultUtils goToAz(int az) {
        Log.debug("Set Az to " + az + " from StubRotatorGS232B");
        this.currAz = az;
        return ResultUtils.createSuccessfulResult();
    }

    public ResultUtils goToEl(int el) {
        Log.debug("Set El to " + el + " from StubRotatorGS232B");
        this.currEl = el;
        return ResultUtils.createSuccessfulResult();
    }

    public ResultUtils goToAzEl(int az, int el) {
        ResultUtils azRst = goToAz(az);
        ResultUtils elRst = goToEl(el);
        return azRst.and(elRst);
    }
}
