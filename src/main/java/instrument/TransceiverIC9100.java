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
     */
    protected TransceiverIC9100() {
        this.comPort = ConfigurationUtils.getStrProperty("TRANSCEIVER_COM_PORT");
        this.baudRate = ConfigurationUtils.getIntProperty("TRANSCEIVER_BAUD");
        this.transAddr = ConfigurationUtils.getByteProperty("TRANSCEIVER_ADDRESS");
        this.serialUtils = new SerialUtils(comPort, baudRate, 8, 1, 0);
    }

    /**
     * Swap main and sub bands on the IC-9100. Main and sub cannot both be set to UHF or VHF frequencies at the same time.
     * For example, if the current main band is in UHF and a VHF frequency is provided to {@link TransceiverIC9100#setFrequency(long)} (or vice versa)
     * a swap must take place otherwise the set will fail.
     * @return The success/failure status of the operation.
     * @throws InterruptedException
     */
    private ResultUtils swapMainSub() throws InterruptedException {
        long preSwapVFOFreq = this.freqHz;
        Command swapMainSub = new CommandBuilder().address(this.transAddr).command((byte) 0x07).subCommand((byte) 0xB0)
                .buildCommand(); // 0x07 with sub command 0xB0 swaps main/sub
        this.serialUtils.open();
        this.serialUtils.write(swapMainSub.getCmdByteArr());
        readInstrument();
        if (preSwapVFOFreq == this.freqHz) {
            return ResultUtils.createFailedResult();
        }
        return ResultUtils.createSuccessfulResult();
    }

    public ResultUtils readInstrument() throws InterruptedException {
        /*
         * Step 1: Send read frequency command
         */
        this.serialUtils.open();
        Command readFreqCmd = new CommandBuilder().address(this.transAddr).command((byte) 0x03)
                .buildCommand(); // 0x03 reads frequency
        this.serialUtils.write(readFreqCmd.getCmdByteArr());
        TimeUnit.MILLISECONDS.sleep(200); // Delay to allow instrument to respond to command

        /*
         * Step 2: Read response from instrument.
         */
        byte[] rst;
        try {
            rst = this.serialUtils.read();
        } catch (NegativeArraySizeException e) {
            return ResultUtils.createFailedResult();
        }

        /*
         * Step 3: Parse frequency from response.
         * Frequency is contained in bytes 11 through 15 of response, each digit using half a byte with the least
         * significant digits first. See IC-9100 manual page 190.
         */
        byte[] freq = new byte[5];
        this.freqHz = 0;
        for (int i = 0; i < freq.length; i++) { // Flip order and store frequency (most significant digit first now)
            freq[freq.length-1-i] = rst[i+11];
        }
        for (int i = 0; i < freq.length; i++) {
            // Split top and bottom four bits (each digit stored in half a byte).
            int hiBits = (freq[i] & 0xF0) >> 4;
            int loBits = freq[i] & 0x0F;
            // hiBits and loBits now contain correct digits, now use position to determine multiple.
            this.freqHz += hiBits * (1000000000 / Math.pow(10, i*2));
            this.freqHz += loBits * (100000000 / Math.pow(10, i*2));
        }

        /*
         * Step 4: Send read mode command
         */
        Command readModeCmd = new CommandBuilder().address(this.transAddr).command((byte) 0x04)
                .buildCommand(); // 0x04 reads operating mode
        this.serialUtils.write(readModeCmd.getCmdByteArr());
        TimeUnit.MILLISECONDS.sleep(200); // Delay to allow instrument to respond to command

        /*
         * Step 5: Read response from instrument and parse modulation type from 11th byte (see IC-9100 manual pg 190)
         */
        rst = this.serialUtils.read();
        if (rst[11] == 0x05) { // 0x05 indicates FM
            this.modSetting = Modulation.FM;
        } else if (rst[11] == 0x02) { // 0x02 indicates AM
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
        /*
         * Step 1: Verify frequency is within allowable range.
         */
        if (!(FrequencyUtils.isUHF(freqHz) || FrequencyUtils.isVHF(freqHz))) {
            Log.error("Frequency " + freqHz + " is not in valid UHF or VHF amateur bands!");
            return ResultUtils.createFailedResult();
        }

        /*
         * Step 2: Determine if swap of main/sub required (is transceiver already in correct band?)
         */
        if (FrequencyUtils.isUHF(this.freqHz) != FrequencyUtils.isUHF(freqHz)) {
            if(!swapMainSub().isSuccessful()) {
                return ResultUtils.createFailedResult();
            }
            Log.debug("Main/sub band swapped on IC9100");
        }

        /*
         * Step 3: Prepare frequency bytes for data portion of command (see IC-9100 page 190)
         */
        byte[] rst = new byte[5];
        int[] digits = new int[10];
        long modFreqHz = freqHz;
        for (int i = 0; i < digits.length; i++) {
            digits[i] = (int) modFreqHz % 10; // Get least significant digit
            modFreqHz = modFreqHz / 10; // Remove least significant digit
            if (i % 2 != 0) { // Every second digit, prepare a byte (2 digits per byte)
                rst[i/2] = (byte) ((digits[i] << 4) | digits[i-1]); // Combine two digits into one byte
            }
        }

        /*
         * Step 4: Send command
         */
        Log.info("Setting frequency to " + freqHz* FrequencyUtils.HzToMHz + "MHz");
        Command writeFreqCmd = new CommandBuilder().address(this.transAddr).command((byte) 0x00).data(rst)
                .buildCommand(); // 0x00 to set frequency

        this.serialUtils.open();
        this.serialUtils.write(writeFreqCmd.getCmdByteArr());
        TimeUnit.MILLISECONDS.sleep(200); // Delay to allow instrument to respond to command
        this.serialUtils.close();

        /*
         * Step 5: Read from instrument and confirm set was successful.
         */
        readInstrument();
        if (this.freqHz != freqHz) {
            return ResultUtils.createFailedResult();
        }
        return ResultUtils.createSuccessfulResult();
    }

    public ResultUtils setModulation(Modulation mod) throws InterruptedException {
        /*
         * Step 1: Prepare modulation bytes for data portion of command (see IC-9100 page 190), create command
         */
        byte[] writeData = new byte[1];
        if (mod == Modulation.FM) {
            writeData[0] = 0x05;
        } else if (mod == Modulation.AM) {
            writeData[0] = 0x02;
        }
        Command writeModeCmd = new CommandBuilder().address(this.transAddr).command((byte) 0x01).data(writeData).buildCommand();

        /*
         * Step 2: Send command
         */
        this.serialUtils.open();
        this.serialUtils.write(writeModeCmd.getCmdByteArr());
        TimeUnit.MILLISECONDS.sleep(200);
        this.serialUtils.close();

        /*
         * Step 3: Read from instrument and confirm set was successful.
         */
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
     * Class to store the bytes of a CI-V command.
     * See ICOM IC-9100 manual page 183 for details of command structure.
     */
    private class Command {
        private byte addr;
        private byte cn;
        private byte sc;
        private byte[] data;
        private byte[] cmd;

        /**
         * Constructor called by {@link CommandBuilder}. See IC-9100 manual page 183 for specifics of command,
         * subcommand, and data fields.
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
         * Return a byte array of the command to be sent to the IC-9100. Command length varies based on number of
         * parameters set using {@link CommandBuilder}
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
