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

import utils.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Class for communication with the Alfa Rot2Prog rotator controller. This rotator has an az range of
 * 360 degrees plus an additional 180 deg on each side of the range, and el range of 180 deg. The controller takes the
 * shortest path to the requested position in azimuth, for example to move from 350 deg to 0 deg, the rotator will move
 * 10 deg. On the controller screen, values are only reported in the 0-359 range, and the LED to the bottom right of the
 * decimal place flashes or lights up solid to indicate if outside the normal 0 - 360 range. However, when queried via
 * the serial port, the controller reports the values in the range 180 - 900 deg range. The goTo methods in this class
 * still only take values in the range of 0-359 deg for az, and 0-90 deg in elevation, leaving the controller to decide
 * the shortest path to the requested position. In {@link RotatorRot2ProgImpl#readInstrument()}, values are adjusted
 * such that they are always reported to be in the 0 - 359 deg range.
 */
public class RotatorRot2ProgImpl implements Rotator {

    private static final int AZ_TOLERANCE_DEG = 1;
    private static final int EL_TOLERANCE_DEG = 1;
    private final String correctionFilePath;
    private final int[] correctionList = new int[360];

    //Larger than for GS232B since range is bigger. TODO: Scale based on delta to move
    private static final int MOTION_TIMEOUT_MILLIS = 120000;
    private static final int AZ_CMD_OFFSET = 1; // Start position of Az bytes in commands
    private static final int EL_CMD_OFFSET = 6; // Start position of El bytes in commands
    SerialUtils serialUtils;
    private final String comPort;
    private final int baudRate;
    private int currAz;
    private int currEl;

    protected RotatorRot2ProgImpl() {
        this.comPort = ConfigurationUtils.getStrProperty("ROTATOR_COM_PORT");
        this.baudRate = ConfigurationUtils.getIntProperty("ROTATOR_BAUD");
        this.correctionFilePath = ConfigurationUtils.getStrProperty("ROTATOR_CALIBRATION_PATH");
        readCorrectionFile(correctionFilePath);
        this.serialUtils = new SerialUtils(this.comPort, this.baudRate, 8, 1, 0);
    }

    /**
     * Read the values of the rotator correction configuration file.
     */
    protected void readCorrectionFile(String correctionFilePath) {
        BufferedReader fileReader;
        try {
            fileReader = new BufferedReader(new FileReader(correctionFilePath));
            String line = fileReader.readLine();
            int count = 0;
            while (line != null) {
                if (count > 359) {
                    throw new RuntimeException("Too many lines in rotator calibration file at " + this.correctionFilePath);
                }
                correctionList[count] = Integer.parseInt(line.strip());
                line = fileReader.readLine();
                count++;
            }
            if (count < 359) {
                throw new RuntimeException("Too few lines in rotator calibration file at " + this.correctionFilePath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the correction values as a. int[].
     * @return Correction values.
     */
    public int[] getCorrectionList() {
        return this.correctionList;
    }

    public ResultUtils readInstrument() throws InterruptedException {
        /*
         * Step 1: Send read az and el command to instrument, read response.
         * See: https://github.com/jaidenfe/rot2proG
         */
        byte[] readAzElCmd = {0x57, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x1F, 0x20};
        this.serialUtils.open();
        this.serialUtils.write(readAzElCmd);
        // Delay to allow instrument to respond to command, needs to be very long as the baud rate is very slow
        TimeUnit.MILLISECONDS.sleep(600);
        byte[] rst = this.serialUtils.read(); // Check for response
        if (rst.length == 0) { // Cmd failed if no response
            return ResultUtils.createFailedResult();
        }
        this.serialUtils.close();
        TimeUnit.MILLISECONDS.sleep(300); // Delay to allow instrument after closing port

        /*
         * Step 2: Parse az and el values from response.
         */
        byte[] az = new byte[3];
        byte[] el = new byte[3];
        for (int i = 0; i < 3; i++) {
            // Add 0x30 to convert number to ASCII representation of the number
            az[i] = (byte) (rst[i + AZ_CMD_OFFSET] + 0x30);
            el[i] = (byte) (rst[i + EL_CMD_OFFSET] + 0x30);
        }
        int azAngle = Integer.parseInt(new String(az, StandardCharsets.US_ASCII));
        int elAngle = Integer.parseInt(new String(el, StandardCharsets.US_ASCII));

        if (azAngle >= 720) { // Keep final az reported withing 0-359
            azAngle -= 360;
        }
        if (azAngle >= 360) { // Keep final az reported withing 0-359
            azAngle -= 360;
        }

        this.currAz = azAngle;
        this.currEl = elAngle-360;

        return ResultUtils.createSuccessfulResult();
    }

    public ResultUtils testConnect() throws InterruptedException {
        if (serialUtils.open() && readInstrument().isSuccessful() && serialUtils.close()) {
            return ResultUtils.createSuccessfulResult();
        }
        else {
            Log.error("RotatorRot2Prog connection test failed! Could not connect using port " + this.comPort
                    + " with baud " + this.baudRate);
            return ResultUtils.createFailedResult();
        }
    }

    public int getAz() {
        return currAz;
    }

    public int getEl() {
        return currEl;
    }

    public ResultUtils goToAz(int az) throws InterruptedException {
        readInstrument(); // Get current position
        return goToAzEl(az, this.currEl); // Set new az, keep El same
    }

    public ResultUtils goToEl(int el) throws InterruptedException {
        readInstrument(); // Get current position
        return goToAzEl(this.currAz, el); // Set new el, keep az same
    }

    public ResultUtils goToAzEl(int az, int el) throws InterruptedException {
        /*
         * Step 1: Verify el is within acceptable range, check if < tolerance and correct for cal
         */
        if ((el < 0 || el > 180) || (az < 0 || az > 359)) {
            return ResultUtils.createFailedResult();
        }

        // Only move if step is larger than AZ_TOLERANCE_DEG to prevent redundant updating
        if (Math.abs(this.currAz - az) > AZ_TOLERANCE_DEG) {
            Log.info("Moving to position Az " + az);
        } else {
            az = this.currAz;
            Log.debug("Az not updated on instrument, new Az less then " + AZ_TOLERANCE_DEG + " from current position");
        }
        // Only move if step is larger than EL_TOLERANCE_DEG to prevent redundant updating
        if (Math.abs(this.currEl - el) > EL_TOLERANCE_DEG) {
            Log.info("Moving to position El " + el);
        } else {
            el = this.currEl;
            Log.debug("El not updated on instrument, new El less then " + EL_TOLERANCE_DEG + " from current position");
        }

        az = correctionList[az];
        Log.debug("After correcting for calibration, az = " + az);

        /*
         * Step 2: Generate and then send command to set azimuth and elevation
         * Note: Az and el must always be set together.
         */
        byte[] elBytes = String.valueOf(el + 360).getBytes(); // Add 360 per spec
        byte[] azBytes = String.valueOf(az + 360).getBytes();

        // Zero (in ascii) as placeholder for values
        byte[] cmd = {0x57, 0x30, 0x30, 0x30, 0x30, 0x01, 0x30, 0x30, 0x30, 0x30, 0x01, 0x2F, 0x20};
        for (int i = 0; i < elBytes.length; i++) { // Place elevation bytes
            cmd[EL_CMD_OFFSET+1+i] = elBytes[i];
        }
        for (int i = 0; i < azBytes.length; i++) { // Place azimuth bytes (azimuth not changed)
            cmd[AZ_CMD_OFFSET+1+i] = azBytes[i];
        }

        /*
         * Step 3: Send command.
         */
        this.serialUtils.open();
        this.serialUtils.write(cmd);
        TimeUnit.MILLISECONDS.sleep(300);
        this.serialUtils.close();

        /*
         * Step 4: Wait until rotators position has reached within +/- EL_TOLERANCE_DEG
         */
        long motionStart = System.currentTimeMillis();
        while ((this.currEl <= el - EL_TOLERANCE_DEG || this.currEl >= el + EL_TOLERANCE_DEG)
                || (this.currAz <= az - AZ_TOLERANCE_DEG || this.currAz >= az + AZ_TOLERANCE_DEG)) { // || ((az == 0) && (this.currAz >= AZ_TOLERANCE_DEG || this.currAz <= 360-AZ_TOLERANCE_DEG))
            if ((System.currentTimeMillis() - motionStart) >= MOTION_TIMEOUT_MILLIS) {
                return ResultUtils.createFailedResult();
            }
            TimeUnit.MILLISECONDS.sleep(300);
            readInstrument();
        }
        return ResultUtils.createSuccessfulResult();
    }

}
