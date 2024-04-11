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

public class FrequencyUtils {

    private FrequencyUtils() {}

    public static boolean isUHF(long freq) {
        return freq >= 144*MHzToHz && freq <= 147.990*MHzToHz; // See RAC 2m band plan
    }

    public static boolean isVHF(long freq) {
        return freq >= 430.025*MHzToHz && freq <= 450*MHzToHz; // See RAC 70cm band plan
    }

    public static final double kHzToHz = 1e3;
    public static final double HzTokHz = 1e-3;
    public static final double MHzToHz = 1e6;
    public static final double HzToMHz = 1e-6;

}
