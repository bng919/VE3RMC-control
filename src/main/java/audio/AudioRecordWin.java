package audio;

import utils.ResultHelper;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class AudioRecordWin implements AudioRecord {

    private int sampleRate = 44100;
    private int recordLenS = 60;
    private String tmpFileName = "tmp.wav";


    public ResultHelper startRecording(int recordDurationS) {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 2, true, true);

            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                throw new LineUnavailableException(
                        "The system does not support the specified format.");
            }

            TargetDataLine targetLine = AudioSystem.getTargetDataLine(format);

            targetLine.open(format);

            targetLine.start();

            byte[] buffer = new byte[4096];
            int bytesRead = 0;

            ByteArrayOutputStream recordBytes = new ByteArrayOutputStream();

            long endTime = java.time.Instant.now().getEpochSecond() + recordDurationS;

            while (java.time.Instant.now().getEpochSecond() < endTime) {
                bytesRead = targetLine.read(buffer, 0, buffer.length);
                recordBytes.write(buffer, 0, bytesRead);
            }

            byte[] audioData = recordBytes.toByteArray();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
            AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream, format, audioData.length / format.getFrameSize());

            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File("test.wav"));

            audioInputStream.close();
            recordBytes.close();


        } catch (LineUnavailableException | IOException e) {
            return ResultHelper.createFailedResult();
        }
        return ResultHelper.createSuccessfulResult();
    }

}
