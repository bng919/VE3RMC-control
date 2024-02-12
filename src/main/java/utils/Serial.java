package utils;

import com.fazecast.jSerialComm.*;

public class Serial {

    private SerialPort port;

    public Serial(String comID, int baud, int numDataBits, int numStopBits, int parity) {
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

//    public void read() {
//        port.addDataListener(new SerialPortDataListener() {
//            @Override
//            public int getListeningEvents() {
//                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
//            }
//
//            @Override
//            public void serialEvent(SerialPortEvent serialPortEvent) {
//                if (serialPortEvent.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
//                    return;
//                }
//                byte[] newData = new byte[port.bytesAvailable()];
//                int numRead = port.readBytes(newData, newData.length);
//                System.out.println("Read " + numRead + " bytes: " + new String(newData, StandardCharsets.UTF_8));
//            }
//        });
//    }

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
