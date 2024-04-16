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

import data.PassData;
import data.SatelliteData;
import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SatTrackPredict4JavaTest {

    SatelliteData sat;

    String[] testTle = {"ISS", "1 25544U 98067A   24043.51459821  .00024146  00000-0  43257-3 0  9999",
            "2 25544  51.6404 218.1523 0001911 230.6128 226.8660 15.49814353439063"};
    @BeforeClass
    public void setup() {
        sat = Mockito.mock(SatelliteData.class);
        Mockito.when(sat.getId()).thenReturn("TESTSAT");
        Mockito.when(sat.getTle()).thenReturn(testTle);
        Mockito.when(sat.getNominalDlFreqHz()).thenReturn(140000000L);
    }

    @Test
    public void testGetNextPass() {
        /*
         * This test case does not assert a result.
         * To verify, update the testTle, then verify out printed output agrees with a 3rd party tracking program.
         */
        SatTrack tracker = new SatTrackPredict4Java();
        PassData nextPass = tracker.getNextPass(sat);
        System.out.println(nextPass);
    }

}