package instrument;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.Log;
import utils.enums.Verbosity;

import static org.testng.Assert.*;

public class RotatorGS232BTest {

    @BeforeClass
    public void setup() {
        new Log(".\\logs\\", Verbosity.DEBUG);
    }

    @Test
    public void testReadWriteInstrument() throws InterruptedException {

        RotatorGS232B rotatorGS232B = new RotatorGS232B();

        Log.info("AZ: " + rotatorGS232B.getAz());
        Log.info("EL: " + rotatorGS232B.getEl());

        rotatorGS232B.goToAz(270);

        Log.info("AZ: " + rotatorGS232B.getAz());
        Log.info("EL: " + rotatorGS232B.getEl());

    }

}