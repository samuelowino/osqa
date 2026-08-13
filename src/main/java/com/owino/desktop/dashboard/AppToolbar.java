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
import com.owino.core.Result;
import com.owino.core.UICommons;
import javafx.geometry.Insets;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.Node;
import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;
import static javafx.stage.PopupWindow.AnchorLocation.WINDOW_TOP_RIGHT;
import static javafx.stage.PopupWindow.AnchorLocation.WINDOW_TOP_LEFT;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import com.owino.desktop.OSQANavigationEvents.OpenDashboardEvent;
import com.owino.desktop.OSQANavigationEvents.OpenFeatureFormEvent;
import com.owino.desktop.OSQANavigationEvents.OpenProductFormEvent;
import com.owino.desktop.OSQANavigationEvents.ShowVerificationFormEvent;
import com.owino.desktop.OSQANavigationEvents.ResetVerificationsEvent;
import com.owino.desktop.OSQANavigationEvents.ToggleShowVerificationButtonEvent;
public class AppToolbar extends BorderPane {
    private final ToolBar rightMostContainer;
    private final Button addVerificationButton;
    private final Button resetVerificationButton;
    private final Stage window;
    private Popup hintMenu;
    private final Label tooltipLabel;
    public AppToolbar(Stage stage){
        hintMenu = new Popup();
        this.window = stage;
        var welcomeAscii = """
                 _____ _____  _____  ___
                |  _  /  ___||  _  |/ _ \\
                | | | \\ `--. | | | / /_\\ \\
                | | | |`--. \\| | | |  _  |
                \\ \\_/ /\\__/ /\\ \\/' / | | |
                 \\___/\\____/  \\_/\\_\\_| |_/
                """;
        var brandLabel = new Label(welcomeAscii);
        brandLabel.setFont(Font.font(4));
        brandLabel.setMaxHeight(22);
        addVerificationButton = new Button();
        var addFeatureButton = new Button();
        var homeButton = new Button();
        var addProductButton = new Button();
        if (UICommons.loadImage("addproductdark.png") instanceof
                Result.Success<Image> (var image)) {
            var addProductIcon = new ImageView(image);
            addProductIcon.setFitWidth(16);
            addProductIcon.setFitHeight(16);
            addProductButton.setGraphic(addProductIcon);
        } else {
            addProductButton.setText("New Product");
        }
        if (UICommons.loadImage("featureaddicon.png") instanceof
                Result.Success<Image> (var image)) {
            var addFeatureIcon = new ImageView(image);
            addFeatureIcon.setFitWidth(16);
            addFeatureIcon.setFitHeight(16);
            addFeatureButton.setGraphic(addFeatureIcon);
        } else {
            addFeatureButton.setText("New Feature");
        }
        if (UICommons.loadImage("homeicon.png") instanceof
                Result.Success<Image> (var image)) {
            var homeIcon = new ImageView(image);
            homeIcon.setFitWidth(16);
            homeIcon.setFitHeight(16);
            homeButton.setGraphic(homeIcon);
        } else {
            homeButton.setText("Home");
        }
	tooltipLabel = new Label();
	hintMenu.setAnchorLocation(WINDOW_TOP_RIGHT);
        hintMenu.getContent().add(tooltipLabel);
        addProductButton.setOnMouseEntered(_ -> {
	    	showTooltip("New Product", addProductButton);
        });
        homeButton.setOnMouseEntered(_ -> {
	    	showTooltip("Open Dashboard", homeButton);
        });
        addFeatureButton.setOnMouseEntered(_ -> {
	    	showTooltip("New Feature", addFeatureButton);
        });
        addProductButton.setOnMouseExited(_ -> hintMenu.hide());
        homeButton.setOnMouseExited(_ -> hintMenu.hide());
        addFeatureButton.setOnMouseExited(_ -> hintMenu.hide());

        resetVerificationButton = new Button("Reset");
        styleBrandLabel(brandLabel);
        rightMostContainer = new ToolBar();
        rightMostContainer.getItems().add(addFeatureButton);
        rightMostContainer.getItems().add(addProductButton);
        rightMostContainer.getItems().add(homeButton);
        HBox.setMargin(addFeatureButton, new Insets(6));
        HBox.setMargin(addProductButton, new Insets(6));
        HBox.setMargin(homeButton, new Insets(6));
        setRight(rightMostContainer);
        setLeft(brandLabel);
        setMargin(rightMostContainer,new Insets(22));
        setMargin(brandLabel,new Insets(6));
        homeButton.setOnAction(_ -> {
            EventBus.getDefault().post(new ToggleShowVerificationButtonEvent(false));
            EventBus.getDefault().post(new OpenDashboardEvent());
        });
        addFeatureButton.setOnAction(_ -> {
            EventBus.getDefault().post(new ToggleShowVerificationButtonEvent(false));
            EventBus.getDefault().post(new OpenFeatureFormEvent());
        });
        brandLabel.setOnMouseClicked(_ -> {
            EventBus.getDefault().post(new OpenDashboardEvent());
            EventBus.getDefault().post(new ToggleShowVerificationButtonEvent(false));

        });
        addProductButton.setOnMouseClicked(_ -> EventBus.getDefault().post(new OpenProductFormEvent()));
        addVerificationButton.setOnAction(_ -> EventBus.getDefault().post(new ShowVerificationFormEvent()));
        resetVerificationButton.setOnAction(_ -> EventBus.getDefault().post(new ResetVerificationsEvent()));
        EventBus.getDefault().register(this);
    }
    private void styleBrandLabel(Label brandLabel) {
        brandLabel.setFont(Font.font(21));
        brandLabel.setTextFill(Color.BLUE);
    }
    @Subscribe
    public void showVerificationButton(ToggleShowVerificationButtonEvent event){
        Platform.runLater(() -> {
            rightMostContainer.getItems().remove(addVerificationButton);
            rightMostContainer.getItems().remove(resetVerificationButton);
            if (event.show()) {
                rightMostContainer.getItems().add(addVerificationButton);
                rightMostContainer.getItems().add(resetVerificationButton);
                HBox.setMargin(addVerificationButton, new Insets(6));
                HBox.setMargin(resetVerificationButton, new Insets(6));
            }
        });
    }
    public void showTooltip(String msg, Node node) {
	tooltipLabel.setText(msg);
	var lb = node.getBoundsInLocal();
	var sb = node.localToScreen(lb);
	var YOffset = 20;
	var nodeYOffset = sb.getMaxY() + YOffset;
        hintMenu.show(node, sb.getMinX(), nodeYOffset);
    }

}
