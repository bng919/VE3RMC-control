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

package utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Utility to manage delays and time conversions.
 */
public class TimeUtils {

    /**
     * Private constructor to prevent instantiation. All methods static.
     */
    private TimeUtils() {}

    /**
     * Wait for a set amount of time.
     * @param millis Time to wait in milliseconds.
     */
    public static void delayMillis(long millis) {
        long endTime = System.currentTimeMillis() + millis;
        while (System.currentTimeMillis() < endTime) {}
    }

    /**
     * Covert {@link Date} object to an instance of {@link ZonedDateTime} assuming UTC.
     * @param date Instance of {@link Date} to be converted.
     * @return Corresponding {@link ZonedDateTime} object in UTC.
     */
    public static ZonedDateTime dateToZonedDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.of("UTC"));
    }

}
