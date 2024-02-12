package sattrack;

import com.github.amsacode.predict4java.GroundStationPosition;
import com.github.amsacode.predict4java.PassPredictor;
import com.github.amsacode.predict4java.SatNotFoundException;
import com.github.amsacode.predict4java.SatPassTime;
import com.github.amsacode.predict4java.TLE;
import data.Pass;
import data.Satellite;

import java.util.Date;
import java.util.List;

public class SatTrackPredict4Java implements SatTrack{



    public Pass getNextPass(Satellite sat) {

        TLE tle = new TLE(sat.getTle());
        GroundStationPosition qth = new GroundStationPosition(44.23, -76.48, 95, null);
        PassPredictor passPredictor;
        try {
            passPredictor = new PassPredictor(tle, qth);
        } catch (SatNotFoundException e) {
            //TODO: Improve error handling
            throw new RuntimeException(e);
        }

        Date currDate = new Date();

        List<SatPassTime> passes;
        long freq;

        try {
            passes = passPredictor.getPasses(currDate, 48, false);
            //freq = passPredictor.getDownlinkFreq(sat.getNominalDlFreqHz(), currDate);
        } catch (SatNotFoundException e) {
            throw new RuntimeException(e);
        }


        return null;
    }

    public List<Pass> getNextTenPasses(Satellite sat) {
        return null;
    }
}
