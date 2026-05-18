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
import com.owino.core.OSQAModel;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import java.util.List;
import java.util.Comparator;
public class OSQAPaginatedResultTest {
    @Test
    public void shouldFindPaginatedResultTest(){
        List<TestDatum> data = List.of(
                new TestDatum(LocalDate.of(2020, 1, 15), 45.2d),
                new TestDatum(LocalDate.of(2020, 3, 20), 52.7d),
                new TestDatum(LocalDate.of(2020, 6, 10), 58.3d),
                new TestDatum(LocalDate.of(2020, 9, 5), 63.1d),
                new TestDatum(LocalDate.of(2021, 1, 12), 71.5d),
                new TestDatum(LocalDate.of(2021, 4, 18), 78.9d),
                new TestDatum(LocalDate.of(2021, 7, 22), 84.2d),
                new TestDatum(LocalDate.of(2021, 10, 30), 89.6d),
                new TestDatum(LocalDate.of(2022, 2, 8), 94.3d),
                new TestDatum(LocalDate.of(2022, 5, 14), 97.8d),
                new TestDatum(LocalDate.of(2022, 8, 19), 102.4d),
                new TestDatum(LocalDate.of(2023, 1, 25), 110.2d)
        );
        var sortedData = data.stream().sorted(Comparator.comparing(TestDatum::date)).toList();
        int page = 0;
        int pageSize = 4;
        var paginatedResult = OSQAModel.OSQAPaginatedResult.paginatedResult(sortedData,page,pageSize);
        assertThat(paginatedResult).isNotNull();
        assertThat(paginatedResult.hasNext()).isTrue();
        assertThat(paginatedResult.hasPrevious()).isFalse();
        assertThat(paginatedResult.result()).isNotEmpty();
        assertThat(paginatedResult.result().size()).isEqualTo(4);
    }
    record TestDatum(LocalDate date, Double amount) {}
}
