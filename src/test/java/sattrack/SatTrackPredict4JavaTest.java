package sattrack;

import data.Pass;
import data.Satellite;
import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SatTrackPredict4JavaTest {

    Satellite sat;

    String[] testTle = {"ISS", "1 25544U 98067A   24043.51459821  .00024146  00000-0  43257-3 0  9999",
            "2 25544  51.6404 218.1523 0001911 230.6128 226.8660 15.49814353439063"};
    @BeforeClass
    public void setup() {
        sat = Mockito.mock(Satellite.class);
        Mockito.when(sat.getId()).thenReturn("TESTSAT");
        Mockito.when(sat.getTle()).thenReturn(testTle);
        Mockito.when(sat.getNominalDlFreqHz()).thenReturn(140000000L);
    }

    @Test
    public void testGetNextPass() {
        SatTrack tracker = new SatTrackPredict4Java();
        Pass nextPass = tracker.getNextPass(sat);
        System.out.println(nextPass);
    }

}