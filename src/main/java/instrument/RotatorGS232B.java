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
import java.util.concurrent.TimeUnit;

/**
 * Class for communication with the Yaesu GS232B rotator controller.
 */
public class RotatorGS232B implements Rotator {

    private static final int AZ_TOLERANCE_DEG = 2;
    private static final int EL_TOLERANCE_DEG = 2;
    private final String correctionFilePath;
    private final int[] correctionList = new int[360];
    private static final int MOTION_TIMEOUT_MILLIS = 40000; //TODO: Scale based on delta to move
    SerialUtils serialUtils;
    private final String comPort;
    private final int baudRate;
    private int currAz;
    private int currEl;

    /**
     * Instate this class via the {@link InstrumentFactory} only.
     */
    protected RotatorGS232B() {
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
         */
        byte[] readAzElCmd = {0x43, 0x32, 0x0D}; // Read command (see GS232B manual page 16): C2<CR>
        this.serialUtils.open();
        this.serialUtils.write(readAzElCmd);
        TimeUnit.MILLISECONDS.sleep(250); // Delay to allow instrument to respond to command
        byte[] rst = this.serialUtils.read(); // Check for response
        if (rst.length == 0) { // Cmd failed if no response
            return ResultUtils.createFailedResult();
        }
        this.serialUtils.close();

        /*
         * Step 2: Parse az and el values from response.
         * Response is in form: AZ=000  EL=000
         */
        int azOffset = 3;
        int elOffset = 11;
        if(rst[3] == '-') { // Az can read as -000, disregard minus sign
            azOffset++;
            elOffset++;
        }

        int azAngle = 0;
        int elAngle = 0;

        try { //TODO: Improve error handling. Catch block could reissue command if incorrect number of bytes returned.
            // Convert ascii char to int (subtract 48) then multiply by order of magnitude.
            //TODO: Cleanup to loop
            azAngle += (rst[azOffset] - 48) * 100;
            elAngle += (rst[elOffset] - 48) * 100;
            azAngle += (rst[1 + azOffset] - 48) * 10;
            elAngle += (rst[1 + elOffset] - 48) * 10;
            azAngle += (rst[2 + azOffset] - 48);
            elAngle += (rst[2 + elOffset] - 48);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.error(e.getMessage());
            Log.error(HexadecimalUtils.hexDump(rst));
            return ResultUtils.createFailedResult();
        }

        this.currAz = azAngle;
        this.currEl = elAngle;

        return ResultUtils.createSuccessfulResult();
    }

    public ResultUtils testConnect() throws InterruptedException {
        if (serialUtils.open() && readInstrument().isSuccessful() && serialUtils.close()) {
            return ResultUtils.createSuccessfulResult();
        }
        else {
            Log.error("RotatorGS232B connection test failed! Could not connect using port " + this.comPort
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
        /*
         * Step 1: Verify az is within acceptable range, then adjust based on correction specified in the
         * rotatorCalibration file.
         */
        if (az < 0 || az > 359) {
            return ResultUtils.createFailedResult();
        }
        az = correctionList[az];
        Log.debug("After correcting for calibration, az = " + az);

        /*
         * Step 2: Generate and then send command M (set azimuth, see GS232B manual page 17)
         */
        byte[] setAzCmd = "M".getBytes();
        byte[] posByte = String.valueOf(az).getBytes(); // Convert az to a string, then get bytes of each char

        // Setup three zero bytes (0x30) as a placeholder, therefore if posByte is less than 3 in length, the zeros
        // will already be present.
        byte[] cmd = {setAzCmd[0], 0x30, 0x30, 0x30, 0x0D};
        for (int i = 0; i < posByte.length; i++) { // Place posByte
            cmd[cmd.length-2-i] = posByte[posByte.length-1-i];
        }

        /*
         * Step 3: Send command.
         */
        this.serialUtils.open();
        this.serialUtils.write(cmd);
        TimeUnit.MILLISECONDS.sleep(200); // Delay to allow instrument to respond to command
        this.serialUtils.close();

        /*
         * Step 4: Wait until rotators position has reached within +/- AZ_TOLERANCE_DEG
         */
        long motionStart = System.currentTimeMillis();
        while (this.currAz <= az-AZ_TOLERANCE_DEG || this.currAz >= az+AZ_TOLERANCE_DEG) {
            if ((System.currentTimeMillis()-motionStart) >= MOTION_TIMEOUT_MILLIS) {
                return ResultUtils.createFailedResult();
            }
            TimeUnit.MILLISECONDS.sleep(200);
            readInstrument();
        }
        return ResultUtils.createSuccessfulResult();
    }

    public ResultUtils goToEl(int el) throws InterruptedException {
        /*
         * Step 1: Verify el is within acceptable range, read current position (need to get the current azimuth)
         */
        if (el < 0 || el > 180) {
            return ResultUtils.createFailedResult();
        }
        readInstrument();

        /*
         * Step 2: Generate and then send command W (set azimuth and elevation, see GS232B manual page 17)
         * Note: Elevation cannot be set without azimuth also being set on GS232B
         */
        byte[] posByte = String.valueOf(el).getBytes();
        byte[] currAzByte = String.valueOf(this.currAz).getBytes();
        byte[] cmd = {0x57, 0x30, 0x30, 0x30, 0x20, 0x30, 0x30, 0x30, 0x0D}; // Zero as placeholders (0x20 is a space)
        for (int i = 0; i < posByte.length; i++) { // Place elevation bytes
            cmd[cmd.length-2-i] = posByte[posByte.length-1-i];
        }
        for (int i = 0; i < currAzByte.length; i++) { // Place azimuth bytes (azimuth not changed)
            cmd[cmd.length-6-i] = currAzByte[currAzByte.length-1-i];
        }

        /*
         * Step 3: Send command.
         */
        this.serialUtils.open();
        this.serialUtils.write(cmd);
        TimeUnit.MILLISECONDS.sleep(200);
        this.serialUtils.close();

        /*
         * Step 4: Wait until rotators position has reached within +/- EL_TOLERANCE_DEG
         */
        long motionStart = System.currentTimeMillis();
        while (this.currEl <= el-EL_TOLERANCE_DEG || this.currEl >= el+EL_TOLERANCE_DEG) {
            if ((System.currentTimeMillis()-motionStart) >= MOTION_TIMEOUT_MILLIS) {
                return ResultUtils.createFailedResult();
            }
            TimeUnit.MILLISECONDS.sleep(200);
            readInstrument();
        }
        return ResultUtils.createSuccessfulResult();
    }

    public ResultUtils goToAzEl(int az, int el) throws InterruptedException {
        // Assume successful, update if either set fails
        ResultUtils azRst = ResultUtils.createSuccessfulResult();
        ResultUtils elRst = ResultUtils.createSuccessfulResult();
        // Only move if step is larger than AZ_TOLERANCE_DEG to prevent redundant updating
        if (Math.abs(this.currAz - az) > AZ_TOLERANCE_DEG) {
            Log.info("Moving to position Az " + az);
            azRst = goToAz(az);
        } else {
            Log.debug("Az not updated on instrument, new Az less then " + AZ_TOLERANCE_DEG + " from current position");
        }
        // Only move if step is larger than EL_TOLERANCE_DEG to prevent redundant updating
        if (Math.abs(this.currEl - el) > EL_TOLERANCE_DEG) {
            Log.info("Moving to position El " + el);
            elRst = goToEl(el);
        } else {
            Log.debug("El not updated on instrument, new El less then " + EL_TOLERANCE_DEG + " from current position");
        }
        return azRst.and(elRst);
    }
}
