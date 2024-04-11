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

package audio;

import utils.Log;
import utils.ResultUtils;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class AudioRecordJavaxSoundSampled implements AudioRecord {

    private int sampleRate;
    private int recordDurationS;

    public AudioRecordJavaxSoundSampled() {

    }

    public void run() {
        Log.debug("Running audio recorder in thread " + Thread.currentThread().threadId());
        startRecording();
    }

    public ResultUtils startRecording() {
        try {
            Log.info("Starting audio recording service.");
            Log.debug("Setting up audio recorder with sample rate " + sampleRate);
            AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, true);
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
            return ResultUtils.createFailedResult();
        }
        return ResultUtils.createSuccessfulResult();
    }

    public void setRecordDurationS(int recordDurationS) {
        this.recordDurationS = recordDurationS;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

}
