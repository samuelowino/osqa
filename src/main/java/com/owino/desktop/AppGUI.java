package com.owino.desktop;
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
import atlantafx.base.theme.PrimerDark;
import com.owino.core.OSQAConfig;
import com.owino.core.Result;
import com.owino.desktop.dashboard.DashboardView;
import com.owino.desktop.dashboard.AppToolbar;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Separator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class AppGUI extends Application {
    public static void main(String[] args){
        launch(args);
    }
    @Override
    public void start(Stage stage) {
        setTheme();
        var mainAppWindow = new VBox();
        var toolbar = new AppToolbar();
        var dashboard = new DashboardView(stage);
        mainAppWindow.getChildren().add(toolbar);
        mainAppWindow.getChildren().add(new Separator());
        mainAppWindow.getChildren().add(dashboard);
        VBox.setVgrow(dashboard, Priority.ALWAYS);
        var scene = new Scene(mainAppWindow);
        stage.setScene(scene);
        stage.setMinHeight(900);
        stage.setMinWidth(1200);
        stage.setTitle("OSQA");
        stage.setFullScreen(false);
        stage.setOnShown(_ -> {
            switch(OSQAConfig.appInit()){
                case Result.Success<Void> _ -> IO.println("OSQA init was successful!");
                case Result.Failure<Void> error -> {
                    var alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText(
                        """
                        Failed to initialize application.
                        Internal system failure
                        %s
                        """.formatted(error.error().getLocalizedMessage()));
                    alert.getButtonTypes().add(ButtonType.CLOSE);
                    if(alert.showAndWait().isPresent()){
                        System.exit(0);
                    }
                }
            }
        });
        stage.show();
    }
    private void setTheme() {
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
    }
}