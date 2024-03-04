package sattrack;

import com.github.amsacode.predict4java.GroundStationPosition;
import com.github.amsacode.predict4java.PassPredictor;
import com.github.amsacode.predict4java.SatNotFoundException;
import com.github.amsacode.predict4java.SatPassTime;
import com.github.amsacode.predict4java.SatPos;
import com.github.amsacode.predict4java.TLE;
import data.Pass;
import data.Satellite;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SatTrackPredict4Java implements SatTrack{

    public Pass getNextPass(Satellite sat) {
        TLE tle = new TLE(sat.getTle());
        GroundStationPosition qth = new GroundStationPosition(44.23, -76.48, 95, "VE3RMC");
        ZonedDateTime currDate = ZonedDateTime.now(ZoneId.of("UTC"));
        PassPredictor passPredictor;
        List<SatPassTime> passes;
        SatPassTime nextSatPassTime;
        List<SatPos> positions;
        try {
            passPredictor = new PassPredictor(tle, qth);
            passes = passPredictor.getPasses(Date.from(currDate.toInstant()), 48, false);
            nextSatPassTime = passes.getFirst();
            positions = passPredictor.getPositions(nextSatPassTime.getStartTime(), 5, 0, (int) ((nextSatPassTime.getEndTime().getTime()-nextSatPassTime.getStartTime().getTime())/1000/60)+1);
        } catch (SatNotFoundException e) {
            //TODO: Improve error handling
            throw new RuntimeException(e);
        }

        //TODO: Profile data class?
        List<Double> azProfile = new ArrayList<>();
        List<Double> elProfile = new ArrayList<>();
        List<Long> freqProfile = new ArrayList<>();

        for (SatPos p : positions) {
            azProfile.add(p.getAzimuth() / (Math.PI * 2.0) * 360);
            elProfile.add(p.getElevation() / (Math.PI * 2.0) * 360);
            try {
                freqProfile.add(passPredictor.getDownlinkFreq(sat.getNominalDlFreqHz(), p.getTime()));
            } catch (SatNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        ZonedDateTime aos = dateToZonedDateTime(nextSatPassTime.getStartTime());
        ZonedDateTime los = dateToZonedDateTime(nextSatPassTime.getEndTime());

        return new Pass(sat, aos, los, 5, azProfile, elProfile, freqProfile);
    }

    public List<Pass> getNextTenPasses(Satellite sat) {
        //TODO: Implement
        return null;
    }

    private ZonedDateTime dateToZonedDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.of("UTC"));
    }

}
