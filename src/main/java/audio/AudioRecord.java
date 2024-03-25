package audio;

import utils.ResultUtils;

public interface AudioRecord extends Runnable {

    ResultUtils startRecording();

    //ResultHelper stopRecording();
    void setRecordDurationS(int recordDurationS);

    void setSampleRate(int sampleRate);

}
