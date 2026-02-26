package com.owino.core;
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
import java.util.List;
import java.util.regex.Pattern;
public sealed interface OSQAModel {
    record OSQAModule(
            String uuid,
            String name,
            String description,
            String priority,
            List<OSQATestCase> testCases
    ) implements OSQAModel {
        public OSQAModule {
            var uuidPattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");
            var error = new StringBuilder();
            if (uuid.isBlank() || !uuidPattern.matcher(uuid).find()) error.append("Invalid module uuid\n");
            if (name.isBlank()) error.append("Module name cannot be blank\n");
            if (description.isBlank()) error.append("Module description cannot be blank\n");
            if (priority.isBlank()) error.append("Module priority cannot be blank\n");
            if (testCases.isEmpty()) error.append("Test cases cannot be empty");
            if (!error.isEmpty()) throw new OSQAValidationException(error.toString());
        }
    }
    record OSQATestCase(
            String uuid,
            String title,
            String specFile
    ) implements OSQAModel {
        public OSQATestCase {
            var uuidPattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");
            var error = new StringBuilder();
            if (uuid.isBlank() || !uuidPattern.matcher(uuid).find()) error.append("Invalid test case uuid:").append(uuid).append("\n");
            if (title.isBlank()) error.append("Model title cannot be blank\n");
            if (specFile.isBlank()) error.append("Spec file name cannot be blank");
            if (!error.isEmpty()) throw new OSQAValidationException(error.toString());
        }
    }
    record OSQATestSpec(
            String uuid,
            String title,
            String action,
            List<OSQAVerification> verifications
    ) implements OSQAModel {
        public OSQATestSpec {
            var uuidPattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");
            var error = new StringBuilder();
            if (uuid.isBlank() || !uuidPattern.matcher(uuid).find()) error.append("Invalid test spec uuid\n");
            if (title.isBlank()) error.append("Test Spec title cannot be blank\n");
            if (action.isBlank()) error.append("Test spec action cannot be blank\n");
            if (verifications.isEmpty()) error.append("Test cases must be verified. Include verifications");
            if (!error.isEmpty()) throw new OSQAValidationException(error.toString());
        }
    }
    record OSQAVerification(
            Integer order,
            String description
    ) implements OSQAModel {
        public OSQAVerification {
            var error = new StringBuilder();
            if (order < 1) error.append("Verification order must start with 1. < 1 order is not supported");
            if (description.isBlank()) error.append("Verification order description is required");
            if (!error.isEmpty()) throw new OSQAValidationException(error.toString());
        }
    }
    record OSQAOutcome(
            String testSpecUuid,
            OSQAVerification verification,
            boolean passedTest
    ) implements OSQAModel {
        public OSQAOutcome {
            var uuidPattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");
            var error = new StringBuilder();
            if (testSpecUuid.isBlank() || !uuidPattern.matcher(testSpecUuid).find()) error.append("Invalid test spec uuid\n");
            if (verification == null) error.append("Affected verification cannot be null");
            if (!error.isEmpty()) throw new OSQAValidationException(error.toString());
        }
    }
}
