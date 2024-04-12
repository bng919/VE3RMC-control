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

/**
 * Encapsulates data required to describe a single satellite including its ID (name as a String), TLE set,
 * uplink frequency, and downlink frequency.
 */
public class SatelliteData {

    private String id;
    private String[] tle;
    private long nominalDlFreqHz;
    private long nominalUlFreqHz; //Not currently used, for future expansion

    /**
     * Create a satellite object.
     * @param id Name of the satellite (or NORAD ID as a String).
     * @param tle Array of Strings containing the lines of the TLE.
     * @param nominalDlFreqHz Satellite nominal transmission frequency in hertz.
     * @param nominalUlFreqHz Satellite nominal receive frequency (not currently used) in hertz.
     */
    public SatelliteData(String id, String[] tle, long nominalDlFreqHz, long nominalUlFreqHz) {
        this.id = id;
        this.tle = tle;
        this.nominalDlFreqHz = nominalDlFreqHz;
        this.nominalUlFreqHz = nominalUlFreqHz;
    }

    /**
     * Get the ID String of the satellite.
     * @return ID
     */
    public String getId() {
        return id;
    }

    /**
     * Get the TLE of the satellite.
     * @return TLE
     */
    public String[] getTle() {
        return tle;
    }

    /**
     * Get the satellites nominal DL frequency in hertz.
     * @return Downlink frequency in Hz.
     */
    public long getNominalDlFreqHz() {
        return nominalDlFreqHz;
    }

    /**
     * Get the satellites nominal UL frequency in hertz.
     * @return Uplink frequency in Hz.
     */
    public long getNominalUlFreqHz() {
        return nominalUlFreqHz;
    }

    /**
     * Generate a description of a {@link SatelliteData} object.
     * @return Description.
     */
    @Override
    public String toString() {
        return "Satellite: " + this.id;
    }

}
