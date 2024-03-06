package audio;

import utils.Log;
import utils.ResultHelper;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class AudioRecordWin implements AudioRecord {

    private int sampleRate;

    private int recordDurationS;

    public AudioRecordWin() {

    }

    public void run() {
        Log.debug("Running audio recorder in thread " + Thread.currentThread().threadId());
        startRecording();
    }

    public ResultHelper startRecording() {
        try {
            Log.info("Starting audio recording service.");
            Log.debug("Setting up audio recorder with sample rate " + sampleRate);
            AudioFormat format = new AudioFormat(sampleRate, 16, 2, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                Log.error("Error setting up audio recording.");
                throw new LineUnavailableException(
                        "The system does not support the specified format.");
            }

            TargetDataLine targetLine = AudioSystem.getTargetDataLine(format);
            targetLine.open(format);
            targetLine.start();

            byte[] buffer = new byte[4096];
            int bytesRead = 0;

            ByteArrayOutputStream recordBytes = new ByteArrayOutputStream();

            Log.info("Audio recording started. Will record for " + recordDurationS + "s.");
            long endTime = java.time.Instant.now().getEpochSecond() + recordDurationS;
            while (java.time.Instant.now().getEpochSecond() < endTime) {
                bytesRead = targetLine.read(buffer, 0, buffer.length);
                recordBytes.write(buffer, 0, bytesRead);
            }
            Log.info("Audio recording complete.");

            byte[] audioData = recordBytes.toByteArray();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
            AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream, format, audioData.length / format.getFrameSize());

            //TODO: Audio shouldn't be saved here as it breaks modularity. Should pass the stream back to main to be saved there.
            File writeFile = Log.getNextAudioFile();
            Log.info("Saving audio to " + writeFile.getAbsolutePath());
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, writeFile);

            audioInputStream.close();
            recordBytes.close();

        } catch (LineUnavailableException | IOException e) {
            Log.error(e.getClass() + "\n" + e.getMessage());
            return ResultHelper.createFailedResult();
        }
        return ResultHelper.createSuccessfulResult();
    }

    public void setRecordDurationS(int recordDurationS) {
        this.recordDurationS = recordDurationS;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

}
