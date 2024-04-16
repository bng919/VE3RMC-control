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

package sattrack;

import com.github.amsacode.predict4java.GroundStationPosition;
import com.github.amsacode.predict4java.PassPredictor;
import com.github.amsacode.predict4java.SatNotFoundException;
import com.github.amsacode.predict4java.SatPassTime;
import com.github.amsacode.predict4java.SatPos;
import com.github.amsacode.predict4java.TLE;
import data.PassData;
import data.SatelliteData;
import utils.ConfigurationUtils;
import utils.TimeUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class for interfacing with Predict via the {@link com.github.amsacode.predict4java} package.
 */
public class SatTrackPredict4Java implements SatTrack {

    //TODO: Make a class parameter to store satellite, set in constructor.

    /**
     * Convert Predict4Java type {@link SatPassTime} to {@link PassData} (VE3RMC-control type required by main)
     * @param satelliteData Satellite being tracked.
     * @param satPassTime Input object of type {@link SatPassTime}.
     * @param passPredictor Associated {@link PassPredictor} object.
     * @return Fully populated {@link PassData} object.
     */
    private PassData satPassTimeToPass(SatelliteData satelliteData, SatPassTime satPassTime, PassPredictor passPredictor) {
        // Step 1: Generate a list of SatPos type at the desired increment (5 s) from AOS to LOS+1 min
        List<SatPos> positions;
        try {
            positions = passPredictor.getPositions(satPassTime.getStartTime(), 5, 0, (int) ((satPassTime.getEndTime().getTime()-satPassTime.getStartTime().getTime())/1000/60)+1);
        } catch (SatNotFoundException e) {
            throw new RuntimeException(e);
        }

        List<Double> azProfile = new ArrayList<>();
        List<Double> elProfile = new ArrayList<>();
        List<Long> freqProfile = new ArrayList<>();

        // Step 2: Iterate through SatPos list and add azimuth, elevation, frequency to new Lists
        for (SatPos p : positions) {
            azProfile.add(p.getAzimuth() / (Math.PI * 2.0) * 360); // Convert to degrees
            elProfile.add(p.getElevation() / (Math.PI * 2.0) * 360); // Convert to degrees
            try {
                freqProfile.add(passPredictor.getDownlinkFreq(satelliteData.getNominalDlFreqHz(), p.getTime()));
            } catch (SatNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        ZonedDateTime aos = TimeUtils.dateToZonedDateTime(satPassTime.getStartTime());
        ZonedDateTime los = TimeUtils.dateToZonedDateTime(satPassTime.getEndTime());

        // Step 3: Return new PassData object
        return new PassData(satelliteData, aos, los, 5, azProfile, elProfile, freqProfile);
    }

    public PassData getNextPass(SatelliteData sat) {
        TLE tle = new TLE(sat.getTle());
        GroundStationPosition qth = new GroundStationPosition(ConfigurationUtils.getDoubleProperty("GS_LAT"),
                ConfigurationUtils.getDoubleProperty("GS_LON"),
                ConfigurationUtils.getDoubleProperty("GS_ELE"),
                ConfigurationUtils.getStrProperty("GS_CALL"));
        ZonedDateTime currDate = ZonedDateTime.now(ZoneId.of("UTC"));
        PassPredictor passPredictor;
        List<SatPassTime> passes;
        SatPassTime nextSatPassTime;
        try {
            passPredictor = new PassPredictor(tle, qth);
            passes = passPredictor.getPasses(Date.from(currDate.toInstant()), 48, false);
            nextSatPassTime = passes.getFirst();
        } catch (SatNotFoundException e) {
            //TODO: Improve error handling
            throw new RuntimeException(e);
        }

        return satPassTimeToPass(sat, nextSatPassTime, passPredictor);
    }

    public List<PassData> getNext48hOfPasses(SatelliteData sat) {
        TLE tle = new TLE(sat.getTle());
        GroundStationPosition qth = new GroundStationPosition(ConfigurationUtils.getDoubleProperty("GS_LAT"),
                ConfigurationUtils.getDoubleProperty("GS_LON"),
                ConfigurationUtils.getDoubleProperty("GS_ELE"),
                ConfigurationUtils.getStrProperty("GS_CALL"));
        ZonedDateTime currDate = ZonedDateTime.now(ZoneId.of("UTC"));
        PassPredictor passPredictor;
        List<SatPassTime> passes;
        try {
            passPredictor = new PassPredictor(tle, qth);
            passes = passPredictor.getPasses(Date.from(currDate.toInstant()), 48, false);
        } catch (SatNotFoundException e) {
            //TODO: Improve error handling
            throw new RuntimeException(e);
        }
        List<PassData> result = new ArrayList<>();
        for (SatPassTime p : passes) {
            result.add(satPassTimeToPass(sat, p, passPredictor));
        }
        return result;
    }

}
