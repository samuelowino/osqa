package com.owino.conf;
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
import com.owino.core.Result;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.owino.core.OSQAModel.OSQAModule;
import com.owino.core.OSQAModel.OSQATestCase;
import com.owino.core.OSQAModel.OSQATestSpec;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
public class OSQAConfig {
    public static final String MODULE_FILE = "data/modules.json";
    public static final String MODULE_DIR = "data";
    public static Result<Void> loadModulesListFile(){
        try {
            Path envFile = Paths.get(MODULE_DIR + "/" + "env.properties");
            if (Files.notExists(envFile)){
                var dir = Paths.get(MODULE_DIR);
                if (Files.notExists(dir)) Files.createDirectory(dir);
                envFile = Files.createFile(Paths.get("data" + "/" + "env.properties"));
                Files.writeString(envFile,"modules-file = " + OSQAConfig.MODULE_FILE);
            }
            return Result.success(null);
        } catch (IOException error) {
            return Result.failure("Failed to load modules list file: cause " + error.getLocalizedMessage());
        }
    }
    public static Result<List<OSQAModule>> loadModules(String modulesFile) {
        try {
            var json = Files.readString(Paths.get(modulesFile));
            var modules = new ObjectMapper().readValue(json, new TypeReference<List<OSQAModule>>() {});
            return Result.success(modules);
        } catch (IOException error){
            return Result.failure(error.getLocalizedMessage());
        }
    }
    public static Result<OSQATestSpec> loadTestCaseSpec(OSQATestCase testCase) {
        try {
            var specFile = Paths.get(testCase.specFile());
            var json = Files.readString(specFile);
            var testSpec = new ObjectMapper().readValue(json,OSQATestSpec.class);
            return Result.success(testSpec);
        } catch (IOException error){
            return Result.failure(error.getLocalizedMessage());
        }
    }
    public static String timestampedName(LocalDateTime createdTime, String ext) {
        var formater = DateTimeFormatter.ofPattern("yyyy-MM-dd-hh-mm-ss");
        return createdTime.format(formater) + "." + ext;
    }
}
