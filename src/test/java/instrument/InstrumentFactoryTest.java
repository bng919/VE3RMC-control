package instrument;

import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.SerialUtils;

import static org.testng.Assert.*;

public class InstrumentFactoryTest {

    @BeforeClass
    public void setup() {
        SerialUtils serialUtils = Mockito.mock(SerialUtils.class);
        Mockito.when(serialUtils.open()).thenReturn(true);
        Mockito.when(serialUtils.write(Mockito.any(byte[].class))).thenReturn(true);
        Mockito.when(serialUtils.close()).thenReturn(true);
        Mockito.when(serialUtils.read()).thenReturn(null);
    }

    @Test
    public void testCreateInstrumentRotator() {
        try {
            Instrument rst = InstrumentFactory.createRotator("RotatorGS232B");
            assertTrue(rst instanceof Rotator);
            assertTrue(rst instanceof RotatorGS232B);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testCreateInstrumentTransceiver() {
        try {
            Instrument rst = InstrumentFactory.createTransceiver("TransceiverIC9100");
            assertTrue(rst instanceof Transceiver);
            assertTrue(rst instanceof TransceiverIC9100);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testCreateInstrumentEmptyString() {
        assertThrows(RuntimeException.class, () -> {
            InstrumentFactory.createRotator("");
        });
    }

    @Test
    public void testCreateInstrumentNullString() {
        assertThrows(RuntimeException.class, () -> {
            InstrumentFactory.createRotator(null);
        });
    }

    @Test
    public void testCreateInstrumentInvalidString() {
        assertThrows(RuntimeException.class, () -> {
            InstrumentFactory.createRotator("InvalidID");
        });
    }
}