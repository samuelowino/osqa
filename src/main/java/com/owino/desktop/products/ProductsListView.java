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
import java.util.List;
import java.util.Optional;

import com.owino.core.Result;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import org.greenrobot.eventbus.EventBus;
import javafx.collections.ObservableList;
import com.owino.core.OSQAModel.OSQAProduct;
import com.owino.desktop.OSQANavigationEvents.OpenDashboardEvent;
import com.owino.desktop.OSQANavigationEvents.OpenProductFormEvent;
import com.owino.desktop.OSQANavigationEvents.OpenFeaturesListViewEvent;
public class ProductsListView extends VBox{
    private final ObservableList<OSQAProduct> productObservableList = FXCollections.observableArrayList();
    private final VBox noDataContainer = new VBox();
    private ListView<OSQAProduct> productsListView;
    public ProductsListView(){
        initView();
        initProducts();
    }
    private void initView() {
        var titleLabel = new Label("Products");
        titleLabel.setFont(Font.font(21));
        var noDataTitleLabel = new Label("Products list is empty");
        var newProductButton = new Button("Register Product");
        noDataTitleLabel.setFont(Font.font(18));
        noDataContainer.getChildren().add(noDataTitleLabel);
        noDataContainer.getChildren().add(newProductButton);
        noDataContainer.setAlignment(Pos.CENTER);
        VBox.setMargin(noDataContainer,new Insets(12));
        VBox.setMargin(noDataTitleLabel, new Insets(8));
        VBox.setMargin(newProductButton, new Insets(8));
        productsListView = new ListView<>(productObservableList);
        productsListView.setCellFactory(_ -> new ListCell<>(){
            @Override
            protected void updateItem(OSQAProduct product, boolean empty) {
                super.updateItem(product, empty);
                if (product == null || empty){
                    setText("");
                    setGraphic(null);
                } else {
                    var container = new VBox();
                    var nameLabel = new Label(product.name());
                    var detailedContainer = new HBox(12);
                    var targetLabel = new Label(product.target());
                    var dirLabel = new Label(product.projectDir().toAbsolutePath().toString());
                    var actionSection = new HBox();
                    var deleteButton = new Button("Delete");
                    var editButton = new Button("Edit");
                    var bottomPane = new BorderPane();
                    actionSection.getChildren().addAll(deleteButton,editButton);
                    nameLabel.setFont(Font.font(17));
                    targetLabel.setFont(Font.font(15));
                    dirLabel.setFont(Font.font(15));
                    deleteButton.setTextFill(Color.RED);
                    detailedContainer.getChildren().add(targetLabel);
                    detailedContainer.getChildren().add(dirLabel);
                    actionSection.setAlignment(Pos.BOTTOM_RIGHT);
                    bottomPane.setRight(actionSection);
                    bottomPane.setLeft(detailedContainer);
                    container.getChildren().add(nameLabel);
                    container.getChildren().add(bottomPane);
                    container.getChildren().add(new Separator());
                    var blueBackground = new Background(new BackgroundFill(Color.BLUE,new CornerRadii(12), new Insets(6,0,6,0)));
                    var blackBackground = new Background(new BackgroundFill(Color.BLACK,new CornerRadii(12), new Insets(6,0,6,0)));
                    container.setOnMouseEntered(_ -> container.setBackground(blueBackground));
                    container.setOnMouseExited(_ -> container.setBackground(blackBackground));
                    container.setBackground(blackBackground);
                    VBox.setMargin(nameLabel, new Insets(12,12,6,12));
                    VBox.setMargin(bottomPane, new Insets(6,12,12,12));
                    HBox.setMargin(deleteButton, new Insets(8));
                    HBox.setMargin(editButton, new Insets(8));
                    deleteButton.setOnAction(_ -> {
                        var deleteConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
                        deleteConfirmation.setContentText("Are your sure you want to delete this product?");
                        Optional<ButtonType> result = deleteConfirmation.showAndWait();
                        if (result.isPresent()){
                            if (result.get() == ButtonType.OK){
                                OSQAProductDao.delete(product);
                                initProducts();
                            }
                        }

                    });
                    editButton.setOnAction(_ -> EventBus.getDefault().post(new OpenProductFormEvent(true,product)));
                    setGraphic(container);
                }
            }
        });
        productsListView.getSelectionModel().selectedItemProperty().addListener((_,_,selectedProduct) -> {
            EventBus.getDefault().post(new OpenFeaturesListViewEvent(selectedProduct));
        });
        newProductButton.setOnAction(_ -> EventBus.getDefault().post(new OpenProductFormEvent()));
        productsListView.setBorder(Border.EMPTY);
        getChildren().add(titleLabel);
        getChildren().add(productsListView);
        VBox.setMargin(titleLabel, new Insets(12));
        VBox.setMargin(productsListView, new Insets(12));
        VBox.setVgrow(productsListView,Priority.ALWAYS);
    }
    private void initProducts() {
        switch (OSQAProductDao.listProducts()){
            case Result.Success<List<OSQAProduct>> (List<OSQAProduct> products) -> Platform.runLater(() -> {
                productObservableList.removeAll();
                productObservableList.clear();
                productObservableList.addAll(products);
                getChildren().remove(noDataContainer);
                setAlignment(Pos.TOP_LEFT);
                if (products.isEmpty()){
                    getChildren().remove(productsListView);
                    getChildren().add(noDataContainer);
                    setAlignment(Pos.CENTER);
                }
            });
            case Result.Failure<List<OSQAProduct>> failure -> Platform.runLater(() -> {
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("""
                    Failed to load products
                    Cause: %s
                    """.formatted(failure.error().getLocalizedMessage()));
                if (alert.showAndWait().isPresent()){
                    EventBus.getDefault().post(new OpenDashboardEvent());
                }
            });
        }
    }
}
