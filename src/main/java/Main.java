import audio.AudioRecord;
import audio.AudioRecorderFactory;
import data.Pass;
import data.Satellite;
import decode.Decoder;
import decode.DecoderFactory;
import instrument.InstrumentFactory;
import instrument.Rotator;
import instrument.Transceiver;
import sattrack.SatTrack;
import sattrack.SatTrackFactory;
import utils.Log;
import utils.TLEHelper;
import utils.Time;
import utils.enums.Verbosity;
import utils.PropertyHelper;

import java.io.FileReader;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Properties;

public class Main {

    public static void main(String [] args) throws InterruptedException {


        new Log(PropertyHelper.getStrProperty("LOG_PATH"), Verbosity.valueOf(PropertyHelper.getStrProperty("LOG_LEVEL")));

        Rotator rotator = InstrumentFactory.createRotator(PropertyHelper.getStrProperty("ROTATOR_MODEL"));
        Transceiver transceiver = InstrumentFactory.createTransceiver(PropertyHelper.getStrProperty("TRANSCEIVER_MODEL"));
        AudioRecord audio = AudioRecorderFactory.createAudioRecord(PropertyHelper.getStrProperty("RECORDER_MODEL"));
        Decoder dec = DecoderFactory.createDecoder(PropertyHelper.getStrProperty("DECODER_MODEL"));
        SatTrack satTrack = SatTrackFactory.createSatTrack(PropertyHelper.getStrProperty("SATELLITE_TRACK_MODEL"));

        if (!transceiver.testConnect().isSuccessful() || !rotator.testConnect().isSuccessful()) {
            throw new RuntimeException("Instrument setup failed!");
        }

        String[] testTle = TLEHelper.fileToStrArray(PropertyHelper.getStrProperty("TLE_PATH"));

        //TODO: Satellite shouldn't need currAz, currAl, or visible to be instantiated
        Satellite sat = new Satellite("TEST", testTle, 0, 0, false, PropertyHelper.getIntProperty("SAT_DL_FREQ_HZ"), 146000000);
        Pass pass = satTrack.getNextPass(sat);
        List<Double> azProfile = pass.getAzProfile();
        List<Double> elProfile = pass.getElProfile();

        ZonedDateTime setupTime = pass.getAos().minusMinutes(1);
        Log.debug("Waiting for " + setupTime + " before rotator setup");
        while (ZonedDateTime.now(ZoneId.of("UTC")).isBefore(setupTime)) {

        }

        // To rotator initial position
        int initAz = azProfile.getFirst().intValue();
        int initEl = elProfile.getFirst().intValue();
        Log.debug("Moving rotator to initial position Az " + initAz + ", El " + initEl);
        rotator.goToAzEl(initAz, initEl);
        Log.debug("Set transceiver to nominal DL freq " + PropertyHelper.getIntProperty("SAT_DL_FREQ_HZ"));
        transceiver.setFrequency(PropertyHelper.getIntProperty("SAT_DL_FREQ_HZ"));


        audio.setSampleRate(PropertyHelper.getIntProperty("RECORDER_SAMPLE_RATE"));
        audio.setRecordDurationS(pass.getDurationS());

        dec.setDireWolfDir(PropertyHelper.getStrProperty("DECODER_PATH"));
        dec.setDurationS(pass.getDurationS());

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
            if (stopTime-startTime < pass.getProfileStepS()*1000L) {
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
