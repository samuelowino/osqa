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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import com.owino.core.Result;
import com.owino.core.OSQAConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import com.owino.desktop.settings.SettingDao;
import static org.assertj.core.api.Assertions.assertThat;
public class SettingDaoTest {
    @Test
    public void shouldGetDatabaseConnectionTest(){
        var result = SettingDao.connection();
        IO.println(result);
        assertThat(result instanceof Result.Success<Connection>).isTrue();
        var connection = ((Result.Success<Connection>) result).value();
        assertThat(connection).isNotNull();
    }
    @Test
    public void shouldSaveAppDirTest(){
        var path = OSQAConfig.MODULE_FILE;
        var file = new File(Paths.get(path).toUri());
        assertThat(file).isNotNull();
        var result = SettingDao.setAppDataDir(file);
        IO.println(result);
        assertThat(result instanceof Result.Success<Void>).isTrue();
    }
    @Test
    public void shouldFetchAppDataDirTest(){
        var path = OSQAConfig.MODULE_FILE;
        var file = new File(Paths.get(path).toUri());
        assertThat(file).isNotNull();
        var result = SettingDao.setAppDataDir(file);
        IO.println(result);
        assertThat(result instanceof Result.Success<Void>).isTrue();
        var fetchResult = SettingDao.getAppDataDir();
        assertThat(fetchResult instanceof Result.Success<Path>).isTrue();
        var persistedFile = ((Result.Success<Path>) fetchResult).value();
        assertThat(persistedFile).isNotNull();
    }
    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(OSQAConfig.OSQA_DB));
    }
}
