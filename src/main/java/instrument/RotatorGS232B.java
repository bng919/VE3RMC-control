package instrument;

import utils.HexFormat;
import utils.Log;
import utils.ResultHelper;
import utils.Serial;

import java.util.concurrent.TimeUnit;

public class RotatorGS232B implements Rotator {

    private static final int AZ_TOLERANCE_DEG = 2;
    private static final int EL_TOLERANCE_DEG = 2;
    private static final int MOTION_TIMEOUT_MILLIS = 40000; //TODO: Scale based on delta to move
    Serial serial;
    private int currAz;
    private int currEl;


    public RotatorGS232B() throws InterruptedException {
        //TODO: Read parameters from config file
        this.serial = new Serial("COM4", 2400, 8, 1, 0);
        readInstrument();
    }

    public void readInstrument() throws InterruptedException {
        byte[] readAzElCmd = {0x43, 0x32, 0x0D};

        this.serial.open();
        this.serial.write(readAzElCmd);
        TimeUnit.MILLISECONDS.sleep(250);
        byte[] rst = this.serial.read();
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
        }

        this.currAz = azAngle;
        this.currEl = elAngle;
    }

    public ResultHelper testConnect() {
        if (serial.open() && serial.close()) {
            return ResultHelper.createSuccessfulResult();
        }
        else {
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
        if (az < 0 || az > 360) {
            return ResultHelper.createFailedResult();
        }
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
            azRst = goToAz(az);
        }
        if (Math.abs(this.currEl - el) > EL_TOLERANCE_DEG) {
            elRst = goToEl(el);
        }
        return azRst.and(elRst);
    }
}
