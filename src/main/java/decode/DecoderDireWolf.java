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

public class DecoderDireWolf implements Decoder {

    private List<byte[]> decodedData = new ArrayList<>();
    private String direWolfDir;
    private int kissPort = 8001;
    private int durationS;

    public DecoderDireWolf() {}

    public void run() {
        Log.debug("Running DireWolf decoder in thread " + Thread.currentThread().threadId());
        startDecoder();
    }

    public void startDecoder() {
        ProcessBuilder direWolfPb = new ProcessBuilder(direWolfDir + "\\direwolf", "-B " + ConfigurationUtils.getStrProperty("SAT_BAUD"));
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
        TimeUtils.delayMillis(1000); // Give direwolf time to set up

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

        long endTime = System.currentTimeMillis() + durationS*1000L;
        Log.debug("Monitoring DireWolf KISS port for " + durationS + "s.");
        durationLoop:
        while (System.currentTimeMillis() < endTime) {
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
        direWolfP.destroyForcibly();
    }

    public List<byte[]> getDecodedData() {
        return decodedData;
    }

    public void setDireWolfDir(String direWolfDir) {
        this.direWolfDir = direWolfDir;
    }

    public void setDurationS(int durationS) {
        this.durationS = durationS;
    }

}
