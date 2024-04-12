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

package utils;

import com.fazecast.jSerialComm.*;

/**
 * Wrapper for {@link com.fazecast.jSerialComm} package designed specifically for the communication requirements of
 * this program.
 * TODO: Methods should return ResultUtils for readability and consistency.
 */
public class SerialUtils {

    private final SerialPort port;

    /**
     * Create a new instance (typically one per instrument).
     * @param comID COM port.
     * @param baud Baud rate.
     * @param numDataBits Number of data bits.
     * @param numStopBits Number of stop bits.
     * @param parity Parity.
     */
    public SerialUtils(String comID, int baud, int numDataBits, int numStopBits, int parity) {
        this.port = SerialPort.getCommPort(comID);
        this.port.setBaudRate(baud);
        this.port.setNumDataBits(numDataBits);
        this.port.setNumStopBits(numStopBits);
        this.port.setParity(parity);
    }

    /**
     * Open serial connection.
     * @return True if successful, false if failed.
     */
    public boolean open() {
        return this.port.openPort();
    }

    /**
     * Write a byte array to the serial port.
     * @param data Data to write.
     * @return True if successful, false if failed.
     */
    public boolean write(byte[] data) {
        int rst = this.port.writeBytes(data, data.length);
        return rst != -1;
    }

    /**
     * Read a byte array from the serial port.
     * @return Data read from port. Empty array if no bytes are available on the port.
     */
    public byte[] read() {
        if (port.bytesAvailable() != 0) {
            byte[] newData = new byte[port.bytesAvailable()];
            port.readBytes(newData, newData.length);
            return newData;
        }
        return new byte[]{};
    }

    /**
     * Close the serial port.
     * @return True if successful, false if failed.
     */
    public boolean close() {
        return this.port.closePort();
    }
}
