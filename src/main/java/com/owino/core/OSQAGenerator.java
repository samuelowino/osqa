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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import com.owino.core.OSQAModel.OSQAModule;
import com.owino.core.OSQAModel.OSQATestCase;
import com.owino.core.OSQAModel.OSQATestSpec;
import com.owino.core.OSQAModel.OSQAVerification;
public class OSQAGenerator {
    private final int FAIL_SAFE_LIMIT = 100;
    private final Scanner scanner;
    private int failSafe;
    public OSQAGenerator(Scanner inputReader){ scanner = inputReader; }
    public OSQAGenerator(){ scanner = new Scanner(System.in); }
    public List<OSQAModel.OSQAModule> collectModules() {
        List<OSQAModule> modules = new ArrayList<>();
        var done = false;
        do {
            String moduleTitle;
            String description;
            int priority;
            do {
                IO.println("Set module name:");
                moduleTitle = scanner.nextLine();
            } while(moduleTitle.isBlank());
            do {
                IO.println("Set module description:");
                description = scanner.nextLine();
            } while (description.isBlank());
            do {
                IO.println("""
                        Set module priority:
                        Critical -> 0
                        Medium -> 1
                        Non-Critical -> 2
                        """);
                priority = scanner.nextInt();
            } while (priority < 0 || priority >= 3);
            var priorityName = switch (priority){
                case 0 -> "Critical";
                case 1 -> "Medium";
                case 2 -> "Non-Critical";
                default -> throw new RuntimeException("Unknown priority type");
            };
            var testCases = collectTestCases(moduleTitle);
            var module = new OSQAModule(UUID.randomUUID().toString(),moduleTitle,description,priorityName,testCases);
            modules.add(module);
            IO.println("Add another module?: y/n");
            done = !scanner.nextLine().equalsIgnoreCase("y");
            IO.println("---");
            failSafe++;
        } while(!done && failSafe < FAIL_SAFE_LIMIT);
        return modules;
    }
    public List<OSQATestCase> collectTestCases(String moduleName) {
        IO.println("Create test cases for this module:[" + moduleName + "]");
        List<OSQATestCase> testCases = new ArrayList<>();
        var done = false;
        do {
            var testCaseTitle = "";
            var specFile = new StringBuilder(UUID.randomUUID().toString())
                    .append("-")
                    .append(moduleName);
            do {
                IO.println("Set test case testCaseTitle:");
                testCaseTitle = scanner.nextLine();
            } while(testCaseTitle.isBlank());
            specFile.append("-").append(testCaseTitle).append(".json");
            testCases.add(new OSQATestCase(UUID.randomUUID().toString(),testCaseTitle,specFile.toString()));
            IO.println("Add another test case?: y/n");
            done = !scanner.nextLine().equalsIgnoreCase("y");
            failSafe++;
        } while(!done && failSafe < FAIL_SAFE_LIMIT);
        return testCases;
    }
    public OSQATestSpec collectTestCaseSpecs() {
        String userAction;
        List<OSQAModel.OSQAVerification> verifications = new ArrayList<>();
        do {
            IO.print("User action:");
            userAction = scanner.nextLine();
        } while (userAction.isBlank());
        var isDone = false;
        IO.println("Verification of expected behaviour (Multiple verification steps are supported):");
        int order = 0;
        do {
            String description;
            IO.println("Step [" + order + "]");
            description = scanner.nextLine();
            verifications.add(new OSQAVerification(order,description));
            IO.println("Add another verification step: y/n");
            isDone = !scanner.nextLine().equalsIgnoreCase("y");
            order++;
            failSafe++;
        } while (!isDone && failSafe < FAIL_SAFE_LIMIT);
        return new OSQATestSpec(UUID.randomUUID().toString(),userAction,verifications);
    }
}
