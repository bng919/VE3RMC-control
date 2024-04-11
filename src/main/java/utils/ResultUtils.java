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

public class ResultUtils {

    private final boolean status;

    private ResultUtils(boolean status) {
        this.status=status;
    }

    public static ResultUtils createSuccessfulResult() {
        return new ResultUtils(true);
    }

    public static ResultUtils createFailedResult() {
        return new ResultUtils(false);
    }

    public static ResultUtils createResult(boolean result) {
        return new ResultUtils(result);
    }

    public ResultUtils and(ResultUtils other) {
        return new ResultUtils(this.status & other.isSuccessful());
    }

    public boolean isSuccessful() {
        return status;
    }

}
