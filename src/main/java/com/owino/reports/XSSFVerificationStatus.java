package com.owino.reports;
/*
 * Copyright (C) 2026 Samuel Owino
 *
 * OSQA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OSQA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OSQA.  If not, see <https://www.gnu.org/licenses/>.
 */
import java.util.Arrays;
public enum XSSFVerificationStatus {
    PASSED("PASSED", "Passed all verifications"),
    VERIFICATIONS_FAILED("VERIFICATIONS FAILED", "All Verifications Failed (List failed verifications in notes)"),
    PENDING("PENDING", "Pending Test");
    final String displayName;
    final String description;
    XSSFVerificationStatus(String name, String description){
        this.displayName = name;
        this.description = description;
    }
    static String[] descriptions() {
        return Arrays.stream(values())
                .map (value -> value.displayName.concat("\t").concat(value.description))
                .toArray(String[]::new);
    }
}
