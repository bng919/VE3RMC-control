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

package utils.enums;

/**
 * Enum to define verbosity levels for {@link utils.Log}. Each level as an associated integer to allow ease of
 * comparison of levels.
 */
public enum Verbosity {
    DEBUG(3),
    INFO(2),
    WARN(1),
    ERROR(0);

    private int printLevel;

    Verbosity(int printLevel) {
        this.printLevel = printLevel;
    }

    public boolean isEqOrHigher(Verbosity other) {
        return this.printLevel >= other.printLevel;
    }

}