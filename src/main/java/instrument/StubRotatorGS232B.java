/*
 * Copyright (C) 2024  Benjamin Graham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
