package audio;

import utils.ResultHelper;

public interface AudioRecord extends Runnable {

    ResultHelper startRecording();

    //ResultHelper stopRecording();


}
