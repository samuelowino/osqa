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
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.ArrayList;
import com.owino.core.Result;
import com.owino.desktop.CSS;
import javafx.geometry.Insets;
import javafx.scene.control.*;
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
    private static final Insets MARGIN = new Insets(6,22,12,22);
    private static final Insets FIELD_MARGIN = new Insets(4,12,2,12);
    private static final Insets LABEL_MARGIN = new Insets(12);
    private static final Font FORM_LABEL_FONT = Font.font(17);
    private final ComboBox<OSQAProduct> productComboBox = new ComboBox<>();
    private final VBox verificationListContainer = new VBox();
    private final List<OSQAVerification> verifications = new ArrayList<>();
    private TextArea userActionField;
    private TextField featureTitleTextField;
    public FeatureFormView(){
        var featureForm = initFeatureForm();
        setContent(featureForm);
        setFitToWidth(true);
        initProducts();
    }
    private VBox initFeatureForm() {
        var formContainer = new VBox();
        var header = new BorderPane();
        var titleText = new Text("New Feature");
        titleText.setFont(Font.font(21));
        var actionButtonsContainer = new HBox(22);
        var cancelButton = new Button("Cancel");
        actionButtonsContainer.getChildren().add(cancelButton);
        header.setLeft(titleText);
        header.setRight(actionButtonsContainer);
        var saveButton = new Button("Save");
        cancelButton.setOnAction(_ -> EventBus.getDefault().post(new OSQANavigationEvents.OpenDashboardEvent()));
        var featureDetailsContainer = new VBox();
        var productTitleLabel = new Label("Product");
        var featureTitleText = new Text("Name");
        featureTitleTextField = new TextField();
        var descriptionText = new Text("Description");
        var descriptionTextField = new TextField();
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
        addTestCaseForm(formContainer);
        saveButton.setOnAction(_ -> {
            var selectedProduct = productComboBox.getValue();
            if (selectedProduct == null) return;
            var testCaseTitle = "testcase";
            var appDir = selectedProduct.projectDir();
            var specFile = testCaseTitle + OSQAConfig.timestampedName(LocalDateTime.now(),"json");
            var filePath = appDir.toAbsolutePath().toString().concat(File.separator).concat(specFile);
            var testCase = new OSQATestCase(UUID.randomUUID().toString(),testCaseTitle,filePath);
            var specification = new OSQATestSpec(UUID.randomUUID().toString(),userActionField.getText(),verifications);
            OSQAConfig.writeSpecFile(appDir,specification,specFile);
            testCases.add(testCase);
            var featureTitle = featureTitleTextField.getText();
            var featureDescription = descriptionTextField.getText();
            var feature = new OSQAFeature(
                    UUID.randomUUID().toString(),
                    selectedProduct.uuid(),
                    featureTitle,
                    featureDescription,
                    "Critical",
                    testCases);
            OSQAConfig.writeFeature(appDir,feature);
            IO.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(feature));
            Alert successAlert = new Alert(Alert.AlertType.NONE);
            successAlert.setTitle("Success!");
            successAlert.setContentText("Feature created successfully!");
            successAlert.getButtonTypes().add(ButtonType.OK);
            if (successAlert.showAndWait().isPresent()){
                successAlert.close();
                EventBus.getDefault().post(new OSQANavigationEvents.OpenDashboardEvent());
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
    private void addTestCaseForm(VBox container) {
        var testCaseFormContainer = new VBox();
        var separator = new Separator();
        var userActionTitle = new Text("Usage instructions");
        userActionField = new TextArea();
        userActionTitle.setFont(FORM_LABEL_FONT);
        userActionField.setFont(Font.font(15));
        testCaseFormContainer.getChildren().add(userActionTitle);
        testCaseFormContainer.getChildren().add(userActionField);
        testCaseFormContainer.getChildren().add(separator);
        VBox.setMargin(userActionTitle,LABEL_MARGIN);
        VBox.setMargin(userActionField,FIELD_MARGIN);
        container.getChildren().add(testCaseFormContainer);
        VBox.setMargin(testCaseFormContainer,MARGIN);
        var verificationsContainer = new BorderPane();
        var verificationLabel = new Text("Verifications:");
        var addVerificationButton = new Button("Add Verification");
        verificationsContainer.setLeft(verificationLabel);
        verificationsContainer.setRight(addVerificationButton);
        verificationsContainer.setBottom(verificationListContainer);
        BorderPane.setMargin(verificationLabel,FIELD_MARGIN);
        verificationLabel.setFont(FORM_LABEL_FONT);
        var verificationSeparator = new Separator();
        testCaseFormContainer.getChildren().add(verificationsContainer);
        testCaseFormContainer.getChildren().add(verificationSeparator);
        testCaseFormContainer.setStyle(CSS.FORM_SECTION_BORDER);
        addVerificationButton.setOnAction(_ -> {
            Optional<String> inputResult = new FeatureVerificationForm().showAndWait();
            if (inputResult.isPresent()){
                if (!inputResult.get().isBlank()){
                    verifications.add( new OSQAVerification(UUID.randomUUID().toString(),0,inputResult.get()));
                    Alert successAlert = new Alert(Alert.AlertType.NONE);
                    successAlert.setTitle("Success");
                    successAlert.setContentText("""
                            Verification step
                            (%s)
                            has been added successfully.
                            This feature now has (%d) verification steps.
                            """.formatted(inputResult.get(),verifications.size()));
                    successAlert.getButtonTypes().add(ButtonType.OK);
                    successAlert.show();
                    var verificationCheckbox = new CheckBox(inputResult.get());
                    verificationListContainer.getChildren().add(verificationCheckbox);
                    VBox.setMargin(verificationCheckbox, new Insets(8));
                }
            }
        });
    }
    private void initProducts() {
        switch(OSQAProductDao.listProducts()){
            case Result.Success<List<OSQAProduct>> (List<OSQAProduct> products) -> productComboBox.getItems().addAll(products);
            case Result.Failure<List<OSQAProduct>> failure -> IO.println(failure.error().getLocalizedMessage());
        }
    }
}
