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
import utils.enums.Modulation;

/**
 * Defines methods required for communication with a transceiver.
 * All transceiver models must implement this interface.
 */
public interface Transceiver extends Instrument {

    /**
     * Get the current frequency setting in Hz.
     * {@link Instrument#readInstrument()} should be called before this method to ensure most recent instrument values are available.
     * @return current frequency.
     */
    long getFrequencyHz();

    /**
     * Get the current modulation setting.
     * {@link Instrument#readInstrument()} should be called before this method to ensure most recent instrument values are available.
     * @return current modulation setting.
     */
    Modulation getModulation();

    /**
     * Set a new frequency value on the transceiver.
     * @param freqHz the frequency in hertz to set on the transceiver.
     * @return the success/failure status of the operation.
     * @throws InterruptedException
     */
    ResultUtils setFrequency(long freqHz) throws InterruptedException;

    /**
     * Set a new modulation setting on the transceiver.
     * @param mod The {@link Modulation} type to set.
     * @return the success/failure status of the operation.
     * @throws InterruptedException
     */
    ResultUtils setModulation(Modulation mod) throws InterruptedException;

}
