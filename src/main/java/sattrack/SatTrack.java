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

import java.util.List;

/**
 * Defines methods required to interface with an external orbital prediction tool.
 */
public interface SatTrack {

    /**
     * Determine the next pass from the current time of a satellite defined by a {@link SatelliteData} object.
     * @param sat The satellite to determine the next pass of.
     * @return {@link PassData} describing the next pass.
     */
    PassData getNextPass(SatelliteData sat);

    /**
     * Determine the passes occurring in the next 48h of a satellite defined by a {@link SatelliteData} object.
     * @param sat The satellite to determine the next pass of.
     * @return List of {@link PassData}.
     */
    List<PassData> getNext48hOfPasses(SatelliteData sat);

}
