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
import java.nio.file.Files;
import java.util.*;
import com.owino.core.OSQAModel;
import com.owino.core.OSQAVoid;
import com.owino.desktop.STYLES;
import com.owino.reports.OSQAXSSFTestingReport;
import javafx.geometry.Pos;
import com.owino.core.Result;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import com.owino.core.OSQAConfig;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.owino.core.OSQAModel.OSQAFeature;
import com.owino.core.OSQAModel.OSQAProduct;
import com.owino.desktop.OSQANavigationEvents.OpenFeatureFormEvent;
import com.owino.desktop.OSQANavigationEvents.OpenFeatureDetailedViewEvent;
import com.owino.core.OSQAModel.OSQAPaginatedResult;
import java.util.stream.Stream;
import static com.owino.core.OSQAModel.FeaturesSortOrder;
import static com.owino.desktop.features.ChangeColor.ChangeColorPair;
public class FeatureListingsView extends VBox {
    private final OSQAProduct product;
    private final ObservableList<OSQAFeature> listViewContents = FXCollections.observableArrayList();
    private final Text pageRangeLabel = new Text();
    private final Text totalItemsCountView = new Text();
    private final Button prevPageButton = new Button("Previous");
    private final Button nextPageButton = new Button("Next");
    private final Button testingFormsButton = new Button("Generate Testing Forms");
    private int currentPage = 1;
    private long totalPages = 1;
    private FeaturesSortOrder sortOrder = FeaturesSortOrder.BY_NAME;
    private final Stage window;
    private Pane featuresCountCard;
    private Pane passedVerificationsCard;
    private Pane activeVerificationsCard;
    private Pane systemStatusCard;
    private Pane pendingVerificationsCard;
    private HBox productStatusContainer = new HBox();
    public FeatureListingsView(OSQAProduct osqaProduct, Stage window){
        this.product = osqaProduct;
        this.window = window;
        var productHeaderContainer = new BorderPane();
        var productTitleLabel = new Label(product.name());
        var actionButtonsContainer = new HBox(12);
        productTitleLabel.setFont(Font.font(42));
        actionButtonsContainer.getChildren().add(testingFormsButton);
        productHeaderContainer.setLeft(productTitleLabel);
        productHeaderContainer.setRight(actionButtonsContainer);

        var featuresListViewHeaderView = new BorderPane();
        var featuresTitleLabel = new Label("Features Registry");
        featuresTitleLabel.setFont(Font.font(32));
        var sortOrderComboBox = new ComboBox<String>();
        sortOrderComboBox.getItems()
                .addAll(
                        Stream.of(FeaturesSortOrder.values())
                                .map(FeaturesSortOrder::getName)
                                .toList()
                );
        var selectedItemProperty = sortOrderComboBox.getSelectionModel().selectedItemProperty();
        selectedItemProperty.addListener((_,_,selectedSortOrder) -> {
            sortOrder = FeaturesSortOrder.fromName(selectedSortOrder);
            initFeatures();
        });
        featuresListViewHeaderView.setLeft(featuresTitleLabel);
        featuresListViewHeaderView.setRight(sortOrderComboBox);
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
                            var verificationStatusBackground = new Background(new BackgroundFill(Color.GREEN, STYLES.CARDS_CORNER_RADIUS,new Insets(12)));
                            verificationStatusLabel.setTextFill(Color.WHITE);
                            verificationStatusLabel.setBackground(verificationStatusBackground);
                            verificationStatusLabel.setFont(Font.font(12));
                            topSection.setRight(verificationStatusLabel);
                            BorderPane.setMargin(verificationStatusLabel, new Insets(4,0,0,12));
                        }
                        case Result.Failure<Long> failure -> IO.println("Failed to load verification progress: " + failure.error().getLocalizedMessage());
                    }

                    nameLabel.setFont(Font.font(22));
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
                    VBox.setMargin(topSection,new Insets(12));
                    VBox.setMargin(bottomSection,new Insets(12));
                    var blueBackground = new Background(new BackgroundFill(Color.BLUE,STYLES.CARDS_CORNER_RADIUS, STYLES.CARDS_INTERNAL_MARGIN));
                    var blackBackground = new Background(new BackgroundFill(Color.BLACK,STYLES.CARDS_CORNER_RADIUS, STYLES.CARDS_INTERNAL_MARGIN));
                    featureItemContainer.setOnMouseEntered(_ -> featureItemContainer.setBackground(blueBackground));
                    featureItemContainer.setOnMouseExited(_ -> featureItemContainer.setBackground(blackBackground));
                    featureItemContainer.setBackground(blackBackground);
                    deleteButton.setOnAction(_ -> deleteFeature(feature));
                    editButton.setOnAction(_ -> EventBus.getDefault().post(new OpenFeatureFormEvent(feature,true, window)));
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
        var pageSelectionView = new BorderPane();
        var pageSummaryView = new HBox(12);
        var pageButtonsView = new HBox();
        var showingLabelView = new Text("Showing");
        var ofLabelView = new Text("of");
        pageSummaryView.getChildren().add(showingLabelView);
        pageSummaryView.getChildren().add(pageRangeLabel);
        pageSummaryView.getChildren().add(ofLabelView);
        pageSummaryView.getChildren().add(totalItemsCountView);
        nextPageButton.setOnAction(_ -> {
            currentPage += 1;
            initFeatures();
        });
        prevPageButton.setOnAction(_ -> {
           currentPage -= 1;
           initFeatures();
        });
        pageButtonsView.getChildren().add(prevPageButton);
        pageButtonsView.getChildren().add(nextPageButton);
        HBox.setMargin(prevPageButton, new Insets(0,12,0,12));
        HBox.setMargin(nextPageButton, new Insets(0,12,0,12));
        pageSelectionView.setLeft(pageSummaryView);
        pageSelectionView.setRight(pageButtonsView);
        getChildren().add(productHeaderContainer);
        getChildren().add(productStatusContainer);
        getChildren().add(featuresListViewHeaderView);
        getChildren().add(featuresListView);
        getChildren().add(pageSelectionView);
        setMargin(productHeaderContainer,new Insets(12));
        setMargin(featuresListViewHeaderView,new Insets(12));
        setMargin(featuresListView,new Insets(12));
        setMargin(pageSelectionView, new Insets(12));
        VBox.setVgrow(featuresListView,Priority.ALWAYS);
        sortOrderComboBox.getSelectionModel().select(0);
        testingFormsButton.setOnAction(_ -> handleGenerateTestingForms());
        initProductStatusSummary();
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
            var pageSize = 5;
            if (currentPage > totalPages) return;
            var sorted = switch (sortOrder) {
                case FeaturesSortOrder.BY_NAME -> features.stream().
                        sorted(Comparator.comparing(OSQAFeature::name)).toList();
                case FeaturesSortOrder.BY_VERIFICATION_PROGRESS -> features.stream().
                        sorted(Comparator.comparing(OSQAConfig::verificationProgress, Long::compareTo))
                        .toList();
            };
            var paged = OSQAPaginatedResult.paginatedResult(sorted,currentPage,pageSize);
            totalPages = paged.totalPages();
            nextPageButton.setDisable(!paged.hasNext());
            prevPageButton.setDisable(!paged.hasPrevious());
            pageRangeLabel.setText(currentPage + " - " + totalPages);
            totalItemsCountView.setText(String.valueOf(paged.totalItems()));
            listViewContents.clear();
            listViewContents.addAll(paged.result());
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
    private void handleGenerateTestingForms(){
        var directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Testing Forms Destination Folder:");
        File selectedDir = directoryChooser.showDialog(window);
        if (selectedDir != null) {
            if (Files.exists(selectedDir.toPath())){
                var result = OSQAXSSFTestingReport.generateReport(selectedDir.toPath(), product);
                switch (result) {
                    case Result.Success<OSQAVoid> _ -> {
                        var alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success");
                        alert.setContentText("Testing forms generated successfully!");
                        alert.show();
                    }
                    case Result.Failure<OSQAVoid> failure -> {
                        var alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setContentText("Failed to generate testing forms\n" + failure.error().getLocalizedMessage());
                        alert.show();
                    }
                }
            } else {
                testingFormsButton.setTextFill(Color.RED);
            }
        } else {
            testingFormsButton.setTextFill(Color.RED);
        }
    }
    private void initProductStatusSummary() {
        var listFeaturesResult = OSQAConfig.listFeatures(product.projectDir());
        List<OSQAModel.OSQAVerification> verifications = new ArrayList<>();
        if (listFeaturesResult instanceof Result.Success<List<OSQAFeature>> (var features)) {
            for (OSQAFeature feature : features) {
                for (OSQAModel.OSQATestCase testCase : feature.testCases()) {
                    var loadTEstCaseResult = OSQAConfig.loadTestCaseSpec(testCase);
                    if (loadTEstCaseResult instanceof Result.Success<OSQAModel.OSQATestSpec> (var testSpec)) {
                        var verificationList = testSpec.verifications();
                        verifications.addAll(verificationList);
                        // TODO: Use spec file to load verifications
                        // TODO: work on refining this work flow in the next stream
                    }
                }
            }
        }
        var productStatus = OSQAConfig.productStatus(product.projectDir(),verifications);
        var minimumVerificationProgress = 90;
        var isStable = productStatus.systemStability() >= minimumVerificationProgress;
        var colorPair = isStable ?
                new ChangeColorPair(STYLES.OSQA_GREEN,STYLES.TRANSLUCENT_GREEN) :
                new ChangeColorPair(STYLES.OSQA_RED, STYLES.LIGHT_TRANSLUCENT_RED);
        var icon = isStable ? "upicon.png" : "horizontalline.png";
        var featuresCountDetails = new ProductSummaryCard(
                "Total Features",productStatus.featuresCount(),0,
                icon, String.format(Locale.getDefault(),
                "%d%% Verified",productStatus.systemStability()),
                colorPair);
        var activeVerifications = new ProductSummaryCard(
                "Active Checks",productStatus.allVerifications(),0,
                icon,
                String.format(Locale.getDefault(),"%d Pending", productStatus.failedVerifications()),
                colorPair);
        var passedVerifications = new ProductSummaryCard(
                "Passed Checks",productStatus.passedVerifications(),
                0,
                icon, isStable ? "Steady" : "Unstable",
                colorPair);
        var pendingVerifications = new ProductSummaryCard(
                "Pending Checks",productStatus.failedVerifications(),
                0,
                "horizontalline.png", isStable ? "Almost Done" : "Mostly Unstable",
                new ChangeColorPair(STYLES.OSQA_RED, STYLES.LIGHT_TRANSLUCENT_RED));
        var systemStability = new ProductSummaryCard(
                "System Stability",
                productStatus.systemStability(),
                0, icon,
                isStable ? "All features normal" : "Unstable System",
                colorPair, true);
        featuresCountCard = ProductSummaryCard.summaryView(featuresCountDetails);
        passedVerificationsCard = ProductSummaryCard.summaryView(passedVerifications);
        systemStatusCard = ProductSummaryCard.summaryView(systemStability);
        activeVerificationsCard = ProductSummaryCard.summaryView(activeVerifications);
        pendingVerificationsCard = ProductSummaryCard.summaryView(pendingVerifications);
        productStatusContainer.getChildren().removeAll();
        productStatusContainer.getChildren().add(featuresCountCard);
        productStatusContainer.getChildren().add(systemStatusCard);
        productStatusContainer.getChildren().add(activeVerificationsCard);
        productStatusContainer.getChildren().add(passedVerificationsCard);
        productStatusContainer.getChildren().add(pendingVerificationsCard);

        var summaryCardsMargin = new Insets(12);
        HBox.setMargin(featuresCountCard, summaryCardsMargin);
        HBox.setMargin(passedVerificationsCard, summaryCardsMargin);
        HBox.setMargin(activeVerificationsCard, summaryCardsMargin);
        HBox.setMargin(systemStatusCard, summaryCardsMargin);
    }
}
