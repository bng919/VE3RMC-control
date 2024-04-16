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

package integration;

import audio.AudioRecordJavaxSoundSampled;
import decode.DecoderDireWolf;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.Log;
import utils.enums.Verbosity;

import java.util.List;

public class RecordDecodeTest {

    @BeforeClass
    public void setup() {
        new Log(".\\logs\\", Verbosity.DEBUG);
    }

    @Test
    public void testRecordDecode() {
        /*
         * This test case does not assert a result. It should be run manually to verify that the dire wolf decoder
         * and audio recording portions of the program are working. Update the decoder path and duration accordingly
         * before use.
         */
        AudioRecordJavaxSoundSampled audio = new AudioRecordJavaxSoundSampled();
        audio.setSampleRate(48000);
        audio.setRecordDurationS(20);
        DecoderDireWolf dec = new DecoderDireWolf();
        dec.setDurationS(20);
        dec.setDecoderPath("C:\\direwolf-1.7.0-9807304_i686");

        Thread audioThread = new Thread(audio);
        Thread decoderThread = new Thread(dec);

        audioThread.start();
        decoderThread.start();
        try {
            audioThread.join();
            decoderThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        List<byte[]> data = dec.getDecodedData();
        for (byte[] d : data) {
            Log.storeDecodedData(d);
        }

    }

}
