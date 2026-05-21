package com.owino.reports;
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
import static com.owino.core.OSQAModel.OSQAProduct;
import static com.owino.core.OSQAModel.OSQATestCase;
import static com.owino.core.OSQAModel.OSQATestSpec;
import com.owino.core.OSQAModel;
import com.owino.core.OSQAVoid;
import com.owino.core.Result;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import java.time.format.DateTimeFormatter;
import org.apache.poi.ss.usermodel.*;
import java.io.FileOutputStream;
import com.owino.core.OSQAConfig;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
public record OSQAXSSFTestingReport(
        String name,
        String description,
        String testInstructions,
        List<String> verifications,
        XSSFVerificationStatus testingStatus,
        String testerNotes
) {
    public OSQAXSSFTestingReport(String name, String description, String instructions, List<String> verifications){
        this(name,description,instructions,verifications,XSSFVerificationStatus.PENDING,"");
    }
    public static Result<OSQAVoid> generateReport(Path destinationFolder, OSQAProduct product) {
        return extractReportElements(product)
                .flatMap(report -> toWorkBook(destinationFolder,report,product.name()));
    }
    public static Result<List<OSQAXSSFTestingReport>> extractReportElements(OSQAProduct product) {
        var appDir = product.projectDir();
        var featuresLoadResult = OSQAConfig.listFeatures(appDir);
        return switch (featuresLoadResult) {
            case Result.Success<List<OSQAModel.OSQAFeature>> (var features) -> {
                List<OSQAXSSFTestingReport> report = features.stream().map(feature -> {
                    var featureName = feature.name();
                    var description = feature.description();
                    var testCases = feature.testCases();
                    List<OSQAXSSFTestingReport> testingReport = new ArrayList<>();
                    for (OSQATestCase testCase: testCases) {
                        var testSpecResult = OSQAConfig.loadTestCaseSpec(testCase);
                        if (testSpecResult instanceof Result.Success<OSQATestSpec> (OSQATestSpec testSpec)) {
                            var verifications = testSpec.verifications().stream()
                                    .map(OSQAModel.OSQAVerification::description)
                                    .toList();
                            var testingInstructions = testSpec.action();
                            var XSSFReport = new OSQAXSSFTestingReport(
                                    featureName,
                                    description,
                                    testingInstructions,
                                    verifications
                            );
                            testingReport.add(XSSFReport);
                        } else if (testSpecResult instanceof Result.Failure<OSQATestSpec> (Throwable error)) {
                            IO.println("Some reports failed processing " + error.getLocalizedMessage());
                        }
                    }
                    return testingReport;
                }).flatMap(List::stream).toList();
                yield Result.success(report);
            }
            case Result.Failure (var error) -> Result.failure(error.getLocalizedMessage());
        };
    }
    private static Result<OSQAVoid> toWorkBook(Path destinationFolderPath, List<OSQAXSSFTestingReport> qaList, String productName) {
        try(var workbook = new XSSFWorkbook()){
            var dateFormat = DateTimeFormatter.ofPattern("yyyy-MMMM-dd");
            var sheet = workbook.createSheet(productName + "-QA-" + LocalDate.now().format(dateFormat));
            int rowNum = 0;
            var headerRow = sheet.createRow(rowNum);
            var nameHeader = headerRow.createCell(0);
            var descriptionHeader = headerRow.createCell(1);
            var instructionsHeader = headerRow.createCell(2);
            var verificationsHeader = headerRow.createCell(3);
            var passedQAHeader = headerRow.createCell(4);
            var testerNotesHeader = headerRow.createCell(5);
            styleHeader(nameHeader, workbook);
            styleHeader(descriptionHeader, workbook);
            styleHeader(instructionsHeader, workbook);
            styleHeader(verificationsHeader, workbook);
            styleHeader(passedQAHeader, workbook);
            styleHeader(testerNotesHeader, workbook);
            setMinColumnWidth(sheet, 0, 6000);
            setMinColumnWidth(sheet, 1, 15000);
            setMinColumnWidth(sheet, 2, 18000);
            setMinColumnWidth(sheet, 3, 18000);
            setMinColumnWidth(sheet, 4, 8000);
            setMinColumnWidth(sheet, 5, 20000);
            nameHeader.setCellValue("Feature Name");
            descriptionHeader.setCellValue("Description");
            instructionsHeader.setCellValue("Testing Instructions");
            verificationsHeader.setCellValue("Verifications");
            passedQAHeader.setCellValue("Did all verifications pass testing?");
            testerNotesHeader.setCellValue("Tester Notes");
            addDropdownConstraint(sheet,XSSFVerificationStatus.descriptions());
            rowNum += 1;
            for (OSQAXSSFTestingReport feature: qaList){
                var row = sheet.createRow(rowNum);
                var nameCell = row.createCell(0);
                var descriptionCell = row.createCell(1);
                var instructionsCell = row.createCell(2);
                var verificationsCell = row.createCell(3);
                var passedStatusCell = row.createCell(4);
                var testerNotesCell = row.createCell(5);
                nameCell.setCellValue(feature.name());
                descriptionCell.setCellValue(feature.description());
                instructionsCell.setCellValue(feature.testInstructions());
                passedStatusCell.setCellValue(feature.testingStatus().displayName + "\t" + feature.testingStatus.description);
                testerNotesCell.setCellValue(feature.testerNotes());
                var verificationDesc = String.join(":\n\n", feature.verifications);
                verificationsCell.setCellValue(verificationDesc);
                styleCell(nameCell,true, false, workbook);
                styleCell(descriptionCell, false,false, workbook);
                styleCell(instructionsCell, false,false, workbook);
                styleCell(verificationsCell, true,true, workbook);
                styleCell(passedStatusCell, false,false, workbook);
                styleCell(testerNotesCell, false,false, workbook);
                rowNum += 1;
            }
            Path file = destinationFolderPath.resolve(productName.concat(".xlsx"));
            workbook.write(new FileOutputStream(file.toFile()));
            return Result.success(new OSQAVoid());
        } catch(IOException exception) {
            return Result.failure(exception.getLocalizedMessage());
        }
    }
    private static void styleHeader(Cell headerCell, XSSFWorkbook workbook){
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerCell.setCellStyle(headerStyle);
    }
    private static void styleCell(Cell dataCell, boolean bold, boolean italic, XSSFWorkbook workbook) {
        var cellStyle = workbook.createCellStyle();
        var font = workbook.createFont();
        font.setBold(bold);
        font.setItalic(italic);
        cellStyle.setWrapText(true);
        cellStyle.setFont(font);
        dataCell.setCellStyle(cellStyle);
    }
    private static void addDropdownConstraint(Sheet sheet, String[] options) {
        var dataValidationHelper = sheet.getDataValidationHelper();
        var dataValidationConstraint = dataValidationHelper.createExplicitListConstraint(options);
        var firstRow = 1;
        var firstCol = 4;
        var lastRow = 1048575;
        var lastCol = 4;
        var cellRangeAddressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        var dataValidation = dataValidationHelper.createValidation(dataValidationConstraint, cellRangeAddressList);
        sheet.addValidationData(dataValidation);
    }
    private static void setMinColumnWidth(Sheet sheet, int columnIndex, int minWidth){
        var currentWidth = sheet.getColumnWidth(columnIndex);
        if (currentWidth < minWidth) {
            sheet.setColumnWidth(columnIndex, minWidth);
        }
    }
}
