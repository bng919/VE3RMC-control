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
import utils.enums.Modulation;

/**
 * A stubbed transceiver class that does not communicate with a physical instrument.
 * Used for testing other portions of code while the instrument is not connected, but does NOT mock
 * the responses of the instrument.
 */
public class StubTransceiver implements Transceiver{

    private long freqHz;
    private Modulation modSetting;

    /**
     * Instate this class via the {@link InstrumentFactory} only.
     */
    protected StubTransceiver(){
        Log.debug("Created StubTransceiver");
        freqHz = 0;
        modSetting = Modulation.FM;
        readInstrument();
    }

    public ResultUtils readInstrument() {
        Log.debug("Read from StubTransceiver. Freq " + this.freqHz + ", Mod " + this.modSetting);
        return ResultUtils.createSuccessfulResult();
    }

    public ResultUtils testConnect() {
        Log.debug("Test connect from StubRotator");
        return ResultUtils.createSuccessfulResult();
    }

    public long getFrequencyHz() {
        return this.freqHz;
    }

    public Modulation getModulation() {
        return this.modSetting;
    }

    public ResultUtils setFrequency(long freqHz) {
        Log.debug("Set freq to " + freqHz);
        this.freqHz = freqHz;
        return ResultUtils.createSuccessfulResult();
    }

    public ResultUtils setModulation(Modulation mod) {
        Log.debug("Set modulation to " + mod);
        this.modSetting = mod;
        return ResultUtils.createSuccessfulResult();
    }

}
