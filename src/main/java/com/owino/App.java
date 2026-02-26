package com.owino;
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
import com.owino.conf.OSQAConfig;
import com.owino.core.OSQAModel;
import com.owino.core.OSQASession;
import com.owino.core.Result;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import com.owino.core.OSQAModel.OSQAModule;
import com.owino.core.OSQAModel.OSQATestSpec;
import com.owino.core.OSQAModel.OSQAOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class App {
    private Logger LOG = LoggerFactory.getLogger(App.class);
    void main() {
        IO.println("""
                 _____ _____  _____  ___
                |  _  /  ___||  _  |/ _ \\
                | | | \\ `--. | | | / /_\\ \\
                | | | |`--. \\| | | |  _  |
                \\ \\_/ /\\__/ /\\ \\/' / | | |
                 \\___/\\____/  \\_/\\_\\_| |_/
                
                Welcome to OSQA!
                """);
        var modulesFile = switch (OSQAConfig.loadModulesListFile()) {
            case Result.Success<String> success -> success.value();
            case Result.Failure failure -> throw new RuntimeException(failure.error());
        };
        var modules = switch (OSQAConfig.loadModules(modulesFile)) {
            case Result.Success<List<OSQAModule>> success -> success.value();
            case Result.Failure failure -> throw new RuntimeException(failure.error());
        };
        var session = new OSQASession(new Scanner(System.in));
        var selectedModule = switch (session.moduleSelection(modules)){
            case Result.Success<OSQAModule> success -> success.value();
            case Result.Failure failure -> throw new RuntimeException(failure.error());
        };
        IO.println("Selected Module -> " + selectedModule.name());
        List<OSQAOutcome> testSessionReport = new ArrayList<>();
        for (OSQAModel.OSQATestCase testCase : selectedModule.testCases()) {
            Optional<OSQATestSpec> optionalTestSpec = switch(OSQAConfig.loadTestCaseSpec(testCase)) {
                case Result.Success<OSQATestSpec> success -> Optional.of(success.value());
                case Result.Failure failure -> {
                    LOG.error(failure.error().getLocalizedMessage());
                    LOG.error("Moving to next test spec");
                    yield Optional.empty();
                }
            };
            if (optionalTestSpec.isEmpty()){
                LOG.error("This test case does not contain a valid test spec");
                LOG.error("Moving on to next test case");
                continue;
            }
            var testOutcomes = session.verifyQATestSpec(optionalTestSpec.get());
            testSessionReport.addAll(testOutcomes);
        }
        IO.println("QA Session Completed!");
        IO.println("QA SESSION RESULTS:");
        testSessionReport.forEach(outcome -> IO.println("""
                Verification: %s
                Verification Result: %s
                """.formatted(outcome.verification(),outcome.passedTest() ? "Passed Test ✅" : "Failed Test ❌")));

    }
}
