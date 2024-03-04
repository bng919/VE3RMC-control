import audio.AudioRecordWin;
import data.Pass;
import data.Satellite;
import decode.DecoderDireWolf;
import instrument.Rotator;
import instrument.StubRotatorGS232B;
import instrument.StubTransceiverIC9100;
import instrument.Transceiver;
import sattrack.SatTrack;
import sattrack.SatTrackPredict4Java;
import utils.Log;
import utils.Time;
import utils.enums.Verbosity;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class Main {

    public static void main(String [] args) throws InterruptedException {

        new Log(".\\logs\\", Verbosity.DEBUG);

        String[] testTle = {"ISS", "1 56184U 23054G   24063.54587157  .00022452  00000-0  70076-3 0  9993",
                "2 56184  97.3825 321.2161 0007773 178.1199 182.0074 15.33188012 49913"};

        //TODO: Satellite shouldn't need currAz, currAl, or visible to be instantiated
        Satellite sat = new Satellite("ISS", testTle, 0, 0, false, 145000000, 146000000);

        SatTrack satTrack = new SatTrackPredict4Java();
        Pass pass = satTrack.getNextPass(sat);
        List<Double> azProfile = pass.getAzProfile();
        List<Double> elProfile = pass.getElProfile();

        /*Rotator rotator = InstrumentFactory.createRotator("RotatorGS232B");*/
        Rotator rotator = new StubRotatorGS232B();

        Transceiver transceiver = new StubTransceiverIC9100();

        ZonedDateTime setupTime = pass.getAos().minusMinutes(1);
        Log.debug("Waiting for " + setupTime + " before rotator setup");
        while (ZonedDateTime.now(ZoneId.of("UTC")).isBefore(setupTime)) {

        }

        // To rotator initial position
        int initAz = azProfile.getFirst().intValue();
        int initEl = elProfile.getFirst().intValue();
        Log.debug("Moving rotator to initial position Az " + initAz + ", El " + initEl);
        rotator.goToAzEl(initAz, initEl);


        AudioRecordWin audio = new AudioRecordWin(pass.getDurationS(), 44100);
        DecoderDireWolf dec = new DecoderDireWolf(pass.getDurationS(), "C:\\Users\\benng\\Documents\\Uni\\School Work\\Fifth Year\\Fall\\ENPH455\\Code\\direwolf-1.7.0-9807304_i686");

        Thread audioThread = new Thread(audio);
        Thread decoderThread = new Thread(dec);


        Log.debug("Waiting for AOS at" + pass.getAos());
        while (ZonedDateTime.now(ZoneId.of("UTC")).isBefore(pass.getAos())) {

        }

        audioThread.start();
        decoderThread.start();

        for (int i = 0; i < azProfile.size(); i++) {
            Log.debug("Moving to position Az " + azProfile.get(i).intValue() + ", El " + elProfile.get(i).intValue());
            rotator.goToAzEl(azProfile.get(i).intValue(), elProfile.get(i).intValue());
            transceiver.setFrequency(pass.getDlFreqHzAdjProfile().get(i));
            Time.delayMillis(pass.getProfileStepS()*1000L);
        }

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
