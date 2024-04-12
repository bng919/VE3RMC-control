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

package audio;

import utils.ResultUtils;

/**
 * Defines methods required to configure and begin audio recording. Extends {@link Runnable} to allow audio recording
 * to take place in a separate thread.
 */
public interface AudioRecord extends Runnable {

    /**
     * Record audio at sample rate and for duration specified by calling {@link AudioRecord#setSampleRate(int)} and {@link AudioRecord#setRecordDurationS(int)}
     * @return The success/failure status of the operation.
     */
    ResultUtils recordAudio();

    /**
     * Set how long {@link AudioRecord#recordAudio()} will record for.
     * @param recordDurationS duration of the recording in seconds.
     */
    void setRecordDurationS(int recordDurationS);

    /**
     * Set the sample rate to be used by {@link AudioRecord#recordAudio()}.
     * @param sampleRate sample rate in hertz.
     */
    void setSampleRate(int sampleRate);

}
