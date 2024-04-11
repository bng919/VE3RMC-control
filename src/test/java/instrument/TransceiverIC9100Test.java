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

package instrument;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.Log;
import utils.SerialUtils;
import utils.enums.Modulation;
import utils.enums.Verbosity;

import static org.testng.Assert.*;

public class TransceiverIC9100Test {

    SerialUtils serialUtils;

    @BeforeClass
    public void setup() {
        new Log(".\\logs\\", Verbosity.DEBUG);
        //serial = Mockito.mock(Serial.class);
        //byte[] rst = {(byte) 0xfe, (byte) 0xfe, 0x7c, 0x00, 0x03, (byte) 0xfd, (byte) 0xfe, (byte) 0xfe, 0x00, 0x7c, 0x03, 0x01, 0x00, 0x07, 0x45, 0x01, (byte) 0xFD};
        //Mockito.when(serial.read()).thenReturn(rst);
    }

    @Test
    public void testReadWriteInstrument() throws InterruptedException {
        TransceiverIC9100 transceiverIC9100 = new TransceiverIC9100();

        // Switching frequencies and mode on UHF (testcase 1.b.i)
        setReadAssert(transceiverIC9100, 145000000, Modulation.FM);
        setReadAssert(transceiverIC9100, 144000000, Modulation.FM);
        setReadAssert(transceiverIC9100, 145000000, Modulation.AM);
        setReadAssert(transceiverIC9100, 144000000, Modulation.AM);

        //Switch to VHF (testcase 1.b.iv), and test switching frequencies and mode on VHF (testcase 1.b.i)
        setReadAssert(transceiverIC9100, 445000000, Modulation.FM);
        setReadAssert(transceiverIC9100, 444000000, Modulation.FM);
        setReadAssert(transceiverIC9100, 445000000, Modulation.AM);
        setReadAssert(transceiverIC9100, 444000000, Modulation.AM);

        //Invalid frequency test (testcase 1.b.iii)
        setReadAssert(transceiverIC9100, 144000000, Modulation.FM);
        transceiverIC9100.setFrequency(140000000);
        transceiverIC9100.readInstrument();
        assertEquals(transceiverIC9100.getFrequencyHz(), 144000000);

        setReadAssert(transceiverIC9100, 440000000, Modulation.FM);
        transceiverIC9100.setFrequency(419000000);
        transceiverIC9100.readInstrument();
        assertEquals(transceiverIC9100.getFrequencyHz(), 440000000);

        //Single Hz set test (testcase 1.b.v)
        setReadAssert(transceiverIC9100, 144123456, Modulation.FM);

        //Sweep frequency (testcase 1.b.ii)
        long startFreq = 144500000L;
        long stopFreq = 145000000L;
        long stepFreq = 10000L;

        for (long f = startFreq; f <= stopFreq; f+=stepFreq) {
            transceiverIC9100.setFrequency(f);
        }
        transceiverIC9100.readInstrument();
        assertEquals(transceiverIC9100.getFrequencyHz(), stopFreq);
    }

    @AfterClass
    public void cleanUp() throws InterruptedException {
        TransceiverIC9100 transceiverIC9100 = new TransceiverIC9100();
        transceiverIC9100.setFrequency(145000000);
        transceiverIC9100.setModulation(Modulation.FM);
    }

    private void setReadAssert(TransceiverIC9100 transceiverIC9100, long freq, Modulation mode) throws InterruptedException {
        transceiverIC9100.setFrequency(freq);
        transceiverIC9100.setModulation(mode);
        transceiverIC9100.readInstrument();
        assertEquals(transceiverIC9100.getFrequencyHz(), freq);
        assertEquals(transceiverIC9100.getModulation(), mode);
    }

}