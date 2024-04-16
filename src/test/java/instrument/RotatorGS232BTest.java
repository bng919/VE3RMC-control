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

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.Log;
import utils.enums.Verbosity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;

import static org.testng.Assert.*;

public class RotatorGS232BTest {

    @BeforeClass
    public void setup() {
        new Log(".\\logs\\", Verbosity.DEBUG);
    }

    @Test
    public void testReadWriteInstrument() throws InterruptedException {
        /*
         * NOTE: This test case requires the GS232B rotator controller to be connected!
         */
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
    public void testReadCalFile() {
        String correctionFilePath = ".\\config\\rotatorCalibration.txt";
        RotatorGS232B rotatorGS232B = new RotatorGS232B();
        rotatorGS232B.readCorrectionFile(correctionFilePath);
        int[] correction = rotatorGS232B.getCorrectionList();
        Assert.assertEquals(correction.length, 360);
        BufferedReader fileReader;
        int[] comparison = new int[360];;
        try {
            fileReader = new BufferedReader(new FileReader(correctionFilePath));
            String line = fileReader.readLine();
            int count = 0;
            while (line != null) {
                comparison[count] = Integer.parseInt(line.strip());
                line = fileReader.readLine();
                count++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < comparison.length; i++) {
            Assert.assertEquals(correction[i], comparison[i]);
        }

    }

}