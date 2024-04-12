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

/**
 * Factory to select the appropriate implementation of {@link Decoder} based on the user specification in the
 * configuration file.
 */
public class DecoderFactory {

    /**
     * Private constructor to prevent instantiation (all other methods static).
     */
    private DecoderFactory() {}

    /**
     * Selects an {@link Decoder} implementation.
     * @param decoder ID name of the implementation.
     * @return An implementation of {@link Decoder}
     */
    public static Decoder createDecoder(String decoder) {
        if (decoder == null || decoder.isEmpty()) {
            throw new RuntimeException("DecoderFactory could not create instrument with null or empty string");
        } else if (decoder.equalsIgnoreCase("DireWolf")) {
            return new DecoderDireWolf();
        } else {
            throw new RuntimeException("DecoderFactory could not create instrument with ID " + decoder);
        }
    }

}
