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
import com.owino.core.Result;
import com.owino.core.UICommons;
import com.owino.desktop.STYLES;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import static com.owino.desktop.features.ChangeColor.ChangeColorPair;
public record ProductSummaryCard(
        String titleLabel,
        int count,
        int progress,
        String progressIcon,
        String progressDescription,
        ChangeColorPair progressColors,
        boolean togglePercentage
) {
    public ProductSummaryCard(String title,
                              int count, int progress,
                              String icon,String desc,
                              ChangeColorPair colorPair) {
        this(title,count,progress,icon,desc,colorPair,false);
    }
    static Pane summaryView(ProductSummaryCard card) {
        var progressCountCard = new BorderPane();
        var progressCountBox = new VBox();
        var progressCountTitleLabel = new Label(card.titleLabel());
        var countText = String.format(card.togglePercentage() ? "%,d%%" : "%,d", card.count());
        var progressCountLabel = new Label(countText);
        progressCountBox.getChildren().add(progressCountTitleLabel);
        progressCountBox.getChildren().add(progressCountLabel);
        var progressDescContainer = new HBox();
        var progressIcon = new ImageView();
        if (UICommons.loadImage(card.progressIcon()) instanceof Result.Success<Image> (Image upIcon)) {
            var cardIconSize = 12;
            progressIcon.setImage(upIcon);
            progressIcon.setFitHeight(cardIconSize);
            progressIcon.setFitWidth(cardIconSize);
        } else {
            IO.println("Failed to load upIcon image 🔴");
        }
        var progressLabel = new Label(card.progressDescription);
        progressLabel.setFont(Font.font("", FontWeight.MEDIUM, 12));
        var monthlyChangeBackground = new BackgroundFill(
                card.progressColors.backgroundColor(),
                STYLES.CARDS_CORNER_RADIUS,
                STYLES.CARDS_INTERNAL_MARGIN
        );
        progressDescContainer.setBackground(new Background(monthlyChangeBackground));
        progressLabel.setTextFill(card.progressColors().textColor());
        progressDescContainer.getChildren().add(progressIcon);
        progressDescContainer.getChildren().add(progressLabel);
        HBox.setMargin(progressIcon, new Insets(8,0,2,6));
        HBox.setMargin(progressLabel, new Insets(6,6,6,4));
        var imageResult = UICommons.loadImage("testImage");
        var featuresCardIcon = new ImageView();
        if (imageResult instanceof Result.Success<Image> (Image iconImage)) {
            featuresCardIcon.setImage(iconImage); //TODO: this can be loaded async and set later
        }
        progressCountTitleLabel.setFont(Font.font("", FontWeight.NORMAL, 17));
        progressCountTitleLabel.setTextFill(Color.GRAY);
        progressCountLabel.setTextFill(Color.WHITE);
        progressCountLabel.setFont(Font.font("",42));
        var featuresSummaryContainer = new VBox();
        featuresSummaryContainer.getChildren().add(progressCountBox);
        featuresSummaryContainer.getChildren().add(progressDescContainer);
        var summaryCardContentsMargin = new Insets(12,22,12,22);
        VBox.setMargin(progressCountBox, summaryCardContentsMargin);
        VBox.setMargin(progressDescContainer, summaryCardContentsMargin);
        progressCountCard.setLeft(featuresSummaryContainer);
        progressCountCard.setRight(featuresCardIcon);
        var summaryCardBackground = new Background(new BackgroundFill(
                Color.BLACK,
                new CornerRadii(12),
                new Insets(3,0,3,0)
        ));
        progressCountCard.setBackground(summaryCardBackground);
        return progressCountCard;
    }
}
