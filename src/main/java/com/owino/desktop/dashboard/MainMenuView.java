package com.owino.desktop.dashboard;
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
import com.owino.desktop.products.ProductSelectionDialog;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import com.owino.desktop.OSQANavigationEvents.OpenDashboardEvent;
import com.owino.desktop.OSQANavigationEvents.OpenProductsListEvent;
import com.owino.desktop.OSQANavigationEvents.OpenFeaturesListViewEvent;
import com.owino.desktop.OSQANavigationEvents.ToggleShowVerificationButtonEvent;
public class MainMenuView extends VBox {
    private static final Insets MENU_ITEM_MARGIN = new Insets(12,12,12,12);
    private Label dashboardLabel;
    private Label productsLabel;
    private Label featuresLabel;
    private final Stage stage;
    public MainMenuView(Stage window){
        this.stage = window;
        setMinWidth(200);
        setMaxWidth(300);
        initView();
    }
    private void initView() {
        dashboardLabel = new Label("Dashboard");
        productsLabel = new Label("Products");
        featuresLabel = new Label("Features");
        setMenuItemStyle(productsLabel);
        setMenuItemStyle(featuresLabel);
        setMenuItemStyle(dashboardLabel);
        dashboardLabel.setOnMouseClicked(_ -> {
            unhighlightEffect(productsLabel);
            unhighlightEffect(featuresLabel);
            EventBus.getDefault().post(new OpenDashboardEvent());
            EventBus.getDefault().post(new ToggleShowVerificationButtonEvent(false));
        });
        productsLabel.setOnMouseClicked(_ -> {
            highlightEffect(productsLabel);
            unhighlightEffect(featuresLabel);
            EventBus.getDefault().post(new ToggleShowVerificationButtonEvent(false));
            EventBus.getDefault().post(new OpenProductsListEvent(stage));
        });
        featuresLabel.setOnMouseClicked(_ -> {
            highlightEffect(featuresLabel);
            unhighlightEffect(productsLabel);
            EventBus.getDefault().post(new ToggleShowVerificationButtonEvent(false));
            var productSelectDialog = new ProductSelectionDialog();
            var result = productSelectDialog.showAndWait();
            result.ifPresent(selectedProduct -> EventBus.getDefault().post(new OpenFeaturesListViewEvent(selectedProduct, stage)));
        });
        getChildren().add(dashboardLabel);
        getChildren().add(productsLabel);
        getChildren().add(featuresLabel);
    }
    private void setMenuItemStyle(Label menuItem){
        menuItem.setFont(Font.font(17));
        setMargin(menuItem,MENU_ITEM_MARGIN);
    }
    private void highlightEffect(Label menuItem){
        menuItem.setFont(Font.font(20));
    }
    private void unhighlightEffect(Label menuItem){
        menuItem.setFont(Font.font(17));
    }
}
