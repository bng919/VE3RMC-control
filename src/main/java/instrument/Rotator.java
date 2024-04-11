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

package instrument;

import utils.ResultUtils;

/**
 * Defines methods required for communication with an azimuth-elevation rotator.
 * All rotator models must implement this interface.
 */
public interface Rotator extends Instrument {

    /**
     * Get the current azimuth position in degrees from true north.
     * {@link Instrument#readInstrument()} should be called before this method to ensure most recent instrument values are available.
     * @return current azimuth.
     */
    int getAz();

    /**
     * Get the current elevation of the rotator in degrees.
     * {@link Instrument#readInstrument()} should be called before this method to ensure most recent instrument values are available.
     * @return current elevation.
     */
    int getEl();

    /**
     * Move the rotator to a new azimuth position.
     * @param az New azimuth position in degrees from true north.
     * @return the success/failure status of the operation.
     * @throws InterruptedException
     */
    ResultUtils goToAz(int az) throws InterruptedException;

    /**
     * Move the rotator to a new elevation position.
     * @param el New elevation position in degrees from the horizon.
     * @return the success/failure status of the operation.
     * @throws InterruptedException
     */
    ResultUtils goToEl(int el) throws InterruptedException;

    /**
     * Move the rotator to a new azimuth and elevation position.
     * @param az New azimuth position in degrees from true north.
     * @param el New elevation position in degrees from the horizon.
     * @return the success/failure status of the operation.
     * @throws InterruptedException
     */
    ResultUtils goToAzEl(int az, int el) throws InterruptedException;

}
