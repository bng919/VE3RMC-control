package sattrack;

import data.PassData;
import data.SatelliteData;

import java.util.List;

public interface SatTrack {

    PassData getNextPass(SatelliteData sat);

    List<PassData> getNextTenPasses(SatelliteData sat);

}
