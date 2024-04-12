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

/**
 * Wrapper for the boolean type to manage success/failure result of methods in an easily readable format.
 */
public class ResultUtils {

    private final boolean status;

    /**
     * Instantiate through creation methods.
     * @param status True for success, false for failed.
     */
    private ResultUtils(boolean status) {
        this.status = status;
    }

    /**
     * Return an object indicating successful method result.
     * @return Successful result.
     */
    public static ResultUtils createSuccessfulResult() {
        return new ResultUtils(true);
    }

    /**
     * Return an object indicating failed method result.
     * @return Failed result.
     */
    public static ResultUtils createFailedResult() {
        return new ResultUtils(false);
    }

    /**
     * Create a result based on a boolean.
     * @param result True for success, false for failed.
     * @return Result based on input.
     */
    public static ResultUtils createResult(boolean result) {
        return new ResultUtils(result);
    }

    /**
     * Perform a AND operation on two ResultUtils objects.
     * @param other Other instance.
     * @return Result of AND of the status of current and other instance.
     */
    public ResultUtils and(ResultUtils other) {
        return new ResultUtils(this.status & other.isSuccessful());
    }

    /**
     * Perform a OR operation on two ResultUtils objects.
     * @param other Other instance.
     * @return Result of OR of the status of current and other instance.
     */
    public ResultUtils or(ResultUtils other) {
        return new ResultUtils(this.status | other.isSuccessful());
    }

    /**
     * Get the status as a boolean.
     * @return True for success, false for failed.
     */
    public boolean isSuccessful() {
        return status;
    }

}
