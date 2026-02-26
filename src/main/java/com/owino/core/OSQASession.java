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
import java.util.*;
import com.owino.core.OSQAModel.OSQAOutcome;
import com.owino.core.OSQAModel.OSQATestSpec;
import com.owino.core.OSQAModel.OSQAVerification;
import com.owino.core.OSQAModel.OSQAModule;
public class OSQASession {
    public Scanner scanner;
    public OSQASession(Scanner inputReader){
        scanner = inputReader;
    }
    public Result<OSQAModule> moduleSelection(List<OSQAModel.OSQAModule> moduleOptions){
        if (moduleOptions.isEmpty()) return Result.failure("Module options list is empty");
        IO.println("Select module from available options:");
        var index = 0;
        Map<Integer, OSQAModel.OSQAModule> selection = new HashMap<>();
        for (OSQAModel.OSQAModule moduleOption : moduleOptions) {
            selection.put(index,moduleOption);
            index++;
        }
        selection.forEach((selectionIndex,module) -> IO.println(selectionIndex + " -> " + module.name() + ":" + module.description()));
        var moduleIndex = scanner.nextInt();
        return Result.success(selection.get(moduleIndex));
    }
    public List<OSQAOutcome> verifyQATestSpec(OSQATestSpec testSpec) {
        IO.println("Title -> " + testSpec.title());
        IO.println("Action -> " + testSpec.action());
        IO.println("""
                    Confirm expected behaviour:
                    0 -> Failed verification
                    1 -> Passed verification
                    """);
        List<OSQAOutcome> outcomes = new ArrayList<>();
        for (OSQAVerification verification : testSpec.verifications()) {
            IO.println(verification.description());
            var passed = scanner.nextInt();
            outcomes.add(new OSQAOutcome(testSpec.uuid(),verification,passed == 1));
        }
        return outcomes;
    }
}
