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
import utils.enums.Modulation;

import java.util.concurrent.TimeUnit;

/**
 * Class for communication with the ICOM IC-9100 transceiver.
 */
public class TransceiverIC9100 implements Transceiver {

    private final SerialUtils serialUtils;
    private final String comPort;
    private final int baudRate;
    private final byte transAddr;
    private long freqHz;
    private Modulation modSetting;


    /**
     * Instate this class via the {@link InstrumentFactory} only.
     * @throws InterruptedException
     */
    protected TransceiverIC9100() throws InterruptedException {
        this.comPort = ConfigurationUtils.getStrProperty("TRANSCEIVER_COM_PORT");
        this.baudRate = ConfigurationUtils.getIntProperty("TRANSCEIVER_BAUD");
        this.transAddr = ConfigurationUtils.getByteProperty("TRANSCEIVER_ADDRESS");
        this.serialUtils = new SerialUtils(comPort, baudRate, 8, 1, 0);
    }

    /**
     * Swap main and sub bands on the IC-9100. Main and sub cannot both be set to UHF or VHF frequencies at the same time.
     * For example, if the current main band is in UHF and a VHF frequency is provided to {@link TransceiverIC9100#setFrequency(long)} (or vice versa)
     * a swap must take place otherwise the set will fail.
     * @return the success/failure status of the operation.
     * @throws InterruptedException
     */
    private ResultUtils swapMainSub() throws InterruptedException {
        long preSwapVFOFreq = this.freqHz;
        Command swapMainSub = new CommandBuilder().address(this.transAddr).command((byte) 0x07).subCommand((byte) 0xB0)
                .buildCommand();
        this.serialUtils.open();
        this.serialUtils.write(swapMainSub.getCmdByteArr());
        readInstrument();
        if (preSwapVFOFreq == this.freqHz) {
            return ResultUtils.createFailedResult();
        }
        return ResultUtils.createSuccessfulResult();
    }

    public ResultUtils readInstrument() throws InterruptedException {
        this.serialUtils.open();
        Command readFreqCmd = new CommandBuilder().address(this.transAddr).command((byte) 0x03).buildCommand();
        this.serialUtils.write(readFreqCmd.getCmdByteArr());
        TimeUnit.MILLISECONDS.sleep(200);
        byte[] rst;
        try {
            rst = this.serialUtils.read();
        } catch (NegativeArraySizeException e) {
            return ResultUtils.createFailedResult();
        }
        byte[] freq = new byte[5];
        this.freqHz = 0;
        for (int i = 0; i < freq.length; i++) {
            freq[freq.length-1-i] = rst[i+11];
        }
        for (int i = 0; i < freq.length; i++) {
            int hiBits = (freq[i] & 0xF0) >> 4;
            int loBits = freq[i] & 0x0F;
            this.freqHz += hiBits * (1000000000 / Math.pow(10, i*2));
            this.freqHz += loBits * (100000000 / Math.pow(10, i*2));
        }

        Command readModeCmd = new CommandBuilder().address(this.transAddr).command((byte) 0x04).buildCommand();
        this.serialUtils.write(readModeCmd.getCmdByteArr());
        TimeUnit.MILLISECONDS.sleep(200);
        rst = this.serialUtils.read();
        if (rst[11] == 0x05) {
            this.modSetting = Modulation.FM;
        } else if (rst[11] == 0x02) {
            this.modSetting = Modulation.AM;
        }

        this.serialUtils.close();
        return ResultUtils.createSuccessfulResult();
    }

    public ResultUtils testConnect() throws InterruptedException {
        if (serialUtils.open() && readInstrument().isSuccessful() && serialUtils.close()) {
            return ResultUtils.createSuccessfulResult();
        }
        else {
            Log.error("TransceiverIC9100 connection test failed! Could not connect using port " + this.comPort
                    + " with baud " + this.baudRate);
            return ResultUtils.createFailedResult();
        }
    }

    public long getFrequencyHz() {
        return this.freqHz;
    }

    public Modulation getModulation() {
        return this.modSetting;
    }

    public ResultUtils setFrequency(long freqHz) throws InterruptedException {
        if (!(FrequencyUtils.isUHF(freqHz) || FrequencyUtils.isVHF(freqHz))) {
            Log.error("Frequency " + freqHz + " is not in valid UHF or VHF amateur bands!");
            return ResultUtils.createFailedResult();
        }
        if (FrequencyUtils.isUHF(this.freqHz) != FrequencyUtils.isUHF(freqHz)) {
            if(!swapMainSub().isSuccessful()) {
                return ResultUtils.createFailedResult();
            }
            Log.debug("Main/sub band swapped on IC9100");
        }
        byte[] rst = new byte[5];
        int[] digits = new int[10];
        long modFreqHz = freqHz;
        for (int i = 0; i < digits.length; i++) {
            digits[i] = (int) modFreqHz % 10;
            modFreqHz = modFreqHz / 10;
            if (i % 2 != 0) {
                rst[i/2] = (byte) ((digits[i] << 4) | digits[i-1]);
            }
        }
        Log.info("Setting frequency to " + freqHz* FrequencyUtils.HzToMHz + "MHz");
        Command writeFreqCmd = new CommandBuilder().address(this.transAddr).command((byte) 0x00).data(rst).buildCommand();

        this.serialUtils.open();
        this.serialUtils.write(writeFreqCmd.getCmdByteArr());
        TimeUnit.MILLISECONDS.sleep(200);
        this.serialUtils.close();
        readInstrument();
        /*System.out.println("this.freqHz = " + this.freqHz);
        System.out.println("freqHz = " + freqHz);*/
        if (this.freqHz != freqHz) {
            return ResultUtils.createFailedResult();
        }
        return ResultUtils.createSuccessfulResult();
    }

