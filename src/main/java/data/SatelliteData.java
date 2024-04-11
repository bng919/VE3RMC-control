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

public class SatelliteData {

    private String id;
    private String[] tle;
    private long nominalDlFreqHz;
    private long nominalUlFreqHz;


    public SatelliteData(String id, String[] tle, long nominalDlFreqHz, long nominalUlFreqHz) {
        this.id = id;
        this.tle = tle;
        this.nominalDlFreqHz = nominalDlFreqHz;
        this.nominalUlFreqHz = nominalUlFreqHz;
    }

    public String getId() {
        return id;
    }

    public String[] getTle() {
        return tle;
    }

    public long getNominalDlFreqHz() {
        return nominalDlFreqHz;
    }

    public long getNominalUlFreqHz() {
        return nominalUlFreqHz;
    }

    @Override
    public String toString() {
        return "Satellite: " + this.id;
    }
}
