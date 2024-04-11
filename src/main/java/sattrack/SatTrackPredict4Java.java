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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SatTrackPredict4Java implements SatTrack{

    //TODO: Make a class parameter to store satellite, set in constructor.
    private PassData satPassTimeToPass(SatelliteData satelliteData, SatPassTime satPassTime, PassPredictor passPredictor) {
        // Convert Predict4Java type SatPassTime to Pass (VE3RMC-control type required by main)
        List<SatPos> positions;
        try {
            positions = passPredictor.getPositions(satPassTime.getStartTime(), 5, 0, (int) ((satPassTime.getEndTime().getTime()-satPassTime.getStartTime().getTime())/1000/60)+1);
        } catch (SatNotFoundException e) {
            throw new RuntimeException(e);
        }

        List<Double> azProfile = new ArrayList<>();
        List<Double> elProfile = new ArrayList<>();
        List<Long> freqProfile = new ArrayList<>();

        for (SatPos p : positions) {
            azProfile.add(p.getAzimuth() / (Math.PI * 2.0) * 360);
            elProfile.add(p.getElevation() / (Math.PI * 2.0) * 360);
            try {
                freqProfile.add(passPredictor.getDownlinkFreq(satelliteData.getNominalDlFreqHz(), p.getTime()));
            } catch (SatNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        ZonedDateTime aos = dateToZonedDateTime(satPassTime.getStartTime());
        ZonedDateTime los = dateToZonedDateTime(satPassTime.getEndTime());

        return new PassData(satelliteData, aos, los, 5, azProfile, elProfile, freqProfile);
    }

    public PassData getNextPass(SatelliteData sat) {
        TLE tle = new TLE(sat.getTle());
        GroundStationPosition qth = new GroundStationPosition(44.23, -76.48, 95, "VE3RMC");
        ZonedDateTime currDate = ZonedDateTime.now(ZoneId.of("UTC"));
        PassPredictor passPredictor;
        List<SatPassTime> passes;
        SatPassTime nextSatPassTime;
        try {
            passPredictor = new PassPredictor(tle, qth);
            passes = passPredictor.getPasses(Date.from(currDate.toInstant()), 48, false);
            nextSatPassTime = passes.getFirst();
            //positions = passPredictor.getPositions(nextSatPassTime.getStartTime(), 5, 0, (int) ((nextSatPassTime.getEndTime().getTime()-nextSatPassTime.getStartTime().getTime())/1000/60)+1);
        } catch (SatNotFoundException e) {
            //TODO: Improve error handling
            throw new RuntimeException(e);
        }

        return satPassTimeToPass(sat, nextSatPassTime, passPredictor);
    }

    public List<PassData> getNext48hOfPasses(SatelliteData sat) {
        TLE tle = new TLE(sat.getTle());
        GroundStationPosition qth = new GroundStationPosition(44.23, -76.48, 95, "VE3RMC");
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

    private ZonedDateTime dateToZonedDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.of("UTC"));
    }

}
