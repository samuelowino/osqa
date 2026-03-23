package com.owino.desktop.features;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.ArrayList;
import com.owino.core.Result;
import com.owino.desktop.CSS;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.time.LocalDateTime;
import com.owino.core.OSQAConfig;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import org.greenrobot.eventbus.EventBus;
import com.owino.core.OSQAModel.OSQAFeature;
import tools.jackson.databind.ObjectMapper;
import com.owino.core.OSQAModel.OSQATestSpec;
import com.owino.core.OSQAModel.OSQAProduct;
import com.owino.core.OSQAModel.OSQATestCase;
import com.owino.desktop.OSQANavigationEvents;
import com.owino.desktop.products.OSQAProductDao;
import com.owino.core.OSQAModel.OSQAVerification;
public class FeatureFormView extends ScrollPane {
    private final List<OSQATestCase> testCases = new ArrayList<>();
    private final ComboBox<OSQAProduct> productComboBox = new ComboBox<>();
    private final ObservableList<OSQAVerification> verificationsList = FXCollections.observableArrayList();
    private static final Insets MARGIN = new Insets(6,22,12,22);
    private static final Insets FIELD_MARGIN = new Insets(4,12,2,12);
    private static final Insets LABEL_MARGIN = new Insets(12);
    private static final Font FORM_LABEL_FONT = Font.font(20);
    private final VBox verificationListContainer = new VBox();
    private TextArea usageInstructionsTextArea;
    private TextField featureTitleTextField;
    private TextField descriptionTextField;
    private OSQAFeature feature;
    private boolean isEditMode;
    private OSQATestSpec testSpec;
    private OSQATestCase testCase;
    public FeatureFormView(OSQAFeature editModeFeature,boolean isEditMode){
        this.isEditMode = isEditMode;
        this.feature = editModeFeature;
        var featureForm = initFeatureForm();
        setContent(featureForm);
        setFitToWidth(true);
        if (!isEditMode)
            initProducts();
    }
    private VBox initFeatureForm() {
        var formContainer = new VBox();
        var header = new BorderPane();
        var titleText = new Text("New Feature");
        var testCaseFormContainer = new VBox();
        var separator = new Separator();
        var userActionTitle = new Text("Usage instructions");
        var actionButtonsContainer = new HBox(22);
        var cancelButton = new Button("Cancel");
        var featureDetailsContainer = new VBox();
        var productTitleLabel = new Label("Product");
        var featureTitleText = new Text("Name");
        featureTitleTextField = new TextField();
        var descriptionText = new Text("Description");
        descriptionTextField = new TextField();
        usageInstructionsTextArea = new TextArea();
        var verificationsContainer = new BorderPane();
        var verificationLabel = new Text("Verifications:");
        var addVerificationButton = new Button("Add Verification");
        actionButtonsContainer.getChildren().add(cancelButton);
        titleText.setFont(Font.font(21));
        header.setLeft(titleText);
        header.setRight(actionButtonsContainer);
        var saveButton = new Button("Save");
        cancelButton.setOnAction(_ -> EventBus.getDefault().post(new OSQANavigationEvents.OpenDashboardEvent()));
        productComboBox.setMinWidth(900);
        productTitleLabel.setFont(FORM_LABEL_FONT);
        featureTitleText.setFont(FORM_LABEL_FONT);
        descriptionText.setFont(FORM_LABEL_FONT);
        featureDetailsContainer.getChildren().add(productTitleLabel);
        featureDetailsContainer.getChildren().add(productComboBox);
        featureDetailsContainer.getChildren().add(featureTitleText);
        featureDetailsContainer.getChildren().add(featureTitleTextField);
        featureDetailsContainer.getChildren().add(descriptionText);
        featureDetailsContainer.getChildren().add(descriptionTextField);
        featureDetailsContainer.getChildren().add(new Separator());
        featureDetailsContainer.setStyle(CSS.FORM_SECTION_BORDER);
        VBox.setMargin(productTitleLabel,LABEL_MARGIN);
        VBox.setMargin(featureTitleText,LABEL_MARGIN);
        VBox.setMargin(featureTitleTextField,FIELD_MARGIN);
        VBox.setMargin(descriptionText,LABEL_MARGIN);
        VBox.setMargin(descriptionTextField,FIELD_MARGIN);
        VBox.setMargin(productComboBox,FIELD_MARGIN);
        formContainer.getChildren().add(header);
        formContainer.getChildren().add(featureDetailsContainer);
        VBox.setMargin(header,MARGIN);
        VBox.setMargin(featureDetailsContainer,MARGIN);
        actionButtonsContainer.getChildren().add(saveButton);
        VBox.setMargin(saveButton,MARGIN);
        userActionTitle.setFont(FORM_LABEL_FONT);
        usageInstructionsTextArea.setFont(Font.font(15));
        testCaseFormContainer.getChildren().add(userActionTitle);
        testCaseFormContainer.getChildren().add(usageInstructionsTextArea);
        testCaseFormContainer.getChildren().add(separator);
        VBox.setMargin(userActionTitle,LABEL_MARGIN);
        VBox.setMargin(usageInstructionsTextArea,FIELD_MARGIN);
        formContainer.getChildren().add(testCaseFormContainer);
        VBox.setMargin(testCaseFormContainer,MARGIN);
        verificationsContainer.setLeft(verificationLabel);
        verificationsContainer.setRight(addVerificationButton);
        verificationsContainer.setBottom(verificationListContainer);
        BorderPane.setMargin(verificationLabel,FIELD_MARGIN);
        verificationLabel.setFont(FORM_LABEL_FONT);
        testCaseFormContainer.getChildren().add(verificationsContainer);
        testCaseFormContainer.setStyle(CSS.FORM_SECTION_BORDER);
        addVerificationButton.setOnAction(_ -> {
            Optional<String> inputResult = new FeatureVerificationForm(null,false).showAndWait();
            if (inputResult.isPresent()){
                if (!inputResult.get().isBlank()){
                    verificationsList.add( new OSQAVerification(UUID.randomUUID().toString(),0,inputResult.get()));
                    Alert successAlert = new Alert(Alert.AlertType.NONE);
                    successAlert.setTitle("Success");
                    successAlert.setContentText("""
                            Verification step
                            (%s)
                            has been added successfully.
                            This feature now has (%d) verification steps.
                            """.formatted(inputResult.get(), verificationsList.size()));
                    successAlert.getButtonTypes().add(ButtonType.OK);
                    successAlert.show();
                }
            }
        });
        if (isEditMode){
            initProducts();
            var productResult = OSQAProductDao.findProductByUuid(feature.productUuid());
            if (productResult instanceof Result.Success<OSQAProduct>(OSQAProduct product)){
                productComboBox.getSelectionModel().select(product);
            }
            featureTitleTextField.setText(feature.name());
            descriptionTextField.setText(feature.description());
            testCases.addAll(feature.testCases());
            testCase = testCases.getFirst();
            var testCaseLoadResult = OSQAConfig.loadTestCaseSpec(testCase);
            if (testCaseLoadResult instanceof Result.Success<OSQATestSpec>(OSQATestSpec spec)){
                testSpec = spec;
                IO.println("Test Spec:\n" + new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(testSpec));
                usageInstructionsTextArea.setText(testSpec.action());
                verificationsList.addAll(testSpec.verifications());
            }
        }
        var verificationsListView = new ListView<>(verificationsList);
        verificationsListView.setCellFactory(_ -> new ListCell<>(){
            @Override
            protected void updateItem(OSQAVerification verification, boolean empty) {
                super.updateItem(verification, empty);
                if (empty || verification == null){
                    setText("");
                    setGraphic(null);
                } else {
                    var buttonsContainer = new HBox(12);
                    var container = new VBox();
                    var contentContainer = new BorderPane();
                    var verificationCheckbox = new CheckBox(verification.description());
                    var deleteButton = new Button("Delete");
                    deleteButton.setTextFill(Color.RED);
                    deleteButton.setFont(Font.font(12));
                    buttonsContainer.getChildren().addAll(deleteButton);
                    verificationCheckbox.setSelected(verification.verificationStatus());
                    verificationCheckbox.setWrapText(true);
                    verificationCheckbox.selectedProperty().addListener((observableValue,_,newVerifiedStatus) -> {
                        var updatedVerification = new OSQAVerification(verification.uuid(),verification.order(),verification.description(),newVerifiedStatus);
                        switch (OSQAConfig.updateVerificationStatus(testSpec,testCase,updatedVerification)){
                            case Result.Success<OSQATestSpec> (OSQATestSpec updatedTestSpec) -> {
                                testSpec = updatedTestSpec;
                            }
                            case Result.Failure<OSQATestSpec> failure -> {
                                var alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setContentText("""
                                            Verification Update Failed!
                                            Error: %s
                                            """.formatted(failure.error().getLocalizedMessage()));
                                alert.show();
                            }
                        }
                    });
                    deleteButton.setOnAction(_ -> verificationsList.removeIf(e -> e.uuid().equalsIgnoreCase(verification.uuid())));
                    contentContainer.setLeft(verificationCheckbox);
                    contentContainer.setRight(buttonsContainer);
                    container.getChildren().add(contentContainer);
                    container.getChildren().add(new Separator());
                    VBox.setMargin(contentContainer,new Insets(0,22,0,8));
                    setGraphic(container);
                }
            }
        });
        verificationsListView.setBorder(Border.EMPTY);
        testCaseFormContainer.getChildren().add(verificationsListView);
        VBox.setMargin(verificationsListView,new Insets(22,0,12,6));
        saveButton.setOnAction(_ -> {
            var selectedProduct = productComboBox.getValue();
            var formDataWarning = new Alert(Alert.AlertType.ERROR);
            if (selectedProduct == null){
                formDataWarning.setContentText("Select the product associated with this feature");
                formDataWarning.show();
                return;
            }
            if (featureTitleTextField.getText().isBlank()){
                formDataWarning.setContentText("Feature name is required!");
                formDataWarning.show();
                return;
            }
            if (descriptionTextField.getText().isBlank()){
                formDataWarning.setContentText("Feature description is required!");
                formDataWarning.show();
                return;
            }
            if (usageInstructionsTextArea.getText().isBlank()){
                formDataWarning.setContentText("Feature usage instructions are required!");
                formDataWarning.show();
                return;
            }
            if (verificationsList.isEmpty()){
                formDataWarning.setContentText("Feature verifications are required!");
                formDataWarning.show();
                return;
            }
            if (isEditMode){
                updateFeature();
            } else {
                createFeature();
            }
        });
        productComboBox.setCellFactory(_ -> new ListCell<>(){
            @Override
            protected void updateItem(OSQAProduct product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null){
                    setText("");
                    setGraphic(null);
                } else {
                    var nameLabel = new Label(product.name() + " (" + product.target() + ")" + " (" + product.projectDir().toAbsolutePath() + ")");
                    setGraphic(nameLabel);
                }
            }
        });
        productComboBox.setButtonCell(new ListCell<>(){
            @Override
            protected void updateItem(OSQAProduct selectedProduct, boolean empty) {
                super.updateItem(selectedProduct, empty);
                if (empty || selectedProduct == null){
                    setText("");
                    setGraphic(null);
                } else {
                    var nameLabel = new Label(selectedProduct.name() + " (" + selectedProduct.target() + ")" + " (" + selectedProduct.projectDir().toAbsolutePath() + ")");
                    setGraphic(nameLabel);
                }
            }
        });
        return formContainer;
    }
    private void updateFeature() {
        var selectedProduct = productComboBox.getValue();
        var featureTitle = featureTitleTextField.getText();
        var featureDescription = descriptionTextField.getText();
        testSpec = new OSQATestSpec(testSpec.uuid(), usageInstructionsTextArea.getText(), verificationsList);
        OSQAConfig.overwriteSpecFile(testSpec,testCase);
        feature = new OSQAFeature(
                feature.uuid(),
                selectedProduct.uuid(),
                featureTitle,
                featureDescription,
                "Critical",
                feature.filePath(),
                testCases);
        Result<Void> featureWriteResult = OSQAConfig.overwriteFeature(feature,testSpec,testCase);
        if (featureWriteResult instanceof Result.Success<Void>){
            IO.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(feature));
            var successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setContentText("Feature updated successfully!");
            if (successAlert.showAndWait().isPresent()){
                successAlert.close();
                EventBus.getDefault().post(new OSQANavigationEvents.OpenDashboardEvent());
            }
        } else {
            var errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setContentText("Failed to update feature!");
            errorAlert.show();
        }
    }
    private void createFeature() {
        var selectedProduct = productComboBox.getValue();
        var testCaseTitle = "testcase";
        var appDir = selectedProduct.projectDir();
        var specFile = testCaseTitle + OSQAConfig.timestampedName(LocalDateTime.now(),"json");
        var filePath = appDir.toAbsolutePath().toString().concat(File.separator).concat(specFile);
        testCase = new OSQATestCase(UUID.randomUUID().toString(),testCaseTitle,filePath);
        var specification = new OSQATestSpec(UUID.randomUUID().toString(), usageInstructionsTextArea.getText(), verificationsList);
        OSQAConfig.writeSpecFile(appDir,specification,specFile);
        testCases.add(testCase);
        var featureTitle = featureTitleTextField.getText();
        var featureDescription = descriptionTextField.getText();
        var prefix = "feature";
        var fileNameBuilder = new StringBuilder(appDir.toUri().getPath());
        fileNameBuilder.append(File.separator);
        fileNameBuilder.append(prefix);
        fileNameBuilder.append(featureTitle.replaceAll(" ",""));
        fileNameBuilder.append(OSQAConfig.timestampedName(LocalDateTime.now(),"json"));
        var fileName = fileNameBuilder.toString();
        var featureFilePath = Paths.get(fileName);
        feature = new OSQAFeature(
                UUID.randomUUID().toString(),
                selectedProduct.uuid(),
                featureTitle,
                featureDescription,
                "Critical",
                featureFilePath.toAbsolutePath().toString(),
                testCases);
        var featureWriteResult = OSQAConfig.writeFeature(feature);
        if (featureWriteResult instanceof Result.Success<Path>){
            IO.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(feature));
            var successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setContentText("Feature created successfully!");
            if (successAlert.showAndWait().isPresent()){
                successAlert.close();
                EventBus.getDefault().post(new OSQANavigationEvents.OpenDashboardEvent());
            }
        } else {
            var errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setContentText("Failed to create feature!");
            errorAlert.show();
        }
    }
    private void initProducts() {
        switch(OSQAProductDao.listProducts()){
            case Result.Success<List<OSQAProduct>> (List<OSQAProduct> products) -> productComboBox.getItems().addAll(products);
            case Result.Failure<List<OSQAProduct>> failure -> IO.println(failure.error().getLocalizedMessage());
        }
    }
}
