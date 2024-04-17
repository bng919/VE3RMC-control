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

/**
 * Factory to select the appropriate implementation of {@link Transceiver} or {@link Rotator} based on the user specification in the configuration file.
 */
public class InstrumentFactory {

    /**
     * Private constructor to prevent instantiation (all other methods static).
     */
    private InstrumentFactory() {}

    /**
     * Select a {@link Rotator} implementation.
     * @param rotator ID name of the rotator implementation.
     * @return an implementation of {@link Rotator}
     * @throws InterruptedException
     */
    public static Rotator createRotator(String rotator) throws InterruptedException {
        if (rotator == null || rotator.isEmpty()) {
            throw new RuntimeException("InstrumentFactory could not create instrument with null or empty string");
        } else if (rotator.equalsIgnoreCase("RotatorGS232B")) {
            return new RotatorGS232B();
        } else if (rotator.equalsIgnoreCase("RotatorRot2Prog")) {
            return new RotatorRot2ProgImpl();
        } else if (rotator.equalsIgnoreCase("StubRotator")) {
            return new StubRotator();
        } else {
            throw new RuntimeException("InstrumentFactory could not create instrument with ID " + rotator);
        }
    }

    /**
     * Select a {@link Transceiver} implementation.
     * @param transceiver ID name of the transceiver implementation.
     * @return an implementation of {@link Transceiver}
     * @throws InterruptedException
     */
    public static Transceiver createTransceiver(String transceiver) throws InterruptedException {
        if (transceiver == null || transceiver.isEmpty()) {
            throw new RuntimeException("InstrumentFactory could not create instrument with null or empty string");
        } else if (transceiver.equalsIgnoreCase("TransceiverIC9100")) {
            return new TransceiverIC9100();
        } else if (transceiver.equalsIgnoreCase("StubTransceiver")) {
            return new StubTransceiver();
        }  else {
            throw new RuntimeException("InstrumentFactory could not create instrument with ID " + transceiver);
        }
    }

}
