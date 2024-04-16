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

import audio.AudioRecord;
import audio.AudioRecorderFactory;
import data.PassData;
import data.SatelliteData;
import decode.Decoder;
import decode.DecoderFactory;
import instrument.*;
import sattrack.SatTrack;
import sattrack.SatTrackFactory;
import utils.Log;
import utils.TLEUtils;
import utils.TimeUtils;
import utils.enums.Verbosity;
import utils.ConfigurationUtils;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Main program.
 */
public class Main {

    /**
     * Main method of program.
     * @param args Args are unused.
     * @throws InterruptedException
     */
    public static void main(String [] args) throws InterruptedException {

        /*
         * Step 1: Setup and configure instrument (and external software) models.
         */
        new Log(ConfigurationUtils.getStrProperty("LOG_PATH"), Verbosity.valueOf(ConfigurationUtils
                .getStrProperty("LOG_LEVEL")));

        Rotator rotator = InstrumentFactory.createRotator(ConfigurationUtils.getStrProperty("ROTATOR_MODEL"));
        Transceiver transceiver = InstrumentFactory.createTransceiver(ConfigurationUtils
                .getStrProperty("TRANSCEIVER_MODEL"));

        List<Instrument> physicalInstruments = new ArrayList<>();
        physicalInstruments.add(rotator);
        physicalInstruments.add(transceiver);
        for (Instrument instrument : physicalInstruments) { // Verify connection status for all hardware instruments
            if (!instrument.testConnect().isSuccessful() || !instrument.readInstrument().isSuccessful()) {
                throw new RuntimeException("Instrument setup failed for " + instrument);
            }
        }

        AudioRecord audio = AudioRecorderFactory.createAudioRecord(ConfigurationUtils
                .getStrProperty("RECORDER_MODEL"));
        Decoder dec = DecoderFactory.createDecoder(ConfigurationUtils.getStrProperty("DECODER_MODEL"));
        SatTrack satTrack = SatTrackFactory.createSatTrack(ConfigurationUtils
                .getStrProperty("SATELLITE_TRACK_MODEL"));

        String[] tle = TLEUtils.fileToStrArray(ConfigurationUtils.getStrProperty("TLE_PATH"));
        SatelliteData sat = new SatelliteData(tle[0], tle, ConfigurationUtils
                .getIntProperty("SAT_DL_FREQ_HZ"), 146000000); // Uplink frequency currently unused.

        /*
         * Step 2: Predict next passes of satellite, get user input
         */
        List<PassData> next48h = satTrack.getNext48hOfPasses(sat);
        Log.noPrefix("=========================== Next 48h ===========================");
        for (int i = next48h.size()-1; i >= 0; i--) {
            Log.noPrefix("ID (" + i + ")");
            Log.noPrefix(next48h.get(i).toString());
        }

        Scanner input = new Scanner(System.in);
        Log.noPrefix("Enter ID number of pass to record (comma seperated to select multiple): ");
        String userSel = input.nextLine();
        input.close();
        List<Integer> passIDs = new ArrayList<>();
        for (String s : userSel.split(",")) {
            passIDs.add(Integer.parseInt(s));
        }

        Log.info("Pass ID " + userSel + " selected by user.");

        int passCount = 1; // Track number of passes completed

        for (int passId : passIDs) { // Loop over all passes selected by user.
            Log.info("Configuring for pass " + passCount + " of " + passIDs.size());
            PassData pass = next48h.get(passId);
            List<Double> azProfile = pass.getAzProfile();
            List<Double> elProfile = pass.getElProfile();

            /*
             * Step 3: Wait until 1 min before pass.
             */
            Log.info("Tracking satellite " + sat.getId());
            Log.info("Set to record pass beginning at " + pass.getAos() + ", ending at " + pass.getLos());
            ZonedDateTime setupTime = pass.getAos().minusMinutes(1);
            Log.info("Waiting until AOS minus 1 min to start setup...");
            Log.debug("Waiting for " + setupTime + " before rotator setup");
            while (ZonedDateTime.now(ZoneId.of("UTC")).isBefore(setupTime)) {

            }

            /*
             * Step 4: Configure transceiver, set initial rotator position, and configure threads for the audio recorder
             * and decoder tools.
             */
            int initAz = azProfile.getFirst().intValue();
            int initEl = elProfile.getFirst().intValue();
            Log.debug("Moving rotator to initial position Az " + initAz + ", El " + initEl);
            rotator.goToAzEl(initAz, initEl);
            Log.debug("Set transceiver to nominal DL freq " + ConfigurationUtils.getIntProperty("SAT_DL_FREQ_HZ"));
            transceiver.setFrequency(ConfigurationUtils.getIntProperty("SAT_DL_FREQ_HZ"));

            audio.setSampleRate(ConfigurationUtils.getIntProperty("RECORDER_SAMPLE_RATE"));
            audio.setRecordDurationS(pass.getDurationS());
            dec.setDecoderPath(ConfigurationUtils.getStrProperty("DECODER_PATH"));
            dec.setDurationS(pass.getDurationS());
            Thread audioThread = new Thread(audio);
            Thread decoderThread = new Thread(dec);

            /*
             * Step 5: Wait for pass to begin
             */
            Log.debug("Waiting for AOS at " + pass.getAos());
            while (ZonedDateTime.now(ZoneId.of("UTC")).isBefore(pass.getAos())) {

            }

            /*
             * Step 6: During pass: begin audio recording/decoding, update rotator and transceiver throughout.
             */
            audioThread.start();
            decoderThread.start();

            for (int i = 0; i < azProfile.size(); i++) {
                long startTime = System.currentTimeMillis();
                rotator.goToAzEl(azProfile.get(i).intValue(), elProfile.get(i).intValue());
                transceiver.setFrequency(pass.getDlFreqHzAdjProfile().get(i));
                long stopTime = System.currentTimeMillis();
                if (stopTime-startTime < pass.getProfileSampleIntervalS()*1000L) {
                    TimeUtils.delayMillis((pass.getProfileSampleIntervalS()*1000L) - (stopTime-startTime));
                } else {
                    Log.warn("TIME MISALIGNMENT: Setting rotator and transceiver took longer than specified delay.\n" +
                            "Rotator position may lag desired position!");
                }
            }

            /*
             * Step 7: Clean up: Once profiles have been completed, join audio and decoder threads (i.e. wait for them to
             * finish if they haven't already), then store decoded data.
             */
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
}
