package utils.enums;

import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class VerbosityTest {

    @Test
    public void testIsEqOrHigher() {
        Assert.assertTrue(Verbosity.DEBUG.isEqOrHigher(Verbosity.INFO));
        Assert.assertTrue(Verbosity.INFO.isEqOrHigher(Verbosity.WARN));
        Assert.assertTrue(Verbosity.WARN.isEqOrHigher(Verbosity.ERROR));
        Assert.assertFalse(Verbosity.ERROR.isEqOrHigher(Verbosity.DEBUG));
    }

}