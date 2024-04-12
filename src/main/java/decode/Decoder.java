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

package decode;

import java.util.List;

/**
 * Defines methods required to configure and execute an external decoder tool. Extends {@link Runnable} to
 * allow monitoring and communication with the decoder tool to take place in a separate thread. This interface is
 * designed to function with external decoders that independently monitor an audio input and decoder in real time.
 */
public interface Decoder extends Runnable {

    /**
     * Activate and monitor the decoder for a duration specified by calling {@link Decoder#setDecoderPath(String)}.
     */
    void runDecoder();

    /**
     * Return a list of packets decoded by the decoder.
     * @return List of byte arrays where each array is a single packet.
     */
    List<byte[]> getDecodedData();

    /**
     * Set the path to the decoder executable.
     * @param decoderPath Path to decoder executable.
     */
    void setDecoderPath(String decoderPath);

    /**
     * Set the duration the decoder should monitor the audio input for.
     * @param durationS Duration in seconds to monitor for data.
     */
    void setDurationS(int durationS);

}
