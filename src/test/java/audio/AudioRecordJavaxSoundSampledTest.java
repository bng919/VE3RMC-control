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