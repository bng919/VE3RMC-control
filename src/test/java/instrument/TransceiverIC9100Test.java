package instrument;

import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.Serial;
import utils.enums.Modulation;

import static org.testng.Assert.*;

public class TransceiverIC9100Test {

    Serial serial;

    @BeforeClass
    public void setup() {
        serial = Mockito.mock(Serial.class);
        byte[] rst = {(byte) 0xfe, (byte) 0xfe, 0x7c, 0x00, 0x03, (byte) 0xfd, (byte) 0xfe, (byte) 0xfe, 0x00, 0x7c, 0x03, 0x01, 0x00, 0x07, 0x45, 0x01, (byte) 0xFD};
        Mockito.when(serial.read()).thenReturn(rst);
    }

    @Test
    public void testReadInstrument() throws InterruptedException {

        TransceiverIC9100 tran = new TransceiverIC9100();
        tran.readInstrument();

        assertEquals(tran.getFrequencyHz(), 145000000L);
        assertEquals(tran.getModulation(), Modulation.FM);
    }

    @Test
    public void testSetFrequency() {
    }

    @Test
    public void testSetModulation() {
    }
}