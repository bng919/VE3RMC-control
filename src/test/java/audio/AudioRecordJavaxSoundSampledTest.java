package audio;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.Log;
import utils.ResultUtils;
import utils.enums.Verbosity;

import static org.testng.Assert.*;

public class AudioRecordJavaxSoundSampledTest {

    @BeforeClass
    public void setup() {
        new Log(".\\logs\\", Verbosity.DEBUG);
    }

    @Test
    public void testStartRecording() {
        int recordDuration = 10;
        AudioRecordJavaxSoundSampled a = new AudioRecordJavaxSoundSampled();
        a.setSampleRate(44100);
        a.setRecordDurationS(recordDuration);
        long startTime = java.time.Instant.now().getEpochSecond();
        ResultUtils rst = a.startRecording();
        long stopTime = java.time.Instant.now().getEpochSecond();
        // Check if actual record time matches requested duration with tolerance to account for overhead
        boolean timeEqual = (stopTime-startTime) >= recordDuration && (stopTime-startTime) <= recordDuration+1;

        assertTrue(rst.isSuccessful());
        assertTrue(timeEqual);
    }
}