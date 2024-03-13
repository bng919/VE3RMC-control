package instrument;

import org.testng.Assert;
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

        Assert.assertTrue(rotatorGS232B.goToAz(270).isSuccessful());
        Assert.assertTrue(Math.abs(rotatorGS232B.getAz()-270) < 2);

        Log.info("AZ: " + rotatorGS232B.getAz());
        Log.info("EL: " + rotatorGS232B.getEl());

        Assert.assertTrue(rotatorGS232B.goToEl(45).isSuccessful());
        Assert.assertTrue(Math.abs(rotatorGS232B.getEl() - 45) < 2);

        Log.info("AZ: " + rotatorGS232B.getAz());
        Log.info("EL: " + rotatorGS232B.getEl());

        Assert.assertFalse(rotatorGS232B.goToAz(-1).isSuccessful());

    }

    @Test
    public void testReadCalFile() throws InterruptedException {
        RotatorGS232B rotatorGS232B = new RotatorGS232B();
        // TODO: Testcase
    }

}