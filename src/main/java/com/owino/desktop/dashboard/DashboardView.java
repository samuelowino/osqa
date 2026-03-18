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
import com.owino.desktop.products.ProductsListView;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import com.owino.desktop.products.ProductFormView;
import com.owino.desktop.features.FeatureFormView;
import com.owino.desktop.features.FeatureDetailedView;
import com.owino.desktop.features.FeatureListingsView;
import com.owino.desktop.OSQANavigationEvents.OpenDashboardEvent;
import com.owino.desktop.OSQANavigationEvents.OpenProductFormEvent;
import com.owino.desktop.OSQANavigationEvents.OpenFeatureFormEvent;
import com.owino.desktop.OSQANavigationEvents.OpenProductsListEvent;
import com.owino.desktop.OSQANavigationEvents.OpenFeaturesListViewEvent;
import com.owino.desktop.OSQANavigationEvents.OpenFeatureDetailedViewEvent;
public class DashboardView extends SplitPane {
    private final Stage stage;
    public DashboardView(Stage stage){
        this.stage = stage;
        EventBus.getDefault().register(this);
        initView();
    }
    private void initView() {
        getItems().add(new MainMenuView());
        getItems().add(new WelcomeView());
        setOrientation(Orientation.HORIZONTAL);
        setDividerPositions(0.05f);
        setStyle("-fx-divider-color: #cccccc; -fx-divider-width: 1;");
    }
    @Subscribe
    public void handleHomeNavEvent(OpenDashboardEvent event){
        Platform.runLater(() -> {
            getItems().removeLast();
            getItems().add(new WelcomeView());
            setDividerPositions(0.05f);
        });
    }
    @Subscribe
    public void openFeatureFormEvent(OpenFeatureFormEvent event){
        Platform.runLater(() -> {
            getItems().removeLast();
            getItems().add(new FeatureFormView());
            setDividerPositions(0.05f);
        });
    }
    @Subscribe
    public void openFeatureDetailedViewEvent(OpenFeatureDetailedViewEvent event){
        getItems().removeLast();
        getItems().add(new FeatureDetailedView(event.selectedFeature(),event.product()));
        setDividerPositions(0.05f);
    }
    @Subscribe
    public void openFeaturesListViewEvent(OpenFeaturesListViewEvent event){
        getItems().removeLast();
        getItems().add(new FeatureListingsView(event.selectedProduct()));
        setDividerPositions(0.05f);
    }
    @Subscribe
    public void openProductFormEvent(OpenProductFormEvent event){
        getItems().removeLast();
        getItems().add(new ProductFormView(stage,event.product(),event.isEditMode()));
        setDividerPositions(0.05f);
    }
    @Subscribe
    public void openProductsListEvent(OpenProductsListEvent event){
        getItems().removeLast();
        getItems().add(new ProductsListView());
        setDividerPositions(0.05f);
    }
}
