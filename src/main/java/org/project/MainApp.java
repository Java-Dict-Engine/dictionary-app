package org.project;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.project.model.DLB;

public class MainApp extends Application {

    private TextField searchField;
    private ListView<String> suggestionList;
    private TextArea definitionArea;
    private Label definitionTitleLabel;
    private DLB dlb;

    @Override
    public void start(Stage primaryStage) {
        // --- 1. BACKEND (PARTNERİNİN KODLARIYLA UYUMLU) ---
        dlb = new DLB();
        DictionaryLoader loader = new DictionaryLoader(dlb);
        loader.load();

        // --- 2. ARAYÜZ ---
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f4f4;");
        root.setPadding(new Insets(20));

        // B. Üst Kısım
        VBox topContainer = new VBox(15);
        topContainer.setPadding(new Insets(0, 0, 20, 0));
        topContainer.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Smart Dictionary");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        titleLabel.setTextFill(Color.web("#2c3e50"));

        searchField = new TextField();
        searchField.setPromptText("Kelime aramaya başlayın...");
        searchField.setPrefHeight(45);
        searchField.setStyle(
                "-fx-background-radius: 15;" + "-fx-border-radius: 15;" +
                        "-fx-border-color: #bdc3c7;" + "-fx-padding: 0 15 0 15;" +
                        "-fx-font-size: 14px;"
        );

        topContainer.getChildren().addAll(titleLabel, searchField);
        root.setTop(topContainer);

        // C. Sol Kısım (Liste)
        VBox leftContainer = new VBox(10);
        Label listHeader = new Label("Sonuçlar");
        listHeader.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        listHeader.setTextFill(Color.web("#7f8c8d"));

        suggestionList = new ListView<>();
        suggestionList.setPrefWidth(280);
        suggestionList.setStyle(
                "-fx-background-radius: 10;" + "-fx-border-radius: 10;" +
                        "-fx-border-color: #ecf0f1;" + "-fx-control-inner-background: white;"
        );
        VBox.setVgrow(suggestionList, Priority.ALWAYS);

        leftContainer.getChildren().addAll(listHeader, suggestionList);
        root.setLeft(leftContainer);

        // D. Orta Kısım (Tanım)
        VBox centerContainer = new VBox(10);
        centerContainer.setPadding(new Insets(0, 0, 0, 20));

        definitionTitleLabel = new Label("Kelime Anlamı");
        definitionTitleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        definitionTitleLabel.setTextFill(Color.web("#2980b9"));
        definitionTitleLabel.setMaxHeight(Double.MAX_VALUE);

        definitionArea = new TextArea();
        definitionArea.setEditable(false);
        definitionArea.setWrapText(true);
        definitionArea.setText("Listeden bir kelime seçtiğinizde detaylar burada görünecek.");
        definitionArea.setStyle(
                "-fx-background-color: transparent;" + "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" + "-fx-border-color: #bdc3c7;" +
                        "-fx-font-family: 'Segoe UI';" + "-fx-font-size: 16px;"
        );
        VBox.setVgrow(definitionArea, Priority.ALWAYS);

        centerContainer.getChildren().addAll(definitionTitleLabel, definitionArea);
        root.setCenter(centerContainer);

        // --- ETKİLEŞİM ---

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                suggestionList.getItems().clear();
            } else {
                try {
                    // Partnerinin suggest metoduyla aynı isimde, sorun yok.
                    suggestionList.getItems().setAll(dlb.suggest(newValue));
                } catch (Exception e) {}
            }
        });

        suggestionList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // *** DÜZELTME BURADA YAPILDI ***
                // Partnerin metodunun adı: searchDefinition
                String meaning = dlb.searchDefinition(newValue);

                if (meaning != null) {
                    definitionTitleLabel.setText(newValue.substring(0, 1).toUpperCase() + newValue.substring(1));
                    definitionArea.setText(meaning);
                } else {
                    definitionArea.setText("Anlam bulunamadı.");
                }
            }
        });

        // --- SAHNE ---
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Smart Dictionary & Autocomplete");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}