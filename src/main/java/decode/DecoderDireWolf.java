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

package decode;

import utils.HexadecimalUtils;
import utils.Log;
import utils.ConfigurationUtils;
import utils.TimeUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for communication with the Dire Wolf packet radio modem.
 * The Dire Wolf executable is started and passed the baud rate of data specified in the configuration file.
 * A socket connection is opened to the Dire Wolf KISS port to receive the demodulated data.
 *
 * Dire Wolf can be started in a separate thread using {@link DecoderDireWolf#run()} or in the existing thread
 * using {@link DecoderDireWolf#startDecoder()}.
 */
public class DecoderDireWolf implements Decoder {

    private List<byte[]> decodedData = new ArrayList<>();
    private String direWolfDir;
    private int kissPort = ConfigurationUtils.getIntProperty("DECODER_KISS_PORT");
    private int durationS;

    public DecoderDireWolf() {}

    public void run() {
        Log.debug("Running DireWolf decoder in thread " + Thread.currentThread().threadId());
        startDecoder();
    }

    public void startDecoder() {
        /*
         * Step 1: Configure, then execute command to start dire wolf executable.
         */
        ProcessBuilder direWolfPb = new ProcessBuilder(direWolfDir + "\\direwolf", "-B "
                + ConfigurationUtils.getStrProperty("SAT_BAUD"));
        Log.debug("Starting DireWolf...");
        Log.debug(String.join(" ", direWolfPb.command().toArray(new String[0])));
        direWolfPb.directory(new File(direWolfDir));
        Process direWolfP = null;
        try {
            direWolfP = direWolfPb.start();
            Log.debug("DireWolf started");
        } catch (IOException e) {
            Log.error("DireWolf failed to start.");
            throw new RuntimeException(e);
        }
        Log.debug("Waiting for DireWolf to setup");
        TimeUtils.delayMillis(1000); // Give dire wolf time to set up

        /*
         * Step 2: Open socket connection to dire wolf KISS port
         */
        Socket s;
        InputStream in;
        try {
            Log.debug("Attempting connection to DireWolf KISS port " + kissPort);
            s = new Socket("localhost", kissPort);
            in = s.getInputStream();
        } catch (IOException e) {
            Log.error("Could not connect to DireWolf KISS on port " + kissPort + ". Is the port number correct?");
            throw new RuntimeException(e);
        }

        Log.info("DireWolf started successfully");

        /*
         * Step 3: Monitor socket port for data for the duration of the pass
         */
        long endTime = System.currentTimeMillis() + durationS*1000L;
        Log.debug("Monitoring DireWolf KISS port for " + durationS + "s.");
        durationLoop:
        while (System.currentTimeMillis() < endTime) { // While pass active
            try {
                List<Byte> rst = new ArrayList<Byte>();
                while (in.available() == 0) {
                    if (System.currentTimeMillis() >= endTime) {
                        Log.debug("DireWolf monitoring time complete.");
                        break durationLoop;
                    }
                }
                while (in.available() > 0) {
                    int b = in.read();
                    rst.add((byte) b);
                }
                Log.debug(rst.size() + " bytes read from DireWolf KISS port");
                Byte[] arr = new Byte[rst.size()];
                arr = rst.toArray(arr);

                // Convert Byte[] to byte[] (primitive type)
                byte[] primArr = new byte[arr.length];
                for (int i = 0; i < primArr.length; i++) {
                    primArr[i] = arr[i];
                }
                decodedData.add(primArr);
                Log.info("Received packet:\n" + HexadecimalUtils.hexDump(primArr));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        direWolfP.destroyForcibly(); // Close dire wolf process
    }

    public List<byte[]> getDecodedData() {
        return decodedData;
    }

    public void setDecoderPath(String decoderPath) {
        this.direWolfDir = decoderPath;
    }

    public void setDurationS(int durationS) {
        this.durationS = durationS;
    }

}
