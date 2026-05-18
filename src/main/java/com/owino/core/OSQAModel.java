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
import java.nio.file.Files;
import java.util.List;
import java.nio.file.Path;
import java.util.regex.Pattern;
public sealed interface OSQAModel {
    record OSQAFeatureLegacy(
            String uuid,
            String productUuid,
            String name,
            String description,
            String priority,
            List<OSQATestCase> testCases
    ) implements OSQAModel {
        public OSQAFeatureLegacy {
            var uuidPattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");
            var error = new StringBuilder();
            if (uuid.isBlank() || !uuidPattern.matcher(uuid).find()) error.append("Invalid feature uuid\n");
            if (productUuid.isBlank() || !uuidPattern.matcher(uuid).find()) error.append("Invalid product uuid\n");
            if (name.isBlank()) error.append("Feature name cannot be blank\n");
            if (description.isBlank()) error.append("Feature description cannot be blank\n");
            if (priority.isBlank()) error.append("Feature priority cannot be blank\n");
            if (testCases.isEmpty()) error.append("Test cases cannot be empty");
            if (!error.isEmpty()) throw new OSQAValidationException(error.toString());
        }
    }
    record OSQAFeature(
            String uuid,
            String productUuid,
            String name,
            String description,
            String priority,
            String filePath,
            List<OSQATestCase> testCases
    ) implements OSQAModel {
        public OSQAFeature {
            var uuidPattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");
            var error = new StringBuilder();
            if (uuid.isBlank() || !uuidPattern.matcher(uuid).find()) error.append("Invalid feature uuid\n");
            if (productUuid.isBlank() || !uuidPattern.matcher(uuid).find()) error.append("Invalid product uuid\n");
            if (name.isBlank()) error.append("Feature name cannot be blank\n");
            if (description.isBlank()) error.append("Feature description cannot be blank\n");
            if (priority.isBlank()) error.append("Feature priority cannot be blank\n");
            if (testCases.isEmpty()) error.append("Test cases cannot be empty");
            if (filePath.isEmpty()) error.append("Feature file cannot be empty");
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
            String action,
            List<OSQAVerification> verifications
    ) implements OSQAModel {
        public OSQATestSpec {
            var uuidPattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");
            var error = new StringBuilder();
            if (uuid.isBlank() || !uuidPattern.matcher(uuid).find()) error.append("Invalid test spec uuid\n");
            if (action.isBlank()) error.append("Test spec action cannot be blank\n");
            if (verifications.isEmpty()) error.append("Test cases must be verified. Include verifications");
            if (!error.isEmpty()) throw new OSQAValidationException(error.toString());
        }
    }
    record OSQAVerification(
            String uuid,
            Integer order,
            String description,
            boolean verificationStatus
    ) implements OSQAModel {
        public OSQAVerification {
            var uuidPattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");
            var error = new StringBuilder();
            if (uuid.isBlank() || !uuidPattern.matcher(uuid).find()) error.append("Invalid verification uuid\n");
            if (order < 0) error.append("Verification order must start with 0. < 0 order is not supported");
            if (description.isBlank()) error.append("Verification description is required");
            if (!error.isEmpty()) throw new OSQAValidationException(error.toString());
        }
        public OSQAVerification(String uuid, Integer order, String description){
            this(uuid,order,description,false);
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
    record OSQAFilesDirTuple(String fileName, Path absPath) implements OSQAModel{
        public OSQAFilesDirTuple {
            var error = new StringBuilder();
            if (fileName.isBlank()) error.append("Invalid file name");
            if (absPath == null) error.append("Abs Path cannot be null");
            if (!error.isEmpty()) throw new OSQAValidationException(error.toString());
        }
    }
    record OSQAProduct(
            String uuid,
            String name,
            String target,
            Path projectDir
    ) implements OSQAModel {
        public OSQAProduct {
            var uuidPattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");
            var error = new StringBuilder();
            if (uuid.isBlank() || !uuidPattern.matcher(uuid).find()) error.append("Invalid product uuid\n");
            if (!error.isEmpty()) throw new OSQAValidationException(error.toString());
            if (name.isBlank()) error.append("Product name cannot be blank");
            if (target.isBlank()) error.append("Platform target cannot be blank");
            if (!Files.exists(projectDir)) error.append("Invalid project directory: Folder does not exist");
            if (!error.isEmpty()) throw new OSQAValidationException(error.toString());
        }
    }
    record OSQAPaginatedResult<T>(
           List<T> result,
           int currentPage,
           int pageSize,
           long totalItems,
           long totalPages,
           boolean hasNext,
           boolean hasPrevious,
           int startIndex,
           int endIndex
    ) {
        public static <E> OSQAPaginatedResult<E> paginatedResult(List<E> data, int page, int pageSize){
            if (data.isEmpty()) return new OSQAPaginatedResult<>(data, page, pageSize, 0, 0, false, false, 0,0);
            if (page < 1) page = 1;
            if (pageSize < 1) pageSize = 1;
            int totalItems = data.size();
            int totalPages = (int) Math.ceil((double) totalItems / pageSize);
            if (page > totalPages && totalPages > 0) page = totalPages;
            int startIndex = (page - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalItems);
            var result = data.subList(startIndex, endIndex);
            var hasNext = page < totalPages;
            var hasPrevious = page > 1;
            return new OSQAPaginatedResult<>(
                    result,
                    page,
                    pageSize,
                    totalItems,
                    totalPages,
                    hasNext,
                    hasPrevious,
                    startIndex,
                    endIndex);
        }
    }
    enum FeaturesSortOrder {
        BY_NAME("By Feature Name"),
        BY_VERIFICATION_PROGRESS("By Verification Progress");
        private final String name;
        FeaturesSortOrder(String label){
            this.name = label;
        }
        public String getName(){
            return this.name;
        }
        public static FeaturesSortOrder fromName(String name) {
             if (name.contentEquals(BY_NAME.getName())) {
                 return BY_NAME;
             } else if (name.contentEquals(BY_VERIFICATION_PROGRESS.getName())) {
                 return BY_VERIFICATION_PROGRESS;
             } else {
                 throw new AssertionError("Invalid sort order option " + name);
             }
        }
    }
}
