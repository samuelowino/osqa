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
import com.owino.core.OSQASession;
import com.owino.core.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.List;
import java.util.Scanner;
import org.mockito.junit.jupiter.MockitoExtension;
import com.owino.core.OSQAModel.OSQAModule;
import com.owino.core.OSQAModel.OSQAOutcome;
import com.owino.core.OSQAModel.OSQATestSpec;
import com.owino.core.OSQAModel.OSQAVerification;
import tools.jackson.databind.ObjectMapper;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(MockitoExtension.class)
public class OSQASessionTest {
    @InjectMocks
    private OSQASession session;
    @Mock
    private Scanner scanner;
    @Test
    public void shouldAllowModuleSelectionTest(){
        when(scanner.nextInt()).thenReturn(1);
        Result<OSQAModule> selectionResult = session.moduleSelection(TestData.moduleOptions);
        assertThat(selectionResult instanceof Result.Success<OSQAModule>).isTrue();
        var selectedModule = ((Result.Success<OSQAModule>) selectionResult).value();
        assertThat(selectedModule).isNotNull();
        assertThat(selectedModule.uuid()).isEqualTo(TestData.firstIndexModule.uuid());
        assertThat(selectedModule.name()).isEqualTo(TestData.firstIndexModule.name());
        assertThat(selectedModule.description()).isEqualTo(TestData.firstIndexModule.description());
        assertThat(selectedModule.priority()).isEqualTo(TestData.firstIndexModule.priority());
        assertThat(selectedModule.testCases().size()).isEqualTo(TestData.firstIndexModule.testCases().size());
    }
    @Test
    public void shouldRenderSpecForIndividualCaseTest(){
        when(scanner.nextInt()).thenReturn(0);
        var testSpec = new OSQATestSpec(
                "b723ba02-26a4-46d6-845b-5a7643df4eeb",
                "Task Completion Sync",
                "On Device B, mark the 'Team Sync' task as complete.",
                List.of(
                        new OSQAVerification(1,"On Device B, the task is marked complete and a new instance appears with the correct future date."),
                        new OSQAVerification(2,"On Device A, after a sync/refresh, the original task is marked complete and the new instance appears with the correct future date.")
                ));
        List<OSQAOutcome> outcomes = session.verifyQATestSpec(testSpec);
        assertThat(outcomes).isNotEmpty();
        assertThat(outcomes.size()).isEqualTo(2);
        assertThat(outcomes.getFirst().passedTest()).isFalse();
        assertThat(outcomes.getFirst().passedTest()).isFalse();
        assertThat(outcomes.getFirst().verification()).isNotNull();
        assertThat(outcomes.getLast().verification()).isNotNull();
        assertThat(outcomes.getFirst().verification().description()).isNotEmpty();
        assertThat(outcomes.getLast().verification().description()).isNotEmpty();
        assertThat(outcomes.getLast().verification().order()).isGreaterThan(0);
        assertThat(outcomes.getFirst().verification().order()).isGreaterThan(0);
        IO.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(outcomes));
    }
}
