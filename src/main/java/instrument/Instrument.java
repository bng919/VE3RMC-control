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
 * Highest level interface to for communication with hardware.
 */
public interface Instrument {

    /**
     * Reads the current parameters from the instrument.
     * @return the read status.
     * @throws InterruptedException
     */
    ResultUtils readInstrument() throws InterruptedException;

    /**
     * Test the connection with the instrument.
     * @return the connection status.
     * @throws InterruptedException
     */
    ResultUtils testConnect() throws InterruptedException;

}
