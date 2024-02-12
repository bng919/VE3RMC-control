package instrument;

import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.Serial;

import static org.testng.Assert.*;

public class InstrumentFactoryTest {

    @BeforeClass
    public void setup() {
        Serial serial = Mockito.mock(Serial.class);
        Mockito.when(serial.open()).thenReturn(true);
        Mockito.when(serial.write(Mockito.any(byte[].class))).thenReturn(true);
        Mockito.when(serial.close()).thenReturn(true);
        Mockito.when(serial.read()).thenReturn(null);
    }

    @Test
    public void testCreateInstrumentRotator() {
        try {
            Instrument rst = InstrumentFactory.createInstrument("RotatorGS232B");
            assertTrue(rst instanceof Rotator);
            assertTrue(rst instanceof RotatorGS232B);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testCreateInstrumentTransceiver() {
        try {
            Instrument rst = InstrumentFactory.createInstrument("TransceiverIC9100");
            assertTrue(rst instanceof Transceiver);
            assertTrue(rst instanceof TransceiverIC9100);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testCreateInstrumentEmptyString() {
        assertThrows(RuntimeException.class, () -> {
            InstrumentFactory.createInstrument("");
        });
    }

    @Test
    public void testCreateInstrumentNullString() {
        assertThrows(RuntimeException.class, () -> {
            InstrumentFactory.createInstrument(null);
        });
    }

    @Test
    public void testCreateInstrumentInvalidString() {
        assertThrows(RuntimeException.class, () -> {
            InstrumentFactory.createInstrument("InvalidID");
        });
    }
}