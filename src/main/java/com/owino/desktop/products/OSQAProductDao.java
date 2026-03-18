package com.owino.desktop.products;
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
import java.util.List;
import java.nio.file.Path;
import java.util.ArrayList;
import java.nio.file.Paths;
import java.sql.Connection;
import com.owino.core.Result;
import java.sql.SQLException;
import java.sql.DriverManager;
import com.owino.core.OSQAConfig;
import com.owino.core.OSQAModel.OSQAProduct;
public class OSQAProductDao {
    public static Result<Connection> connection(){
        try {
            var binDirResult = OSQAConfig.resolveBinDir();
            if (binDirResult instanceof Result.Failure<Path>(Throwable error)){
                return Result.failure(error.getLocalizedMessage());
            }
            if (binDirResult instanceof Result.Success<Path>(Path binDirPath)){
                var sqliteUrl = "jdbc:sqlite:" + binDirPath.toAbsolutePath() + File.separator + OSQAConfig.OSQA_DB;
                var connection = DriverManager.getConnection(sqliteUrl);
                return Result.success(connection);
            }
            return Result.failure("Failed to open sqlite connection");
        } catch (SQLException exception){
            var error = "Failed to open con to sqlite products db: " + exception.getLocalizedMessage();
            IO.println(error);
            return Result.failure(error);
        }
    }
    public static Result<Void> saveProduct(OSQAProduct product){
        try {
            var insertSql = """
                INSERT INTO Products VALUES(?,?,?,?);
                """;
            var resultConn = connection();
            if (!(resultConn instanceof Result.Success<Connection> (Connection connection))){
                return Result.failure("Failed to open database connection");
            }
            var insertStatement = connection.prepareStatement(insertSql);
            insertStatement.setString(1,product.uuid());
            insertStatement.setString(2,product.name());
            insertStatement.setString(3,product.target());
            insertStatement.setString(4,product.projectDir().toAbsolutePath().toString());
            insertStatement.execute();
            return Result.success(null);
        } catch (SQLException error){
            return Result.failure("Failed to save product: " + error.getLocalizedMessage());
        }
    }
    public static Result<Void> updateProduct(OSQAProduct product){
        try {
            var updateSql = """
                UPDATE Products
                SET name = ?, target = ?, projectDir = ?
                WHERE
                uuid = ?
                """;
            if (!(connection() instanceof Result.Success<Connection>(Connection connection))){
                return Result.failure("Failed to open database connection");
            }
            var updateStatement = connection.prepareStatement(updateSql);
            updateStatement.setString(1,product.name());
            updateStatement.setString(2,product.target());
            updateStatement.setString(3,product.projectDir().toAbsolutePath().toString());
            updateStatement.setString(4,product.uuid());
            return updateStatement.executeUpdate() == 1 ? Result.success(null) : Result.failure("Failed to update product, database mismatch");
        } catch (SQLException failure){
            return Result.failure(failure.getLocalizedMessage());
        }
    }
    public static Result<List<OSQAProduct>> listProducts(){
        try {
            var listSql = """
                SELECT * FROM Products;
                """;
            if (!(connection() instanceof Result.Success<Connection> (Connection connection)))
                return Result.failure("Failed to open database connection");
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery(listSql);
            List<OSQAProduct> products = new ArrayList<>();
            while (resultSet.next()){
                var uuid = resultSet.getString(1);
                var name = resultSet.getString(2);
                var target = resultSet.getString(3);
                var projectDir = Paths.get(resultSet.getString(4));
                var product = new OSQAProduct(uuid,name,target,projectDir);
                products.add(product);
            }
            return Result.success(products);
        } catch (SQLException failure){
            return Result.failure("Failed to load products list: " + failure.getLocalizedMessage());
        }
    }
    public static Result<Void> initSchema(){
        try {
            var schemaSql = """
                CREATE TABLE IF NOT EXISTS Products (
                    uuid TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    target TEXT NOT NULL,
                    projectDir TEXT NOT NULL
                );
                """;
            var resultConn = connection();
            if (!(resultConn instanceof Result.Success<Connection> (Connection connection))){
                return Result.failure("Failed to open database connection");
            }
            var schemaStatement = connection.createStatement();
            schemaStatement.execute(schemaSql);
            return Result.success(null);
        } catch (SQLException error){
            return Result.failure(error.getLocalizedMessage());
        }
    }

    public static Result<Void> delete(OSQAProduct product) {
        try {
            var deleteSql = """
                DELETE FROM Products
                WHERE uuid = ?
                """;
            var result = connection();
            if (!(result instanceof Result.Success<Connection> (Connection connection))){
                return Result.failure("Failed to open database connection");
            }
            var statement = connection.prepareStatement(deleteSql);
            statement.setString(1,product.uuid());
            statement.executeUpdate();
            return Result.success(null);
        } catch (SQLException error){
            return Result.failure(error.getLocalizedMessage());
        }
    }
}
