package decode;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.Log;
import utils.enums.Verbosity;

import java.util.List;

import static org.testng.Assert.*;

public class DecoderDireWolfTest {

    @BeforeClass
    public void setup() {
        new Log(".\\logs\\", Verbosity.DEBUG);
    }
    @Test
    public void testStartDecoder() {
        DecoderDireWolf dec = new DecoderDireWolf(20, "C:\\Users\\benng\\Documents\\Uni\\School Work\\Fifth Year\\Fall\\ENPH455\\Code\\direwolf-1.7.0-9807304_i686");
        dec.startDecoder();
        List<byte[]> data = dec.getDecodedData();
        for (byte[] d : data) {
            Log.storeDecodedData(d);
        }
    }

}