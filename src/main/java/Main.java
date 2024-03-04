import audio.AudioRecordWin;
import data.Pass;
import data.Satellite;
import decode.DecoderDireWolf;
import instrument.Rotator;
import instrument.RotatorGS232B;
import instrument.Transceiver;
import instrument.TransceiverIC9100;
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

        String[] testTle = {"TEST", "1 39469U 13072H   24062.30883059 +.00013669 +00000+0 +82190-3 0    15",
                "2 39469 120.5021 125.8073 0193146 215.6961 143.1050 14.97020769549865"};

        //TODO: Satellite shouldn't need currAz, currAl, or visible to be instantiated
        Satellite sat = new Satellite("TEST", testTle, 0, 0, false, 437479000, 146000000);

        SatTrack satTrack = new SatTrackPredict4Java();
        Pass pass = satTrack.getNextPass(sat);
        List<Double> azProfile = pass.getAzProfile();
        List<Double> elProfile = pass.getElProfile();

        /*Rotator rotator = InstrumentFactory.createRotator("RotatorGS232B");*/
        Rotator rotator = new RotatorGS232B();

        Transceiver transceiver = new TransceiverIC9100();

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
        DecoderDireWolf dec = new DecoderDireWolf(pass.getDurationS(), "C:\\Amateur Radio\\Direwolf\\Direwolf-Executable\\direwolf-1.7.0-9807304_i686");

        Thread audioThread = new Thread(audio);
        Thread decoderThread = new Thread(dec);


        Log.debug("Waiting for AOS at " + pass.getAos());
        while (ZonedDateTime.now(ZoneId.of("UTC")).isBefore(pass.getAos())) {

        }

        audioThread.start();
        decoderThread.start();

        for (int i = 0; i < azProfile.size(); i++) {
            long startTime = System.currentTimeMillis();
            Log.debug("Moving to position Az " + azProfile.get(i).intValue() + ", El " + elProfile.get(i).intValue());
            rotator.goToAzEl(azProfile.get(i).intValue(), elProfile.get(i).intValue());
            Log.debug("Set frequency to " + pass.getDlFreqHzAdjProfile().get(i));
            transceiver.setFrequency(pass.getDlFreqHzAdjProfile().get(i));
            long stopTime = System.currentTimeMillis();
            if (stopTime-startTime < pass.getDurationS()*1000L) {
                Time.delayMillis((pass.getProfileStepS()*1000L) - (stopTime-startTime));
            } else {
                Log.warn("TIME MISALIGNMENT: Setting rotator and transceiver took longer than specified delay.\n" +
                        "Rotator position may lag desired position!");
            }
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
