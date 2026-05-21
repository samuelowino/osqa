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
import com.owino.core.OSQAConfig;
import static com.owino.core.OSQAModel.OSQAVerification;
import static com.owino.core.OSQAModel.OSQATestSpec;
import static com.owino.core.OSQAModel.OSQATestCase;
import static com.owino.core.OSQAModel.OSQAFeature;
import static com.owino.core.OSQAModel.OSQAProduct;
import com.owino.reports.OSQAXSSFTestingReport;
import com.owino.core.Result;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
public class OSQAXSSFTestingReportTest {
    @BeforeEach
    public void setUp() throws IOException {
        deleteAppDataFolder();
        var directory = Paths.get("data");
        var folder = Files.createDirectory(directory);
        assertThat(folder).isNotNull();
        assertThat(Files.exists(folder)).isTrue();
    }
    @Test
    public void extractTestReportElementsTest() {
        var appDir = Paths.get("data");
        var product = new OSQAProduct("08363eb4-4b7d-4c70-8853-f129dcd78835","Test Product","Android", appDir);
        var specFile = OSQAConfig.timestampedName(LocalDateTime.now(),"json");
        var filePath = appDir.toAbsolutePath().toString().concat(File.separator).concat(specFile);
        var testCase = new OSQATestCase("73bbcc66-78aa-45ed-956b-f605296a458b","Test Case",filePath);
        var featureTitle = "Feature Name";
        var featureNameBuilder = new StringBuilder(appDir.toUri().getPath());
        var prefix = "feature";
        featureNameBuilder.append(prefix);
        featureNameBuilder.append(featureTitle.replaceAll(" ",""));
        featureNameBuilder.append(OSQAConfig.timestampedName(LocalDateTime.now(),"json"));
        var fileName = featureNameBuilder.toString();
        var feature = new OSQAFeature(
                "91df8a35-6224-4dbc-8c84-a87bb49ac05d",
                "3f16844a-285f-4fc2-894b-65d96fd3a212",
                "Feature Name",
                "Feature description",
                "CRITICAL",fileName,List.of(testCase));
        OSQAConfig.writeFeature(feature);
        var verifications = List.of(
                new OSQAVerification("54df4e30-b691-4ebb-93a2-a294a10b49ea",0,"verification step 1",false),
                new OSQAVerification("2e88a797-7017-40cb-887b-498902880482",0,"verification step 2",false),
                new OSQAVerification("0cd7bc7d-e11e-49ad-b4ad-444978ef93aa",0,"verification step 3",false),
                new OSQAVerification("5f6a8fa4-9f80-4b9e-9474-f308705f378c",0,"verification step 4",true)
        );
        var specification = new OSQATestSpec(
                "6321e37d-b049-4d0f-8d50-3e7e10bef317",
                "Launch application",
                verifications);
        OSQAConfig.writeSpecFile(Paths.get("data"), specification, specFile);
        var result = OSQAXSSFTestingReport.extractReportElements(product);
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Result.Success.class);
        if (result instanceof Result.Success<List<OSQAXSSFTestingReport>> (List<OSQAXSSFTestingReport> xssfReport)) {
            assertThat(xssfReport.size()).isEqualTo(1);
            assertThat(xssfReport.getFirst()).isNotNull();
            assertThat(xssfReport.getFirst().name()).isEqualTo(feature.name());
            assertThat(xssfReport.getFirst().description()).isEqualTo(feature.description());
            assertThat(xssfReport.getFirst().verifications()).isNotEmpty();
            assertThat(xssfReport.getFirst().verifications().size()).isEqualTo(4);
        }
    }
    @AfterEach
    public void tearDown() throws IOException {
        deleteAppDataFolder();
    }
    private static void deleteAppDataFolder() throws IOException {
        var directory = Paths.get("data");
        if (Files.exists(directory)){
            try(var dirWalk = Files.walk(directory)){
                dirWalk.sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
        }
    }
}
