package instrument;

import utils.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RotatorGS232B implements Rotator {

    private static final int AZ_TOLERANCE_DEG = 2;
    private static final int EL_TOLERANCE_DEG = 2;
    private final String correctionFilePath;
    private final int[] correctionList = new int[360];
    private static final int MOTION_TIMEOUT_MILLIS = 40000; //TODO: Scale based on delta to move
    Serial serial;
    private final String comPort;
    private final int baudRate;
    private int currAz;
    private int currEl;

    public RotatorGS232B() throws InterruptedException {
        this.comPort = PropertyHelper.getStrProperty("ROTATOR_COM_PORT");
        this.baudRate = PropertyHelper.getIntProperty("ROTATOR_BAUD");
        this.correctionFilePath = PropertyHelper.getStrProperty("ROTATOR_CALIBRATION_PATH");
        readCorrectionFile();
        this.serial = new Serial(this.comPort, this.baudRate, 8, 1, 0);
    }

    private void readCorrectionFile() {
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultHelper readInstrument() throws InterruptedException {
        byte[] readAzElCmd = {0x43, 0x32, 0x0D};

        this.serial.open();
        this.serial.write(readAzElCmd);
        TimeUnit.MILLISECONDS.sleep(250);
        byte[] rst = this.serial.read();
        if (rst.length == 0) {
            return ResultHelper.createFailedResult();
        }
        this.serial.close();

        int azOffset = 3;
        int elOffset = 11;
        if(rst[3] == '-') { //Az can read as -000, disregard minus sign
            azOffset++;
            elOffset++;
        }

        int azAngle = 0;
        int elAngle = 0;

        try { //TODO: REMOVE TRY CATCH
            //TODO: Cleanup to loop
            azAngle += (rst[0 + azOffset] - 48) * 100;
            elAngle += (rst[0 + elOffset] - 48) * 100;
            azAngle += (rst[1 + azOffset] - 48) * 10;
            elAngle += (rst[1 + elOffset] - 48) * 10;
            azAngle += (rst[2 + azOffset] - 48);
            elAngle += (rst[2 + elOffset] - 48);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.error(e.getMessage());
            Log.error(HexFormat.hexDump(rst));
            return ResultHelper.createFailedResult();
        }

        this.currAz = azAngle;
        this.currEl = elAngle;

        return ResultHelper.createSuccessfulResult();
    }

    public ResultHelper testConnect() throws InterruptedException {
        if (serial.open() && readInstrument().isSuccessful() && serial.close()) {
            return ResultHelper.createSuccessfulResult();
        }
        else {
            Log.error("RotatorGS232B connection test failed! Could not connect using port " + this.comPort
                    + " with baud " + this.baudRate);
            return ResultHelper.createFailedResult();
        }
    }

    public int getAz() {
        return currAz;
    }

    public int getEl() {
        return currEl;
    }

    public ResultHelper goToAz(int az) throws InterruptedException {
        if (az < 0 || az > 359) {
            return ResultHelper.createFailedResult();
        }
        az = correctionList[az];
        Log.debug("After correcting for calibration, az = " + az);
        byte[] setAzCmd = "M".getBytes();
        byte[] posByte = String.valueOf(az).getBytes();
        byte[] cmd = {setAzCmd[0], 0x30, 0x30, 0x30, 0x0D};
        for (int i = 0; i < posByte.length; i++) {
            cmd[cmd.length-2-i] = posByte[posByte.length-1-i];
        }

        this.serial.open();
        this.serial.write(cmd);
        TimeUnit.MILLISECONDS.sleep(200);
        this.serial.close();

        long motionStart = System.currentTimeMillis();
        //readInstrument();
        while (this.currAz <= az-AZ_TOLERANCE_DEG || this.currAz >= az+AZ_TOLERANCE_DEG) {
            if ((System.currentTimeMillis()-motionStart) >= MOTION_TIMEOUT_MILLIS) {
                return ResultHelper.createFailedResult();
            }
            TimeUnit.MILLISECONDS.sleep(200);
            readInstrument();
        }
        return ResultHelper.createSuccessfulResult();
    }

    public ResultHelper goToEl(int el) throws InterruptedException {
        if (el < 0 || el > 180) {
            return ResultHelper.createFailedResult();
        }
        readInstrument();
        byte[] posByte = String.valueOf(el).getBytes();
        byte[] currAzByte = String.valueOf(this.currAz).getBytes();
        byte[] cmd = {0x57, 0x30, 0x30, 0x30, 0x20, 0x30, 0x30, 0x30, 0x0D};
        for (int i = 0; i < posByte.length; i++) {
            cmd[cmd.length-2-i] = posByte[posByte.length-1-i];
        }
        for (int i = 0; i < currAzByte.length; i++) {
            cmd[cmd.length-6-i] = currAzByte[currAzByte.length-1-i];
        }
        this.serial.open();
        this.serial.write(cmd);
        TimeUnit.MILLISECONDS.sleep(200);
        this.serial.close();

        long motionStart = System.currentTimeMillis();
        while (this.currEl <= el-EL_TOLERANCE_DEG || this.currEl >= el+EL_TOLERANCE_DEG) {
            if ((System.currentTimeMillis()-motionStart) >= MOTION_TIMEOUT_MILLIS) {
                return ResultHelper.createFailedResult();
            }
            TimeUnit.MILLISECONDS.sleep(200);
            readInstrument();
        }
        return ResultHelper.createSuccessfulResult();
    }

    public ResultHelper goToAzEl(int az, int el) throws InterruptedException {
        // Assume successful
        ResultHelper azRst = ResultHelper.createSuccessfulResult();
        ResultHelper elRst = ResultHelper.createSuccessfulResult();
        if (Math.abs(this.currAz - az) > AZ_TOLERANCE_DEG) {
            Log.info("Moving to position Az " + az);
            azRst = goToAz(az);
        } else {
            Log.debug("Az not updated on instrument, new Az less then " + AZ_TOLERANCE_DEG + " from current position");
        }
        if (Math.abs(this.currEl - el) > EL_TOLERANCE_DEG) {
            Log.info("Moving to position El " + el);
            elRst = goToEl(el);
        } else {
            Log.debug("El not updated on instrument, new El less then " + EL_TOLERANCE_DEG + " from current position");
        }
        return azRst.and(elRst);
    }
}
