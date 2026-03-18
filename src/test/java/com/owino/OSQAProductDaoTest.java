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
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import com.owino.core.Result;
import com.owino.core.OSQAConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import com.owino.core.OSQAModel.OSQAProduct;
import com.owino.desktop.products.OSQAProductDao;
import static org.assertj.core.api.Assertions.assertThat;
public class OSQAProductDaoTest {
    @BeforeEach
    public void setUp(){
        OSQAProductDao.initSchema();
    }
    @Test
    public void shouldGetDatabaseConnectionTest(){
        var result = OSQAProductDao.connection();
        IO.println(result);
        assertThat(result instanceof Result.Success<Connection>).isTrue();
        var connection = ((Result.Success<Connection>) result).value();
        assertThat(connection).isNotNull();
    }
    @Test
    public void shouldSaveProductTest() throws IOException {
        var projectDir = Paths.get(OSQAConfig.MODULE_DIR);
        if (!Files.exists(projectDir))
            Files.createDirectory(projectDir);
        var product = new OSQAProduct(
                "a76b4d46-e7df-43ea-afec-221b899ae527",
                "OSQA Desktop",
                "OSX",
                projectDir);
        var result = OSQAProductDao.saveProduct(product);
        assertThat(result).isInstanceOf(Result.Success.class);
    }
    @Test
    public void shouldListProductsTest() throws IOException {
        var projectDir = Paths.get(OSQAConfig.MODULE_DIR);
        if (!Files.exists(projectDir))
            Files.createDirectory(projectDir);
        var product = new OSQAProduct(
                "a76b4d46-e7df-43ea-afec-221b899ae527",
                "OSQA Desktop",
                "OSX",
                projectDir);
        var result = OSQAProductDao.saveProduct(product);
        assertThat(result).isInstanceOf(Result.Success.class);
        var listResult = OSQAProductDao.listProducts();
        assertThat(listResult).isNotNull();
        assertThat(listResult).isInstanceOf(Result.Success.class);
        List<OSQAProduct> products = switch (listResult){
            case Result.Success (List<OSQAProduct> productList) -> productList;
            case Result.Failure<List<OSQAProduct>> _ -> List.of();
        };
        assertThat(products).isNotEmpty();
        assertThat(products.getFirst()).isNotNull();
        assertThat(products.getFirst().uuid()).isEqualTo(product.uuid());
        assertThat(products.getFirst().name()).isEqualTo(product.name());
        assertThat(products.getFirst().target()).isEqualTo(product.target());
        assertThat(products.getFirst().projectDir().toAbsolutePath().toString())
                .isEqualTo(product.projectDir().toAbsolutePath().toString());
    }
    @Test
    public void shouldInitSchemaTest(){
        var result = OSQAProductDao.initSchema();
        assertThat(result).isInstanceOf(Result.Success.class);
    }
    @Test
    public void shouldDeleteProductTest() throws IOException {
        var projectDir = Paths.get(OSQAConfig.MODULE_DIR);
        if (!Files.exists(projectDir))
            Files.createDirectory(projectDir);
        var product = new OSQAProduct(
                "a76b4d46-e7df-43ea-afec-221b899ae527",
                "OSQA Desktop",
                "OSX",
                projectDir);
        var saveResult = OSQAProductDao.saveProduct(product);
        assertThat(saveResult).isInstanceOf(Result.Success.class);
        var deleteResult = OSQAProductDao.delete(product);
        assertThat(deleteResult).isInstanceOf(Result.Success.class);
    }
    @Test
    public void shouldUpdateProductTest() throws IOException {
        var projectDir = Paths.get(OSQAConfig.MODULE_DIR);
        if (!Files.exists(projectDir))
            Files.createDirectory(projectDir);
        var product = new OSQAProduct(
                "a76b4d46-e7df-43ea-afec-221b899ae527",
                "OSQA Desktop",
                "OSX",
                projectDir);
        var result = OSQAProductDao.saveProduct(product);
        assertThat(result).isInstanceOf(Result.Success.class);
        var updatedProduct = new OSQAProduct(
                "a76b4d46-e7df-43ea-afec-221b899ae527",
                "OSQA Desktop V2",
                "Windows x86_64",
                projectDir);
        var updateResult = OSQAProductDao.updateProduct(updatedProduct);
        assertThat(updateResult).isInstanceOf(Result.Success.class);
        var listProductsResult = OSQAProductDao.listProducts();
        assertThat(listProductsResult).isInstanceOf(Result.Success.class);
        if (listProductsResult instanceof Result.Success<List<OSQAProduct>>(List<OSQAProduct> updatedProducts)){
            var updated = updatedProducts.getFirst();
            assertThat(updated).isNotNull();
            assertThat(updated.uuid()).isEqualTo(updatedProduct.uuid());
            assertThat(updated.name()).isEqualTo(updatedProduct.name());
            assertThat(updated.target()).isEqualTo(updatedProduct.target());
        }
    }
    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(OSQAConfig.MODULE_DIR + File.separator + OSQAConfig.OSQA_DB));
    }
}