    public ResultUtils setModulation(Modulation mod) throws InterruptedException {
        byte[] writeData = new byte[1];
        if (mod == Modulation.FM) {
            writeData[0] = 0x05;
        } else if (mod == Modulation.AM) {
            writeData[0] = 0x02;
        }
        Command writeModeCmd = new CommandBuilder().address(this.transAddr).command((byte) 0x01).data(writeData).buildCommand();

        /*System.out.println("\n");
        byte[] dat = writeModeCmd.getCmdByteArr();
        for (int i = 0; i < dat.length; i++) {
            System.out.print(dat[i] + ":");
        }
        System.out.println("\n");*/

        this.serialUtils.open();
        this.serialUtils.write(writeModeCmd.getCmdByteArr());
        TimeUnit.MILLISECONDS.sleep(200);
        this.serialUtils.close();
        readInstrument();
        if (this.modSetting != mod) {
            return ResultUtils.createFailedResult();
        }
        return ResultUtils.createSuccessfulResult();
    }

    /**
     * Builder for creation of {@link Command} class. This structure allows for an instance of {@link Command} to be created
     * with a variable number of parameters. For example, address and command fields are always required in a valid CI-V command,
     * but subcommand and data fields may not be depending on the command being created.
     */
    private class CommandBuilder {
        private byte _addr;
        private byte _cn;
        private byte _sc = (byte) 0xff; //0xff used to indicate no subcommand
        private byte[] _data = {}; //length 0 array to indicate no data

        public CommandBuilder() {}

        public Command buildCommand() {
            return new Command(_addr, _cn, _sc, _data);
        }

        public CommandBuilder address(byte _addr) {
            this._addr = _addr;
            return this;
        }

        public CommandBuilder command(byte _cn) {
            this._cn = _cn;
            return this;
        }

        public CommandBuilder subCommand(byte _sc) {
            this._sc = _sc;
            return this;
        }

        public CommandBuilder data(byte[] _data) {
            this._data = _data;
            return this;
        }

    }

    /**
     * Class to store the bytes of a CI-V command
     */
    private class Command {
        private byte addr;
        private byte cn;
        private byte sc;
        private byte[] data;
        private byte[] cmd;

        /**
         * Constructor called by {@link CommandBuilder}. See IC-9100 manual for specifics of command, subcommand, and data fields.
         * @param addr the CI-V address of the IC-9100.
         * @param cn the command id number.
         * @param sc the subcommand id number.
         * @param data the data field.
         */
        private Command(byte addr, byte cn, byte sc, byte[] data) {
            this.addr = addr;
            this.cn = cn;
            this.sc = sc;
            this.data = data;
        }

        /**
         * Return a byte array of the command to be sent to the IC-9100.
         * @return full command byte array.
         */
        public byte[] getCmdByteArr() {
            if (sc == (byte) 0xff && data.length == 0) { // No subcommand, no data
                cmd = new byte[6];
                cmd[0] = (byte) 0xfe;
                cmd[1] = (byte) 0xfe;
                cmd[2] = addr;
                cmd[3] = 0x00;
                cmd[4] = cn;
                cmd[5] = (byte) 0xfd;
            } else if (sc == (byte) 0xff && data.length != 0) { // No subcommand, data
                cmd = new byte[6 + data.length];
                cmd[0] = (byte) 0xfe;
                cmd[1] = (byte) 0xfe;
                cmd[2] = addr;
                cmd[3] = 0x00;
                cmd[4] = cn;
                for (int i = 0; i < data.length; i++) {
                    cmd[5+i] = data[i];
                }
                cmd[cmd.length-1] = (byte) 0xfd;
            } else if (sc != (byte) 0xff && data.length == 0) { // Subcommand, no data
                cmd = new byte[7];
                cmd[0] = (byte) 0xfe;
                cmd[1] = (byte) 0xfe;
                cmd[2] = addr;
                cmd[3] = 0x00;
                cmd[4] = cn;
                cmd[5] = sc;
                cmd[6] = (byte) 0xfd;
            } else if (sc != (byte) 0xff && data.length != 0) { // Subcommand and data
                cmd = new byte[7 + data.length];
                cmd[0] = (byte) 0xfe;
                cmd[1] = (byte) 0xfe;
                cmd[2] = addr;
                cmd[3] = 0x00;
                cmd[4] = cn;
                cmd[5] = sc;
                for (int i = 0; i < data.length; i++) {
                    cmd[6+i] = data[i];
                }
                cmd[cmd.length-1] = (byte) 0xfd;
            }
            return cmd;
        }

    }
}
