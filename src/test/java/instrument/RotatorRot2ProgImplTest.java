package instrument;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.Log;
import utils.enums.Verbosity;

import static org.testng.Assert.*;

public class RotatorRot2ProgImplTest {

    @BeforeClass
    public void setup() {
        new Log(".\\logs\\", Verbosity.DEBUG);
    }

    @Test
    public void testReadWriteInstrument() throws InterruptedException {
        /*
         * NOTE: This test case requires the Rot2Prog rotator controller to be connected!
         */
        RotatorRot2ProgImpl rotatorRot2Prog = new RotatorRot2ProgImpl();

        Log.info("AZ: " + rotatorRot2Prog.getAz());
        Log.info("EL: " + rotatorRot2Prog.getEl());

        Assert.assertTrue(rotatorRot2Prog.goToAz(270).isSuccessful());
        Assert.assertTrue(Math.abs(rotatorRot2Prog.getAz()-270) < 1);

        Log.info("AZ: " + rotatorRot2Prog.getAz());
        Log.info("EL: " + rotatorRot2Prog.getEl());

        Assert.assertTrue(rotatorRot2Prog.goToEl(45).isSuccessful());
        Assert.assertTrue(Math.abs(rotatorRot2Prog.getEl() - 45) < 1);

        Log.info("AZ: " + rotatorRot2Prog.getAz());
        Log.info("EL: " + rotatorRot2Prog.getEl());

        Assert.assertTrue(rotatorRot2Prog.goToAzEl(0, 0).isSuccessful());
        Assert.assertTrue(Math.abs(rotatorRot2Prog.getEl() - 0) < 1);
        Assert.assertTrue(Math.abs(rotatorRot2Prog.getAz() - 0) < 1);

        Log.info("AZ: " + rotatorRot2Prog.getAz());
        Log.info("EL: " + rotatorRot2Prog.getEl());

        Assert.assertTrue(rotatorRot2Prog.goToAzEl(359, 179).isSuccessful());
        Assert.assertTrue(Math.abs(rotatorRot2Prog.getEl() - 179) < 1);
        Assert.assertTrue(Math.abs(rotatorRot2Prog.getAz() - 359) < 1);

        Log.info("AZ: " + rotatorRot2Prog.getAz());
        Log.info("EL: " + rotatorRot2Prog.getEl());

        Assert.assertTrue(rotatorRot2Prog.goToAzEl(180, 90).isSuccessful());
        Assert.assertTrue(Math.abs(rotatorRot2Prog.getEl() - 90) < 1);
        Assert.assertTrue(Math.abs(rotatorRot2Prog.getAz() - 180) < 1);

        Log.info("AZ: " + rotatorRot2Prog.getAz());
        Log.info("EL: " + rotatorRot2Prog.getEl());

        Assert.assertTrue(rotatorRot2Prog.goToAzEl(0, 0).isSuccessful());
        Assert.assertTrue(Math.abs(rotatorRot2Prog.getEl() - 0) < 1);
        Assert.assertTrue(Math.abs(rotatorRot2Prog.getAz() - 0) < 1);

        Log.info("AZ: " + rotatorRot2Prog.getAz());
        Log.info("EL: " + rotatorRot2Prog.getEl());

        Assert.assertFalse(rotatorRot2Prog.goToAz(-1).isSuccessful());
        Assert.assertFalse(rotatorRot2Prog.goToEl(-1).isSuccessful());

    }

}