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

package data;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates data required to describe a single pass of a satellite including the AOS/LOS times, pass duration,
 * lists of values for azimuth/elevation heading and post correction frequencies throughout the pass.
 */
public class PassData {

    private SatelliteData sat;
    private ZonedDateTime aos;
    private ZonedDateTime los;
    private int durationS;
    private int profileSampleIntervalS;
    private List<Double> azProfile;
    private List<Double> elProfile;
    private List<Long> dlFreqHzAdjProfile;

    /**
     * Create a pass object.
     * @param sat {@link SatelliteData} object of the satellite that will be passing.
     * @param aos Time the satellite will rise above the horizon.
     * @param los Time the satellite will fall below the horizon.
     * @param profileSampleIntervalS Sample interval in seconds of {@link PassData#azProfile}, {@link PassData#elProfile}, and {@link PassData#dlFreqHzAdjProfile}.
     * @param azProfile Azimuth heading throughout the pass, sampled at {@link PassData#profileSampleIntervalS}.
     * @param elProfile Elevation heading throughout the pass, sampled at {@link PassData#profileSampleIntervalS}.
     * @param dlFreqHzAdjProfile Corrected frequency throughout the pass, sampled at {@link PassData#dlFreqHzAdjProfile}.
     */
    public PassData(SatelliteData sat, ZonedDateTime aos, ZonedDateTime los, int profileSampleIntervalS, List<Double> azProfile, List<Double> elProfile, List<Long> dlFreqHzAdjProfile) {
        this.sat = sat;
        this.aos = aos;
        this.los = los;
        this.durationS = Math.toIntExact(Duration.between(aos, los).getSeconds());
        this.profileSampleIntervalS = profileSampleIntervalS;
        this.azProfile = azProfile;
        this.elProfile = elProfile;
        this.dlFreqHzAdjProfile = dlFreqHzAdjProfile;
    }

    /**
     * Get the satellite of the pass.
     * @return {@link SatelliteData} of the satellite passing.
     */
    public SatelliteData getSat() {
        return sat;
    }

    /**
     * Get the acquisition of satellite time.
     * @return AOS
     */
    public ZonedDateTime getAos() {
        return aos;
    }

    /**
     * Get the loss of satellite time.
     * @return LOS
     */
    public ZonedDateTime getLos() {
        return los;
    }

    /**
     * Get the duration of the pass as computed by LOS-AOS.
     * @return Duration of the pass in seconds.
     */
    public int getDurationS() {
        return durationS;
    }

    /**
     * Get a list of azimuth values throughout the pass.
     * @return Azimuth profile.
     */
    public List<Double> getAzProfile() {
        return azProfile;
    }

    /**
     * Get a list of elevation values throughout the pass.
     * @return Elevation profile.
     */
    public List<Double> getElProfile() {
        return elProfile;
    }

    /**
     * Get a list of the satellites transmitter frequency after correction for Doppler shift.
     * @return Frequency profile in hertz.
     */
    public List<Long> getDlFreqHzAdjProfile() {
        return dlFreqHzAdjProfile;
    }

    /**
     * Get the sample interval for the profiles.
     * @return Sample interval in seconds.
     */
    public int getProfileSampleIntervalS() {
        return profileSampleIntervalS;
    }

    /**
     * Generate description of a {@link PassData} object.
     * @return Description.
     */
    @Override
    public String toString() {
        return this.sat + "\nStart time " + this.aos
                + "\nEnd time   " + this.los
                + "\nStart Az: " + this.azProfile.getFirst() + ", End Az: " + this.azProfile.getLast() + "\nMax El: " + Collections.max(this.elProfile)
                + "\n================================================================";
    }

}
