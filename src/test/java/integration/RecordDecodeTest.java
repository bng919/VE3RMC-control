package integration;

import audio.AudioRecordJavaxSoundSampled;
import decode.DecoderDireWolf;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.Log;
import utils.enums.Verbosity;

import java.util.List;

public class RecordDecodeTest {

    @BeforeClass
    public void setup() {
        new Log(".\\logs\\", Verbosity.DEBUG);
    }

    @Test
    public void testRecordDecode() {
        AudioRecordJavaxSoundSampled audio = new AudioRecordJavaxSoundSampled();
        audio.setSampleRate(48000);
        audio.setRecordDurationS(20);
        DecoderDireWolf dec = new DecoderDireWolf();
        dec.setDurationS(20);
        dec.setDireWolfDir("C:\\Users\\benng\\Documents\\Uni\\School Work\\Fifth Year\\Fall\\ENPH455\\Code\\direwolf-1.7.0-9807304_i686");

        Thread audioThread = new Thread(audio);
        Thread decoderThread = new Thread(dec);

        audioThread.start();
        decoderThread.start();
        try {
            audioThread.join();
            decoderThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        List<byte[]> data = dec.getDecodedData();
        for (byte[] d : data) {
            Log.storeDecodedData(d);
        }

    }

}
