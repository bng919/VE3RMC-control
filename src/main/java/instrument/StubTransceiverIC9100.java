package instrument;

import utils.Log;
import utils.ResultHelper;
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

    public ResultHelper readInstrument() {
        Log.debug("Read from StubTransceiverIC9100. Freq " + this.freqHz + ", Mod " + this.modSetting);
        return ResultHelper.createSuccessfulResult();
    }

    public ResultHelper testConnect() {
        Log.debug("Test connect from StubRotatorGS232B");
        return ResultHelper.createSuccessfulResult();
    }

    public long getFrequencyHz() {
        return this.freqHz;
    }

    public Modulation getModulation() {
        return this.modSetting;
    }

    public ResultHelper setFrequency(long freqHz) {
        Log.debug("Set freq to " + freqHz);
        this.freqHz = freqHz;
        return ResultHelper.createSuccessfulResult();
    }

    public ResultHelper setModulation(Modulation m) {
        Log.debug("Set modulation to " + m);
        this.modSetting = m;
        return ResultHelper.createSuccessfulResult();
    }

}
