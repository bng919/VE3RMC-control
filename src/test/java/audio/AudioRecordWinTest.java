package audio;

import org.testng.annotations.Test;
import utils.ResultHelper;

import static org.testng.Assert.*;

public class AudioRecordWinTest {

    @Test
    public void testStartRecording() {
        int recordDuration = 10;
        AudioRecordWin a = new AudioRecordWin();
        long startTime = java.time.Instant.now().getEpochSecond();
        ResultHelper rst = a.startRecording(recordDuration);
        long stopTime = java.time.Instant.now().getEpochSecond();
        // Check if actual record time matches requested duration with tolerance to account for overhead
        boolean timeEqual = (stopTime-startTime) >= recordDuration && (stopTime-startTime) <= recordDuration+1;

        assertTrue(rst.isSuccessful());
        assertTrue(timeEqual);
    }
}