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
import java.util.List;

public class MainApp extends Application {

    private TextField searchField;
    private ListView<String> suggestionList;
    private TextArea definitionArea;
    private Label definitionTitleLabel;
    private Label performanceLabel;
    private DLB dlb;

    // Listeden seçim yaparken aramanın tekrar tetiklenmesini engellemek için kontrol
    private boolean isUpdatingFromList = false;

    @Override
    public void start(Stage primaryStage) {
        // --- 1. BACKEND BAĞLANTISI ---
        dlb = new DLB();
        DictionaryLoader loader = new DictionaryLoader(dlb);
        loader.loadData();

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
        listHeader.setTextFill(Color.web("#2980b9"));

        // DÜZELTME: Rengi "Kelime Anlamı" başlığıyla aynı MAVİ (#2980b9) yaptık.
        performanceLabel = new Label("Süre: -");
        performanceLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
        performanceLabel.setTextFill(Color.web("#2980b9"));

        suggestionList = new ListView<>();
        suggestionList.setPrefWidth(280);
        suggestionList.setStyle(
                "-fx-background-radius: 10;" + "-fx-border-radius: 10;" +
                        "-fx-border-color: #ecf0f1;" + "-fx-control-inner-background: white;"
        );
        VBox.setVgrow(suggestionList, Priority.ALWAYS);

        leftContainer.getChildren().addAll(listHeader, performanceLabel, suggestionList);
        root.setLeft(leftContainer);

        // D. Orta Kısım (Tanım)
        VBox centerContainer = new VBox(10);
        centerContainer.setPadding(new Insets(0, 0, 0, 20));

        definitionTitleLabel = new Label("Kelime Anlamı");
        definitionTitleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        definitionTitleLabel.setTextFill(Color.web("#2980b9")); // Bu renkle eşledik
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
            // Eğer güncelleme listeden tıklama ile yapıldıysa tekrar arama yapma
            if (isUpdatingFromList) return;

            if (newValue == null || newValue.isEmpty()) {
                suggestionList.getItems().clear();
                performanceLabel.setText("Süre: -");
            } else {
                try {
                    long startTime = System.nanoTime();
                    List<String> results = dlb.suggest(newValue);

                    suggestionList.getItems().setAll(results);

                    long endTime = System.nanoTime();
                    double durationMs = (endTime - startTime) / 1_000_000.0;
                    String info = String.format("%d sonuç (%.4f ms)", results.size(), durationMs);
                    performanceLabel.setText(info);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        suggestionList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // GÖREV TAMAMLAMA: Tıklanan kelimeyi arama kutusuna yaz
                isUpdatingFromList = true;
                searchField.setText(newValue);
                isUpdatingFromList = false;

                // Anlamı bul ve göster
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