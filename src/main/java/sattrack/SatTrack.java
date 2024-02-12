package sattrack;

import data.Pass;
import data.Satellite;

import java.util.List;

public interface SatTrack {

    Pass getNextPass(Satellite sat);

    List<Pass> getNextTenPasses(Satellite sat);

}
