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

public class TimeUtils {

    private TimeUtils() {}

    public static void delayMillis(long millis) {
        long endTime = System.currentTimeMillis() + millis;
        while (System.currentTimeMillis() < endTime) {}
    }

    public static ZonedDateTime dateToZonedDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.of("UTC"));
    }

}
