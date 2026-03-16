package com.owino.desktop.settings;
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
import com.owino.core.OSQAConfig;
import com.owino.core.Result;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;
public class SettingDao {
    private static final String APP_DIR_SETTING_KEY = "app_dir_key";
    public static Result<Connection> connection(){
        try {
            var connection = DriverManager.getConnection("jdbc:sqlite:" + OSQAConfig.OSQA_DB);
            return Result.success(connection);
        } catch (SQLException exception){
            return Result.failure("Failed to open con to sqlite settings: " + exception.getLocalizedMessage());
        }
    }
    public static Result<Void> setAppDataDir(File path){
        try {
            if (connection() instanceof Result.Success<Connection> (Connection connection)){
                var schemaSql = """
                    CREATE TABLE IF NOT EXISTS APP_SETTINGS(
                        uuid TEXT PRIMARY KEY,
                        settings_key TEXT NOT NULL UNIQUE,
                        settings_value TEXT NOT NULL
                    );
                    """;
                var checkSettingExistsSql = """
                        SELECT COUNT(*) FROM APP_SETTINGS
                        WHERE settings_key = ?;
                        """;
                var deleteSettingByKey = """
                        DELETE FROM APP_SETTINGS
                        WHERE settings_key = ?;
                        """;
                var insertPathDir = """
                        INSERT INTO APP_SETTINGS VALUES(?,?,?);
                        """;
                var schemaStatement = connection.createStatement();
                schemaStatement.execute(schemaSql);
                var settingExistsStatement = connection.prepareStatement(checkSettingExistsSql);
                settingExistsStatement.setString(1, APP_DIR_SETTING_KEY);
                var resultSet = settingExistsStatement.executeQuery();
                var exists = false;
                if (resultSet.next())
                    exists = resultSet.getLong(1) > 0;
                if (exists){
                    var deleteExistingStatement = connection.prepareStatement(deleteSettingByKey);
                    deleteExistingStatement.setString(1,APP_DIR_SETTING_KEY);
                    deleteExistingStatement.executeUpdate();
                }
                var insertStatement = connection.prepareStatement(insertPathDir);
                insertStatement.setString(1,UUID.randomUUID().toString());
                insertStatement.setString(2,APP_DIR_SETTING_KEY);
                insertStatement.setString(3,path.getAbsolutePath());
                insertStatement.executeUpdate();
                connection.close();
                return Result.success(null);
            } else return Result.failure("Failed to create app dir. SQLite connect is unavailable!");
        } catch (SQLException ex){
            return Result.failure("Failed to create app dir: " + ex.getLocalizedMessage());
        }
    }
    public static Result<Path> getAppDataDir(){
        try {
            if (connection() instanceof Result.Success<Connection> (Connection connection)){
                var fetchSettingSql = """
                    SELECT settings_value FROM APP_SETTINGS
                    WHERE settings_key = ?;
                    """;
                var statement = connection.prepareStatement(fetchSettingSql);
                statement.setString(1,APP_DIR_SETTING_KEY);
                var resultSet = statement.executeQuery();
                if (resultSet.next()){
                    var result = Result.success(Paths.get(resultSet.getString(1)));
                    connection.close();
                    return result;
                } else {
                    connection.close();
                    return Result.failure("Failed to fetch app dir: cause missing value");
                }
            } else return Result.failure("Failed to set app dir: cause UNKNOWN error");
        } catch (SQLException ex){
            return Result.failure("Failed to fetch app dir: " + ex.getLocalizedMessage());
        }
    }
}
