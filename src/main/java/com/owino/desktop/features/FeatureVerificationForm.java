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
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.layout.VBox;
import com.owino.core.OSQAModel.OSQAVerification;
public class FeatureVerificationForm extends Dialog<String> {
    public FeatureVerificationForm(OSQAVerification verification, boolean isEditMode){
        var container = new VBox();
        setTitle(isEditMode ? "Update Verification" : "Feature Verification");
        var title = new Label(isEditMode ? "Update Fature Verification" : "New Feature Verification.");
        var description = new Label(isEditMode? "Update this feature verification criteria" : "Add single-step criteria to validate this feature during QA");
        title.setFont(Font.font(21));
        description.setFont(Font.font(15));
        var verificationDescTextArea = new TextArea();
        verificationDescTextArea.setPromptText("""
        Add a verification prompt...for example:
        When a user submits valid data, is the expected output generated immediately?
        """);
        verificationDescTextArea.setWrapText(true);
        verificationDescTextArea.setPrefRowCount(5);
        verificationDescTextArea.setPrefColumnCount(40);
        if (isEditMode) verificationDescTextArea.setText(verification.description());
        var okButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
        container.getChildren().add(title);
        container.getChildren().add(description);
        container.getChildren().add(verificationDescTextArea);
        VBox.setMargin(title, new Insets(12,12,6,12));
        VBox.setMargin(description, new Insets(6,12,6,12));
        VBox.setMargin(verificationDescTextArea, new Insets(12));
        getDialogPane().setContent(container);
        setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return verificationDescTextArea.getText();
            }
            return null;
        });
        ((Button) getDialogPane().lookupButton(okButtonType)).setOnAction(e -> close());
        ((Button) getDialogPane().lookupButton(ButtonType.CANCEL)).setOnAction(e -> close());
    }
}
