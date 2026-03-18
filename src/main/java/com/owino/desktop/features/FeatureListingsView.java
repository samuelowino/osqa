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
import java.util.List;
import java.util.Optional;
import javafx.geometry.Pos;
import com.owino.core.Result;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import com.owino.core.OSQAConfig;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.greenrobot.eventbus.EventBus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.owino.core.OSQAModel.OSQAFeature;
import com.owino.core.OSQAModel.OSQAProduct;
import com.owino.desktop.OSQANavigationEvents.OpenFeatureFormEvent;
import com.owino.desktop.OSQANavigationEvents.OpenFeatureDetailedViewEvent;
public class FeatureListingsView extends VBox {
    private final OSQAProduct product;
    private final ObservableList<OSQAFeature> listViewContents = FXCollections.observableArrayList();
    public FeatureListingsView(OSQAProduct osqaProduct){
        this.product = osqaProduct;
        var titleLabel = new Label("Product Features: (" + product.name() + ")");
        titleLabel.setFont(Font.font(21));
        var featuresListView = new ListView<>(listViewContents);
        featuresListView.setCellFactory(_ -> new ListCell<>(){
            @Override
            protected void updateItem(OSQAFeature feature, boolean empty) {
                super.updateItem(feature, empty);
                if (empty || feature == null){
                    setText("");
                    setGraphic(null);
                } else {
                    var featureItemContainer = new VBox(10);
                    var topSection = new BorderPane();
                    var buttonsContainer = new HBox();
                    var deleteButton = new Button("Delete");
                    var editButton = new Button("Edit");
                    deleteButton.setFont(Font.font(12));
                    editButton.setFont(Font.font(12));
                    deleteButton.setTextFill(Color.RED);
                    buttonsContainer.getChildren().addAll(deleteButton,editButton);
                    var nameLabel = new Label(feature.name());
                    switch (OSQAConfig.calculateFeatureVerificationProgress(feature)){
                        case Result.Success<Long> (Long progress) -> {
                            var verificationStatusLabel = new Label(progress + "%");
                            var verificationStatusBackground = new Background(new BackgroundFill(Color.GREEN, new CornerRadii(12),new Insets(12)));
                            verificationStatusLabel.setTextFill(Color.WHITE);
                            verificationStatusLabel.setBackground(verificationStatusBackground);
                            verificationStatusLabel.setFont(Font.font(12));
                            topSection.setRight(verificationStatusLabel);
                            BorderPane.setMargin(verificationStatusLabel, new Insets(4,0,0,12));
                        }
                        case Result.Failure<Long> failure -> IO.println("Failed to load verification progress: " + failure.error().getLocalizedMessage());
                    }

                    nameLabel.setFont(Font.font(17));
                    topSection.setLeft(nameLabel);
                    var descriptionLabel = new Label(feature.description());
                    descriptionLabel.setMaxWidth(700);
                    descriptionLabel.setWrapText(true);
                    var bottomSection = new BorderPane();
                    bottomSection.setRight(buttonsContainer);
                    bottomSection.setLeft(descriptionLabel);
                    HBox.setMargin(editButton, new Insets(0,8,0,8));
                    HBox.setMargin(deleteButton, new Insets(0,8,0,8));
                    featureItemContainer.getChildren().addAll(topSection, bottomSection);
                    VBox.setMargin(topSection,new Insets(6,12,0,12));
                    VBox.setMargin(bottomSection,new Insets(0,12,8,12));
                    var blueBackground = new Background(new BackgroundFill(Color.BLUE,new CornerRadii(12), new Insets(3,0,3,0)));
                    var blackBackground = new Background(new BackgroundFill(Color.BLACK,new CornerRadii(12), new Insets(3,0,3,0)));
                    featureItemContainer.setOnMouseEntered(_ -> featureItemContainer.setBackground(blueBackground));
                    featureItemContainer.setOnMouseExited(_ -> featureItemContainer.setBackground(blackBackground));
                    deleteButton.setOnAction(_ -> deleteFeature(feature));
                    setGraphic(featureItemContainer);
                }
            }
        });
        featuresListView.setBorder(Border.EMPTY);
        var featureSelectionModel = featuresListView.getSelectionModel();
        featureSelectionModel.setSelectionMode(SelectionMode.SINGLE);
        var featureSelectedItemProp = featureSelectionModel.selectedItemProperty();
        featureSelectedItemProp.addListener((_, _,selectedFeature) -> {
            if (selectedFeature != null){
                EventBus.getDefault().post(new OpenFeatureDetailedViewEvent(selectedFeature,product));
            }
        });
        getChildren().add(titleLabel);
        getChildren().add(featuresListView);
        setMargin(titleLabel,new Insets(12,12,12,12));
        setMargin(featuresListView,new Insets(12,12,12,12));
        VBox.setVgrow(featuresListView,Priority.ALWAYS);
        initFeatures();
    }
    private void initFeatures(){
        var appDir = product.projectDir();
        List<OSQAFeature> features = switch (OSQAConfig.listFeatures(appDir)){
            case Result.Success<List<OSQAFeature>> (List<OSQAFeature> featuresValue) -> featuresValue;
            case Result.Failure<List<OSQAFeature>> failure -> {
                IO.println("Failed to load feature list:" + failure.error().getLocalizedMessage());
                yield List.of();
            }
        };
        if (features.isEmpty()){
            var noDataViewLabel = new Label("Empty Features List");
            noDataViewLabel.setFont(Font.font(21));
            var addFeatureButton = new Button("Register New Feature");
            addFeatureButton.setOnAction(_ -> EventBus.getDefault().post(new OpenFeatureFormEvent()));
            setAlignment(Pos.CENTER);
            getChildren().add(noDataViewLabel);
            getChildren().add(addFeatureButton);
            VBox.setMargin(noDataViewLabel, new Insets(12));
            VBox.setMargin(addFeatureButton, new Insets(12));
        } else {
            listViewContents.clear();
            listViewContents.addAll(features);
        }
    }
    private void deleteFeature(OSQAFeature feature) {
        var confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setContentText("Are you sure you want to delete this feature?");
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            OSQAConfig.deleteFeature(feature);
            initFeatures();
        }
    }
}
