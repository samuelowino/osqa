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
import com.owino.core.OSQAGenerator;
import com.owino.core.OSQAModel.OSQATestSpec;
import com.owino.core.OSQAModel.OSQAVerification;
import com.owino.core.OSQAModel.OSQATestCase;
import com.owino.core.OSQAModel.OSQAModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Scanner;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(MockitoExtension.class)
public class OSQAGeneratorTest {
    @InjectMocks
    private OSQAGenerator generator;
    @Mock
    private Scanner scanner;
    private final String CONTINUE_FALG = "y";
    private final String BREAK_FLAG = "n";
    @Test
    public void shouldCollectModulesListTest(){
        var testCaseTitle = "Test Case 001";
        var moduleName = "Test Module";
        var description = "This is a test module";
        when(scanner.nextLine())
                .thenReturn(moduleName)
                .thenReturn(description)
                .thenReturn(testCaseTitle)
                .thenReturn(BREAK_FLAG)
                .thenReturn(CONTINUE_FALG)
                .thenReturn(moduleName)
                .thenReturn(description)
                .thenReturn(testCaseTitle)
                .thenReturn(BREAK_FLAG)
                .thenReturn(BREAK_FLAG);
        when(scanner.nextInt())
                .thenReturn(2)
                .thenReturn(1);
        List<OSQAModule> modules = generator.collectModules();
        assertThat(modules).isNotEmpty();
        assertThat(modules.size()).isEqualTo(2);
        assertThat(modules.getFirst().name()).isEqualTo(moduleName);
    }
    @Test
    public void shouldCollectModuleTestCasesTest(){
        var testCaseTitle = "Test Case 001";
        var testCaseTitle2 = "Test Case 002";
        var testCaseTitle3 = "Test Case 003";
        var moduleTitle = "Test Module 001";
        when(scanner.nextLine())
                .thenReturn(testCaseTitle)
                .thenReturn(CONTINUE_FALG)
                .thenReturn(testCaseTitle2)
                .thenReturn(CONTINUE_FALG)
                .thenReturn(testCaseTitle3)
                .thenReturn(BREAK_FLAG);
        List<OSQATestCase> testCases = generator.collectTestCases(moduleTitle);
        assertThat(testCases).isNotEmpty();
        assertThat(testCases.size()).isEqualTo(3);
        assertThat(testCases.getFirst()).isNotNull();
        assertThat(testCases.getFirst().title()).isNotEmpty();
        assertThat(testCases.getFirst().title()).isEqualTo(testCaseTitle);
    }
    @Test
    public void shouldCollectTestSpecsTest(){
       var action = "On Device B, mark the 'Team Sync' task as complete.";
       var order0Verification = new OSQAVerification(0,"On Device B, the task is marked complete and a new instance appears with the correct future date.");
       var order1Verification = new OSQAVerification(1,"On Device A, after a sync/refresh, the original task is marked complete and the new instance appears with the correct future date.");
       var verifications = List.of(order0Verification, order1Verification);
       when(scanner.nextLine())
               .thenReturn(action)
               .thenReturn(order0Verification.description())
               .thenReturn(CONTINUE_FALG)
               .thenReturn(order1Verification.description())
               .thenReturn(BREAK_FLAG);
       OSQATestSpec testSpecs = generator.collectTestCaseSpecs();
       assertThat(testSpecs).isNotNull();
       assertThat(testSpecs.action()).isEqualTo(action);
       assertThat(testSpecs.verifications()).isNotEmpty();
       assertThat(testSpecs.verifications().size()).isEqualTo(verifications.size());
       assertThat(testSpecs.verifications().getFirst().order()).isEqualTo(order0Verification.order());
       assertThat(testSpecs.verifications().getFirst().description()).isEqualTo(order0Verification.description());
       assertThat(testSpecs.verifications().getLast().order()).isEqualTo(order1Verification.order());
       assertThat(testSpecs.verifications().getLast().description()).isEqualTo(order1Verification.description());
    }
}
