package instrument;

import utils.Log;
import utils.ResultUtils;
import utils.enums.Modulation;

public class StubTransceiverIC9100 implements Transceiver{

    private long freqHz;
    private Modulation modSetting;

    public StubTransceiverIC9100(){
        Log.debug("Created StubTransceiverIC9100");
        freqHz = 0;
        modSetting = Modulation.FM;
        readInstrument();
    }

    public ResultUtils readInstrument() {
        Log.debug("Read from StubTransceiverIC9100. Freq " + this.freqHz + ", Mod " + this.modSetting);
        return ResultUtils.createSuccessfulResult();
    }

    public ResultUtils testConnect() {
        Log.debug("Test connect from StubRotatorGS232B");
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

    public ResultUtils setModulation(Modulation m) {
        Log.debug("Set modulation to " + m);
        this.modSetting = m;
        return ResultUtils.createSuccessfulResult();
    }

}
