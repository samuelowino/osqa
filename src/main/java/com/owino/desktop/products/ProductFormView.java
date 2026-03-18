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
import java.util.UUID;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.stage.Stage;
import com.owino.core.Result;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import com.owino.core.OSQAModel.OSQAProduct;
import org.greenrobot.eventbus.EventBus;
import com.owino.desktop.OSQANavigationEvents.OpenProductsListEvent;
public class ProductFormView extends VBox {
    private Path projectDir;
    private final Insets MARGIN = new Insets(12,6,12,22);
    private final Insets FIELD_MARGIN = new Insets(6,6,6,6);
    private TextField nameField = new TextField();
    private OSQAProduct product;
    public ProductFormView(Stage window, OSQAProduct editModeProduct, boolean isEditMode){
        this.product = editModeProduct;
        var formTitle = new Label("Create new product");
        var nameLabel = new Label("Product Name");
        var targetLabel = new Label("Deployment Target");
        var targetPicker = new ComboBox<String>();
        var projectDirButton = new Button("Set Project Dir");
        var saveButton = new Button(isEditMode ? "Apply Changes" : "Save");
        var selectedDirLabel = new Label();
        targetPicker.getItems().addAll("iOS","Android","macOS","Windows","Linux");
        targetPicker.setEditable(true);
        saveButton.setMinWidth(300);
        if (isEditMode){
            nameField.setText(product.name());
            targetPicker.getSelectionModel().select(editModeProduct.target());
            selectedDirLabel.setText(product.projectDir().toAbsolutePath().toString());
            projectDir = product.projectDir();
        }
        var nameContainer = new HBox();
        var targetContainer = new HBox();
        formTitle.setFont(Font.font(22));
        nameLabel.setFont(Font.font(22));
        targetLabel.setFont(Font.font(22));
        nameContainer.getChildren().add(nameLabel);
        nameContainer.getChildren().add(nameField);
        targetContainer.getChildren().add(targetLabel);
        targetContainer.getChildren().add(targetPicker);
        getChildren().add(formTitle);
        getChildren().add(nameContainer);
        getChildren().add(targetContainer);
        getChildren().add(projectDirButton);
        getChildren().add(selectedDirLabel);
        getChildren().add(saveButton);
        HBox.setMargin(nameLabel,FIELD_MARGIN);
        HBox.setMargin(nameField,FIELD_MARGIN);
        HBox.setMargin(targetLabel,FIELD_MARGIN);
        HBox.setMargin(targetPicker,FIELD_MARGIN);
        VBox.setMargin(formTitle,MARGIN);
        VBox.setMargin(nameContainer,MARGIN);
        VBox.setMargin(targetContainer,MARGIN);
        VBox.setMargin(projectDirButton,MARGIN);
        VBox.setMargin(selectedDirLabel,MARGIN);
        VBox.setMargin(saveButton,MARGIN);
        projectDirButton.setOnAction(_ -> {
            var directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Project Direction:");
            File selectedDir = directoryChooser.showDialog(window);
            if (selectedDir != null) {
                if (Files.exists(selectedDir.toPath())){
                    projectDir = selectedDir.toPath();
                    selectedDirLabel.setText(projectDir.toAbsolutePath().toString());
                } else {
                    projectDirButton.setTextFill(Color.RED);
                }
            } else {
                projectDirButton.setTextFill(Color.RED);
            }
        });
        saveButton.setOnAction(_ -> {
            if (nameField.getText().isBlank()){
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Missing project name");
                alert.showAndWait();
                return;
            }
            if (targetPicker.getValue() == null || targetPicker.getValue().isBlank()){
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Select deployment target");
                alert.showAndWait();
                return;
            }
            if (projectDir != null) {
                product = new OSQAProduct(isEditMode ? product.uuid() : UUID.randomUUID().toString(), nameField.getText(), targetPicker.getValue(), projectDir);
                var saveOrUpdateResult = isEditMode ? OSQAProductDao.updateProduct(product): OSQAProductDao.saveProduct(product);
                switch (saveOrUpdateResult){
                    case Result.Success<Void> _ -> {
                        var alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText("Product has been saved successfully");
                        if (alert.showAndWait().isPresent()){
                            EventBus.getDefault().post(new OpenProductsListEvent());
                        }
                    }
                    case Result.Failure<Void> failure -> {
                        var alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("""
                                Failed to save product.
                                %s
                                """.formatted(failure.error().getLocalizedMessage()));
                        alert.showAndWait();
                    }
                }
            } else {
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("""
                                Missing project dir.
                                All OSQA output files will be written to the project directory or your preferred destination folder.
                                """);
                alert.showAndWait();
            }
        });
    }
}
