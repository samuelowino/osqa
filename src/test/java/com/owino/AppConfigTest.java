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
import java.util.UUID;
import java.util.List;
import java.nio.file.Path;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import com.owino.core.Result;
import java.time.LocalDateTime;
import com.owino.core.OSQAConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import com.owino.core.OSQAModel.OSQAProduct;
import com.owino.core.OSQAModel.OSQAFeature;
import com.owino.core.OSQAModel.OSQATestCase;
import com.owino.core.OSQAModel.OSQATestSpec;
import com.owino.core.OSQAModel.OSQAVerification;
import tools.jackson.databind.exc.ValueInstantiationException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
public class AppConfigTest {
    private final String TEST_CASE_SPEC_FILE = "data/test-001-spec.json";
    private Path featuresFile;
    private Path testSpecFile;
    @BeforeEach
    public void setUp() throws IOException {
        deleteAppDataFolder();
        prepareFeatureFile();
        var filePath = Paths.get(TEST_CASE_SPEC_FILE);
        testSpecFile = Files.createFile(filePath);
        assertThat(testSpecFile).isNotNull();
        assertThat(Files.exists(testSpecFile)).isTrue();
        Files.write(testSpecFile, List.of(testCaseSpecJson.split("\n")));
        try (var stream = Files.lines(testSpecFile)) {
            assertThat(stream.count()).isGreaterThan(0);
        }
    }
    private void prepareFeatureFile() throws IOException {
        var filePath = Paths.get(OSQAConfig.MODULE_FILE);
        Files.createDirectory(Paths.get("data"));
        featuresFile = Files.createFile(filePath);
        assertThat(featuresFile).isNotNull();
        assertThat(Files.exists(featuresFile)).isTrue();
        Files.write(featuresFile, List.of(featuresJson.split("\n")));
        try (var stream = Files.lines(featuresFile)) {
            assertThat(stream.count()).isGreaterThan(0);
        }
    }
    @Test
    public void shouldLoadFeaturesListFileTest() {
        Result<Void> result = OSQAConfig.loadFeaturesListFile();
        System.out.println(result);
        assertThat(result instanceof Result.Success<Void>).isTrue();
    }
    @Test
    public void shouldComposeFeatureListTest() {
        Result<Void> result = OSQAConfig.loadFeaturesListFile();
        assertThat(result instanceof Result.Success).isTrue();
        Result<OSQAFeature> loadFeaturesResult = OSQAConfig.loadFeature(OSQAConfig.MODULE_FILE);
        assertThat(loadFeaturesResult instanceof Result.Success<OSQAFeature>).isTrue();
        var calendarAndNavFeature = ((Result.Success<OSQAFeature>) loadFeaturesResult).value();
        assertThat(calendarAndNavFeature).isNotNull();
        assertThat(calendarAndNavFeature);
        assertThat(calendarAndNavFeature).isNotNull();
        assertThat(calendarAndNavFeature.uuid()).isEqualTo("a76b4d46-e7df-43ea-afec-221b899ae527");
        assertThat(calendarAndNavFeature.name()).isEqualTo("Core Calendar and Navigation");
        assertThat(calendarAndNavFeature.description()).isEqualTo("Validates basic calendar rendering, navigation controls, and fundamental UI elements.");
        assertThat(calendarAndNavFeature.priority()).isEqualTo("Critical");
        List<OSQATestCase> testCases = calendarAndNavFeature.testCases();
        assertThat(testCases).isNotNull();
        assertThat(testCases).isNotEmpty();
        Optional<OSQATestCase> testCase = testCases.stream().findFirst();
        assertThat(testCase).isNotNull();
        assertThat(testCase).isNotEmpty();
        assertThat(testCase.get().uuid()).isEqualTo("0b8c4bf2-4590-4b01-bda2-cf7271a76789");
        assertThat(testCase.get().title()).isEqualTo("Smoke - Create Daily Task");
        assertThat(testCase.get().specFile()).isEqualTo("tc-smoke-001.json");
    }
    @Test
    public void shouldLoadTestSpecificationTest(){
        var expectedTestSpec = new OSQATestSpec(
                "a06e2598-bed3-4393-b6a2-9645b6bfa294",
                "On Device B, mark the 'Team Sync' task as complete.",
                List.of(
                        new OSQAVerification(UUID.randomUUID().toString(),1,"On Device B, the task is marked complete and a new instance appears with the correct future date."),
                        new OSQAVerification(UUID.randomUUID().toString(),2,"On Device A, after a sync/refresh, the original task is marked complete and the new instance appears with the correct future date.")
                ));
        var testCase = new OSQATestCase(
                "47196d64-56f8-4ad3-b96e-24acbc907af7",
                "Task Completion Sync",
                TEST_CASE_SPEC_FILE);
        Result<OSQATestSpec> result = OSQAConfig.loadTestCaseSpec(testCase);
        IO.println(result);
        assertThat(result instanceof Result.Success<OSQATestSpec>).isTrue();
        OSQATestSpec actualTestSpec = ((Result.Success<OSQATestSpec>) result).value();
        assertThat(actualTestSpec).isNotNull();
        assertThat(actualTestSpec.action()).isEqualTo(expectedTestSpec.action());
        assertThat(actualTestSpec.verifications().size()).isEqualTo(expectedTestSpec.verifications().size());
        assertThat(actualTestSpec.verifications()).isNotEmpty();
        assertThat(actualTestSpec.verifications().getFirst().order()).isEqualTo(1);
        assertThat(actualTestSpec.verifications().getFirst().description()).isEqualTo("On Device B, the task is marked complete and a new instance appears with the correct future date.");
        assertThat(actualTestSpec.verifications().getLast().order()).isEqualTo(2);
        assertThat(actualTestSpec.verifications().getLast().description()).isEqualTo("On Device A, after a sync/refresh, the original task is marked complete and the new instance appears with the correct future date.");
    }
    @Test
    public void shouldRejectInvalidJsonFieldsTest() throws IOException{
        Files.deleteIfExists(featuresFile);
        var filePath = Paths.get(OSQAConfig.MODULE_FILE);
        featuresFile = Files.createFile(filePath);
        assertThat(featuresFile).isNotNull();
        assertThat(Files.exists(featuresFile)).isTrue();
        Files.write(featuresFile, List.of(invalidFeaturesJson.split("\n")));
        try(var stream = Files.lines(featuresFile)){
            assertThat(stream.count()).isGreaterThan(0);
        }
        Result<Void> result = OSQAConfig.loadFeaturesListFile();
        assertThat(result instanceof Result.Success).isTrue();
        assertThatThrownBy(() -> OSQAConfig.loadFeature(OSQAConfig.MODULE_FILE))
                .isInstanceOf(ValueInstantiationException.class);
    }
    @Test
    public void shouldGenerateTimestampedFeatureFileNameTest(){
        var expectedFileName = "2025-11-20-08-34-40.json";
        var extension = "json";
        var created = LocalDateTime.of(2025,11,20,8,34,40);
        String actualFileName = OSQAConfig.timestampedName(created,extension);
        assertThat(actualFileName).isNotEmpty();
        assertThat(actualFileName).isEqualTo(expectedFileName);
    }
    @Test
    public void shouldWriteSpecFileTest() throws IOException {
        var uuid = "5833312b-7c84-4e6d-a067-622eb2156761";
        var verification = new OSQAVerification(UUID.randomUUID().toString(),0,"verification step");
        var specification = new OSQATestSpec(uuid,"Launch application",List.of(verification));
        var timestamp = LocalDateTime.of(2000,11,21,10,55,30);
        var specFile = OSQAConfig.timestampedName(timestamp,"json");
        var result = OSQAConfig.writeSpecFile(Paths.get(OSQAConfig.MODULE_DIR),specification,specFile);
        IO.println(result);
        assertThat(result instanceof Result.Success<Void>).isTrue();
        Files.deleteIfExists(Paths.get(specFile));
    }
    @Test
    public void shouldWriteFeaturesConfFileTest() throws IOException {
        var uuid = "5833312b-7c84-4e6d-a067-622eb2156761";
        var testSpec = new OSQATestCase(uuid,"testcase","specfile.json");
        var prefix = "feature";
        var appDataDir = Paths.get(OSQAConfig.MODULE_DIR);
        var nameBuilder = new StringBuilder(appDataDir.toUri().getPath());
        var featureTitle = "Feature notes";
        nameBuilder.append(File.separator);
        nameBuilder.append(prefix);
        nameBuilder.append(featureTitle.replaceAll(" ",""));
        nameBuilder.append(OSQAConfig.timestampedName(LocalDateTime.now(),"json"));
        var fileName = nameBuilder.toString();
        var feature = new OSQAFeature(uuid,"5833312b-7c84-4e6d-a067-622eb2156761","Launch application",featureTitle,"Critical",fileName,List.of(testSpec));
        var result = OSQAConfig.writeFeature(feature);
        IO.println(result);
        assertThat(result instanceof Result.Success<Path>).isTrue();
        var path = ((Result.Success<Path>) result).value();
        assertThat(Files.exists(path)).isTrue();
        IO.println(path.getFileName());
        Files.deleteIfExists(((Result.Success<Path>) result).value());
    }
    @Test
    public void shouldFindAndListFeatureConfFilesTest() throws IOException {
        var uuid = "5833312b-7c84-4e6d-a067-622eb2156761";
        var featureTitle = "Launch application";
        var appDataDir = Paths.get(OSQAConfig.MODULE_DIR);
        var testSpec = new OSQATestCase(uuid,"testcase","specfile.json");
        var nameBuilder = new StringBuilder(appDataDir.toUri().getPath());
        var prefix = "feature";
        nameBuilder.append(File.separator);
        nameBuilder.append(prefix);
        nameBuilder.append(featureTitle.replaceAll(" ",""));
        nameBuilder.append(OSQAConfig.timestampedName(LocalDateTime.now(),"json"));
        var fileName = nameBuilder.toString();
        var feature = new OSQAFeature(uuid,"5833312b-7c84-4e6d-a067-622eb2156761",featureTitle,"Feature notes","Critical",fileName,List.of(testSpec));
        var result = OSQAConfig.writeFeature(feature);
        IO.println(result);
        assertThat(result instanceof Result.Success<Path>).isTrue();
        var path = ((Result.Success<Path>) result).value();
        assertThat(Files.exists(path)).isTrue();
        Result<List<OSQAFeature>> featuresResult = OSQAConfig.listFeatures(Paths.get(OSQAConfig.MODULE_DIR));
        IO.println(featuresResult);
        assertThat(featuresResult instanceof Result.Success<List<OSQAFeature>>).isTrue();
        var features = ((Result.Success<List<OSQAFeature>>) featuresResult).value();
        assertThat(features).isNotEmpty();
        assertThat(features.getFirst()).isNotNull();
        assertThat(features.getFirst().name()).isNotEmpty();
        assertThat(features.getFirst().description()).isNotEmpty();
        Files.deleteIfExists(path);
    }
    @Test
    public void shouldOverwriteSpecFileTest(){
        var uuid = "5833312b-7c84-4e6d-a067-622eb2156761";
        var verification = new OSQAVerification(UUID.randomUUID().toString(),0,"verification step");
        var specification = new OSQATestSpec(uuid,"Launch application",List.of(verification));
        var timestamp = LocalDateTime.of(2000,11,21,10,55,30);
        var specFile = OSQAConfig.timestampedName(timestamp,"json");
        var appDir = Paths.get(OSQAConfig.MODULE_DIR);
        var filePath = appDir.toAbsolutePath().toString().concat(File.separator).concat(specFile);
        var testCase = new OSQATestCase(uuid,"Test Case",filePath);
        var result = OSQAConfig.writeSpecFile(appDir,specification,specFile);
        IO.println(result);
        assertThat(result instanceof Result.Success<Void>).isTrue();
        var preOverwriteTestSpecLoadResult = OSQAConfig.loadTestCaseSpec(testCase);
        IO.println("load pre overwrite test spec: result : " + preOverwriteTestSpecLoadResult);
        assertThat(preOverwriteTestSpecLoadResult instanceof Result.Success<OSQATestSpec>).isTrue();
        OSQATestSpec preOverwriteSpec = ((Result.Success<OSQATestSpec>) preOverwriteTestSpecLoadResult).value();
        assertThat(preOverwriteSpec).isNotNull();
        assertThat(preOverwriteSpec.verifications().size()).isEqualTo(1);
        assertThat(preOverwriteSpec.verifications().getFirst()).isNotNull();
        assertThat(preOverwriteSpec.verifications().getFirst().order()).isEqualTo(verification.order());
        assertThat(preOverwriteSpec.verifications().getFirst().description()).isEqualTo(verification.description());
        var newVerification = new OSQAVerification(UUID.randomUUID().toString(),0,"verification step");
        var updatedSpec = new OSQATestSpec(uuid,"Launch application",List.of(verification,newVerification));
        var overwriteResult = OSQAConfig.overwriteSpecFile(updatedSpec,testCase);
        assertThat(overwriteResult instanceof Result.Success<Void>).isTrue();
        var testSpecLoadResult = OSQAConfig.loadTestCaseSpec(testCase);
        IO.println(result);
        assertThat(testSpecLoadResult instanceof Result.Success<OSQATestSpec>).isTrue();
        OSQATestSpec specOverwrite = ((Result.Success<OSQATestSpec>) testSpecLoadResult).value();
        assertThat(specOverwrite).isNotNull();
        assertThat(specOverwrite.action()).isEqualTo(updatedSpec.action());
        assertThat(specOverwrite.verifications().size()).isEqualTo(updatedSpec.verifications().size());
        assertThat(specOverwrite.verifications().size()).isGreaterThan(1);
        assertThat(specOverwrite.verifications().size()).isEqualTo(2);
    }
    @Test
    public void shouldCreateOrUpdateVerificationStatusTest(){
        var uuid = "5833312b-7c84-4e6d-a067-622eb2156761";
        var verification = new OSQAVerification(UUID.randomUUID().toString(),0,"verification step");
        var specification = new OSQATestSpec(uuid,"Launch application",List.of(verification));
        var timestamp = LocalDateTime.of(2000,11,21,10,55,30);
        var specFile = OSQAConfig.timestampedName(timestamp,"json");
        var appDir = Paths.get(OSQAConfig.MODULE_DIR);
        var filePath = appDir.toAbsolutePath().toString().concat(File.separator).concat(specFile);
        var testCase = new OSQATestCase(uuid,"Test Case",filePath);
        var result = OSQAConfig.writeSpecFile(appDir,specification,specFile);
        IO.println(result);
        assertThat(result instanceof Result.Success<Void>).isTrue();
        var preOverwriteTestSpecLoadResult = OSQAConfig.loadTestCaseSpec(testCase);
        IO.println("load pre overwrite test spec: result : " + preOverwriteTestSpecLoadResult);
        assertThat(preOverwriteTestSpecLoadResult instanceof Result.Success<OSQATestSpec>).isTrue();
        OSQATestSpec preOverwriteSpec = ((Result.Success<OSQATestSpec>) preOverwriteTestSpecLoadResult).value();
        assertThat(preOverwriteSpec).isNotNull();
        assertThat(preOverwriteSpec.verifications().size()).isEqualTo(1);
        assertThat(preOverwriteSpec.verifications().getFirst()).isNotNull();
        assertThat(preOverwriteSpec.verifications().getFirst().order()).isEqualTo(verification.order());
        assertThat(preOverwriteSpec.verifications().getFirst().description()).isEqualTo(verification.description());
        var updatedVerifications = preOverwriteSpec.verifications()
                .stream()
                .map(e -> new OSQAVerification(e.uuid(),e.order(),e.description(),true))
                .toList();
        var updatedSpec = new OSQATestSpec(preOverwriteSpec.uuid(),preOverwriteSpec.action(),updatedVerifications);
        var overwriteResult = OSQAConfig.overwriteSpecFile(updatedSpec,testCase);
        assertThat(overwriteResult instanceof Result.Success<Void>).isTrue();
        var testSpecLoadResult = OSQAConfig.loadTestCaseSpec(testCase);
        IO.println(result);
        assertThat(testSpecLoadResult instanceof Result.Success<OSQATestSpec>).isTrue();
        OSQATestSpec specOverwrite = ((Result.Success<OSQATestSpec>) testSpecLoadResult).value();
        assertThat(specOverwrite).isNotNull();
        assertThat(specOverwrite.action()).isEqualTo(updatedSpec.action());
        assertThat(specOverwrite.verifications().size()).isEqualTo(updatedSpec.verifications().size());
        assertThat(specOverwrite.verifications().getFirst().uuid()).isEqualTo(updatedSpec.verifications().getFirst().uuid());
        assertThat(specOverwrite.verifications().getFirst().description()).isEqualTo(updatedSpec.verifications().getFirst().description());
        assertThat(specOverwrite.verifications().getFirst().verificationStatus()).isEqualTo(updatedSpec.verifications().getFirst().verificationStatus());
        assertThat(specOverwrite.verifications().getFirst().verificationStatus()).isEqualTo(true);
    }
    @Test
    public void shouldUpdateVerificationStatusTest(){
        var uuid = "5833312b-7c84-4e6d-a067-622eb2156761";
        var verification = new OSQAVerification(UUID.randomUUID().toString(),0,"verification step 1",false);
        var verificationStep2 = new OSQAVerification(UUID.randomUUID().toString(),0,"verification step 2",false);
        var specification = new OSQATestSpec(uuid,"Launch application",List.of(verification,verificationStep2));
        var timestamp = LocalDateTime.of(2000,11,21,10,55,30);
        var specFile = OSQAConfig.timestampedName(timestamp,"json");
        var appDir = Paths.get(OSQAConfig.MODULE_DIR);
        var filePath = appDir.toAbsolutePath().toString().concat(File.separator).concat(specFile);
        var testCase = new OSQATestCase(uuid,"Test Case",filePath);
        var result = OSQAConfig.writeSpecFile(appDir,specification,specFile);
        IO.println(result);
        assertThat(result instanceof Result.Success<Void>).isTrue();
        var updatedVerification1 = new OSQAVerification(verification.uuid(),verification.order(),verification.description(),true);
        Result<OSQATestSpec> updatedResult = OSQAConfig.updateVerificationStatus(specification,testCase,updatedVerification1);
        assertThat(updatedResult).isInstanceOf(Result.Success.class);
        var testSpecLoadResult = OSQAConfig.loadTestCaseSpec(testCase);
        IO.println(result);
        assertThat(testSpecLoadResult instanceof Result.Success<OSQATestSpec>).isTrue();
        OSQATestSpec specOverwrite = ((Result.Success<OSQATestSpec>) testSpecLoadResult).value();
        assertThat(specOverwrite).isNotNull();
        assertThat(specOverwrite.action()).isEqualTo(specification.action());
        assertThat(specOverwrite.verifications().size()).isEqualTo(specification.verifications().size());
        var firstVerification = specOverwrite.verifications().stream()
                .filter(e -> e.uuid().equals(verification.uuid()))
                .toList().getFirst();
        var secondVerification = specOverwrite.verifications().stream()
                .filter(e -> e.uuid().equals(verificationStep2.uuid()))
                .toList().getFirst();
        assertThat(firstVerification).isNotNull();
        assertThat(firstVerification.uuid()).isEqualTo(verification.uuid());
        assertThat(firstVerification.description()).isEqualTo(verification.description());
        assertThat(firstVerification.verificationStatus()).isNotEqualTo(verification.verificationStatus());
        assertThat(firstVerification.verificationStatus()).isEqualTo(true);
        assertThat(secondVerification).isNotNull();
        assertThat(secondVerification.uuid()).isEqualTo(verificationStep2.uuid());
        assertThat(secondVerification.description()).isEqualTo(verificationStep2.description());
        assertThat(secondVerification.verificationStatus()).isEqualTo(verificationStep2.verificationStatus());
    }
    @Test
    public void shouldCalculateFeatureVerificationProgress() throws IOException{
        var verifications = List.of(
                new OSQAVerification("54df4e30-b691-4ebb-93a2-a294a10b49ea",0,"verification step 1",false),
                new OSQAVerification("2e88a797-7017-40cb-887b-498902880482",0,"verification step 2",false),
                new OSQAVerification("0cd7bc7d-e11e-49ad-b4ad-444978ef93aa",0,"verification step 3",false),
                new OSQAVerification("5f6a8fa4-9f80-4b9e-9474-f308705f378c",0,"verification step 4",true)
        );
        var specification = new OSQATestSpec("6321e37d-b049-4d0f-8d50-3e7e10bef317","Launch application",verifications);
        var timestamp = LocalDateTime.of(2000,11,21,10,55,30);
        var specFile = OSQAConfig.timestampedName(timestamp,"json");
        var result = OSQAConfig.writeSpecFile(Paths.get(OSQAConfig.MODULE_DIR),specification,specFile);
        assertThat(result instanceof Result.Success<Void>).isTrue();
        var testCase = new OSQATestCase("b37c79fd-a803-4c83-953d-1240af36960a","Test Case",OSQAConfig.MODULE_DIR + File.separator + specFile);
        var featureTitle = "Feature Name";
        var appDataDir = Paths.get(OSQAConfig.MODULE_DIR);
        var nameBuilder = new StringBuilder(appDataDir.toUri().getPath());
        var prefix = "feature";
        nameBuilder.append(File.separator);
        nameBuilder.append(prefix);
        nameBuilder.append(featureTitle.replaceAll(" ",""));
        nameBuilder.append(OSQAConfig.timestampedName(LocalDateTime.now(),"json"));
        var fileName = nameBuilder.toString();
        var feature = new OSQAFeature(
                "9f8fcf88-fb9e-4b62-88f0-30a77b2883a3",
                "e18b9af0-f984-4dd3-9174-782b2c70033a",
                featureTitle,"Feature description","HIGH",fileName,
                List.of(testCase));
        var actualCompletionCount = OSQAConfig.calculateFeatureVerificationProgress(feature);
        var expectedCompletion = 25;
        assertThat(actualCompletionCount).isInstanceOf(Result.Success.class);
        if (actualCompletionCount instanceof Result.Success<Long>(Long progress)){
            assertThat(progress).isGreaterThan(0);
            assertThat(progress).isEqualTo(expectedCompletion);
        }
        Files.deleteIfExists(Paths.get(specFile));
    }
    @Test
    public void shouldInitializeOSQATest(){
        var result = OSQAConfig.appInit();
        assertThat(result).isInstanceOf(Result.Success.class);
    }
    @Test
    public void shouldLoadEnvProfileTest(){
        var result = OSQAConfig.envProfile();
        assertThat(result).isInstanceOf(Result.Success.class);
        if (result instanceof Result.Success<String> (String profile)){
            assertThat(profile).isNotNull();
            assertThat(profile).isNotBlank();
            IO.println("Active Profile:" + profile);
        }
    }
    @Test
    public void shouldDeleteFeatureTest(){
        var specFile = OSQAConfig.timestampedName(LocalDateTime.now(),"json");
        var appDir = Paths.get(OSQAConfig.MODULE_DIR);
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

        var feature2NameBuilder = new StringBuilder(appDir.toUri().getPath());
        feature2NameBuilder.append(File.separator);
        feature2NameBuilder.append(prefix);
        feature2NameBuilder.append(featureTitle.replaceAll(" ",""));
        feature2NameBuilder.append(OSQAConfig.timestampedName(LocalDateTime.now(),"json"));
        var file2Name = feature2NameBuilder.toString();
        var feature2 = new OSQAFeature(
                "91df8a35-6224-4dbc-8c84-a87bb49ac05d",
                "3f16844a-285f-4fc2-894b-65d96fd3a212",
                "Second Feature Name",
                "Feature description",
                "CRITICAL",file2Name,List.of(testCase));
        var writeResult = OSQAConfig.writeFeature(feature);
        var feature2WriteResult = OSQAConfig.writeFeature(feature2);
        assertThat(writeResult).isInstanceOf(Result.Success.class);
        assertThat(feature2WriteResult).isInstanceOf(Result.Success.class);
        var deleteResult = OSQAConfig.deleteFeature(feature);
        assertThat(deleteResult).isInstanceOf(Result.Success.class);
        var listFeaturesResult = OSQAConfig.listFeatures(appDir);
        assertThat(listFeaturesResult).isInstanceOf(Result.Success.class);
        if (listFeaturesResult instanceof Result.Success<List<OSQAFeature>>(List<OSQAFeature> features)){
            assertThat(features).isNotEmpty();
        }
    }
    @Test
    public void shouldMigrateLegacyFeaturesTest(){
        var appDir = Paths.get(OSQAConfig.MODULE_DIR);
        var product = new OSQAProduct("08363eb4-4b7d-4c70-8853-f129dcd78835","Test Product","Android",appDir);
        var result = OSQAConfig.migrateLegacyFeatures(product);
        assertThat(result).isInstanceOf(Result.Success.class);
    }
    @Test
    public void shouldDeleteVerificationTest(){
        var verification1 = new OSQAVerification("bcdf11b5-9a5e-4702-b19d-82bbb2f9a0d0",1,"On Device B, the task is marked complete and a new instance appears with the correct future date.");
        var verification2 = new OSQAVerification("a76b4d46-e7df-43ea-afec-221b899ae527",2,"On Device A, after a sync/refresh, the original task is marked complete and the new instance appears with the correct future date.");
        var expectedTestSpec = new OSQATestSpec(
                "a06e2598-bed3-4393-b6a2-9645b6bfa294",
                "On Device B, mark the 'Team Sync' task as complete.",
                List.of(verification1,verification2));
        var testCase = new OSQATestCase(
                "47196d64-56f8-4ad3-b96e-24acbc907af7",
                "Task Completion Sync",
                TEST_CASE_SPEC_FILE);
        Result<OSQATestSpec> result = OSQAConfig.loadTestCaseSpec(testCase);
        IO.println(result);
        assertThat(result instanceof Result.Success<OSQATestSpec>).isTrue();
        OSQATestSpec actualTestSpec = ((Result.Success<OSQATestSpec>) result).value();
        assertThat(actualTestSpec).isNotNull();
        assertThat(actualTestSpec.action()).isEqualTo(expectedTestSpec.action());
        assertThat(actualTestSpec.verifications()).isNotEmpty();
        assertThat(actualTestSpec.verifications().size()).isEqualTo(expectedTestSpec.verifications().size());
        assertThat(actualTestSpec.verifications().getFirst().order()).isEqualTo(1);
        assertThat(actualTestSpec.verifications().getFirst().description()).isEqualTo("On Device B, the task is marked complete and a new instance appears with the correct future date.");
        assertThat(actualTestSpec.verifications().getLast().order()).isEqualTo(2);
        assertThat(actualTestSpec.verifications().getLast().description()).isEqualTo("On Device A, after a sync/refresh, the original task is marked complete and the new instance appears with the correct future date.");
        var verificationDeleteResult = OSQAConfig.deleteVerification(actualTestSpec,testCase,verification1);
        assertThat(verificationDeleteResult).isInstanceOf(Result.Success.class);
        Result<OSQATestSpec> secondLoadResult = OSQAConfig.loadTestCaseSpec(testCase);
        assertThat(secondLoadResult instanceof Result.Success<OSQATestSpec>).isTrue();
        actualTestSpec = ((Result.Success<OSQATestSpec>) secondLoadResult).value();
        assertThat(actualTestSpec).isNotNull();
        assertThat(actualTestSpec.action()).isEqualTo(expectedTestSpec.action());
        assertThat(actualTestSpec.verifications()).isNotEmpty();
        assertThat(actualTestSpec.verifications().size()).isEqualTo(1);
        assertThat(actualTestSpec.verifications().getFirst()).isNotNull();
        assertThat(actualTestSpec.verifications().getFirst().description()).isEqualTo(verification2.description());
        assertThat(actualTestSpec.verifications().getFirst().verificationStatus()).isEqualTo(verification2.verificationStatus());
        assertThat(actualTestSpec.verifications().getFirst().uuid()).isEqualTo(verification2.uuid());
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
    private final String featuresJson = """
            {
                "uuid": "a76b4d46-e7df-43ea-afec-221b899ae527",
                "productUuid": "a76b4d46-e7df-43ea-afec-221b899ae527",
                "name": "Core Calendar and Navigation",
                "description": "Validates basic calendar rendering, navigation controls, and fundamental UI elements.",
                "priority": "Critical",
                "filePath": "testFile",
                "testCases": [
                  {
                    "uuid": "0b8c4bf2-4590-4b01-bda2-cf7271a76789",
                    "title": "Smoke - Create Daily Task",
                    "specFile": "tc-smoke-001.json"
                  }
                ]
              }
            """;
    private final String invalidFeaturesJson = """
            {
                "uuid": "uuid",
                "productUuid": "",
                "name": "",
                "description": "",
                "priority": "",
                "filePath": "",
                "testCases": [
                  {
                    "uuid": "uuid",
                    "title": "Smoke - Create Daily Task",
                    "specFile": "tc-smoke-001.json"
                  }
                ]
              }
            """;
    private final String testCaseSpecJson = """
            {
                "uuid": "a06e2598-bed3-4393-b6a2-9645b6bfa294",
                "action": "On Device B, mark the 'Team Sync' task as complete.",
                "verifications": [
                  {
                    "uuid": "bcdf11b5-9a5e-4702-b19d-82bbb2f9a0d0",
                    "order": 1,
                    "description": "On Device B, the task is marked complete and a new instance appears with the correct future date.",
                    "verificationStatus": false
                  },
                  {
                    "uuid": "a76b4d46-e7df-43ea-afec-221b899ae527",
                    "order": 2,
                    "description": "On Device A, after a sync/refresh, the original task is marked complete and the new instance appears with the correct future date.",
                    "verificationStatus": false
                  }
                ]
            }
            """;
}
