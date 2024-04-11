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

public class SerialUtils {

    private final SerialPort port;

    public SerialUtils(String comID, int baud, int numDataBits, int numStopBits, int parity) {
        this.port = SerialPort.getCommPort(comID);
        this.port.setBaudRate(baud);
        this.port.setNumDataBits(numDataBits);
        this.port.setNumStopBits(numStopBits);
        this.port.setParity(parity);
    }

    public boolean open() {
        return this.port.openPort();
    }

    public boolean write(byte[] data) {
        int rst = this.port.writeBytes(data, data.length);
        return rst != -1;
    }

    public byte[] read() {
        if (port.bytesAvailable() != 0) {
            byte[] newData = new byte[port.bytesAvailable()];
            port.readBytes(newData, newData.length);
            return newData;
        }
        return new byte[]{};
    }

    public boolean close() {
        return this.port.closePort();
    }
}
