package org.project;

import javafx.application.Application;
import javafx.application.Platform; // EKLENDİ: Hata düzeltmesi için gerekli
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.project.model.DLB;
import org.project.model.HistoryManager;

import java.util.List;

public class MainApp extends Application {

    private TextField searchField;
    private ListView<String> suggestionList;
    private TextArea definitionArea;
    private Label wordTitleLabel;
    private Label performanceLabel;
    private DLB dlb;
    private HistoryManager historyManager;
    private ListView<String> historyListView;

    private boolean isUpdatingFromList = false;

    @Override
    public void start(Stage primaryStage) {
        // --- 1. BACKEND ---
        dlb = new DLB();
        DictionaryLoader loader = new DictionaryLoader(dlb);
        loader.loadData();
        historyManager = new HistoryManager();

        // --- 2. LAYOUT ---
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(30));

        // --- A. Üst Kısım (Header + Arama) ---
        VBox topContainer = new VBox(20);
        topContainer.setAlignment(Pos.CENTER);
        topContainer.setPadding(new Insets(0, 0, 30, 0));

        Label titleLabel = new Label("Smart Dictionary");
        titleLabel.getStyleClass().add("app-title");

        searchField = new TextField();
        searchField.setPromptText("Search for a word...");
        searchField.setPrefHeight(50);
        searchField.setMaxWidth(600);
        searchField.getStyleClass().add("search-field");

        topContainer.getChildren().addAll(titleLabel, searchField);
        root.setTop(topContainer);

        // --- B. Sol Kısım (Yan Panel Kartı) ---
        VBox sidebarCard = new VBox(10);
        sidebarCard.getStyleClass().add("card");
        sidebarCard.setPrefWidth(300);
        sidebarCard.setMinWidth(250);

        // 1. Geçmiş Bölümü
        Label historyHeader = new Label("SEARCH HISTORY");
        historyHeader.getStyleClass().add("section-title");

        historyListView = new ListView<>();
        historyListView.setPrefHeight(150);
        historyListView.setPlaceholder(new Label("No History"));
        historyListView.getItems().setAll(historyManager.getHistory());

        // 2. Sonuçlar Bölümü
        HBox resultsHeaderBox = new HBox(10);
        resultsHeaderBox.setAlignment(Pos.CENTER_LEFT);
        Label listHeader = new Label("RESULTS");
        listHeader.getStyleClass().add("section-title");

        performanceLabel = new Label("");
        performanceLabel.getStyleClass().add("time-label");
        resultsHeaderBox.getChildren().addAll(listHeader, performanceLabel);

        suggestionList = new ListView<>();
        VBox.setVgrow(suggestionList, Priority.ALWAYS);

        sidebarCard.getChildren().addAll(historyHeader, historyListView, new Separator(), resultsHeaderBox, suggestionList);
        BorderPane.setMargin(sidebarCard, new Insets(0, 20, 0, 0));
        root.setLeft(sidebarCard);

        // --- C. Orta Kısım (Tanım Kartı) ---
        VBox definitionCard = new VBox(15);
        definitionCard.getStyleClass().add("card");
        definitionCard.setPadding(new Insets(30));

        wordTitleLabel = new Label("Definition");
        wordTitleLabel.getStyleClass().add("word-title");

        definitionArea = new TextArea();
        definitionArea.setEditable(false);
        definitionArea.setWrapText(true);
        definitionArea.setText("Select a word from the list to view details.");
        definitionArea.getStyleClass().add("definition-area");
        VBox.setVgrow(definitionArea, Priority.ALWAYS);

        definitionCard.getChildren().addAll(wordTitleLabel, definitionArea);
        root.setCenter(definitionCard);

        // --- ETKİLEŞİM VE OLAYLAR ---

        // 1. Arama yapıldığında (DÜZELTME UYGULANDI)
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isUpdatingFromList) return;

            if (newValue == null || newValue.isEmpty()) {
                suggestionList.getItems().clear();
                performanceLabel.setText("");
            } else {
                // Hesaplamayı yap
                long start = System.nanoTime();
                List<String> results = dlb.suggest(newValue);
                long end = System.nanoTime();

                // UI güncellemesini sıraya koy (Hata vermemesi için)
                Platform.runLater(() -> {
                    // Önceki seçimi temizle ki index hatası vermesin
                    suggestionList.getSelectionModel().clearSelection();

                    // Yeni listeyi yükle
                    suggestionList.getItems().setAll(results);

                    // Süreyi yaz
                    performanceLabel.setText(String.format("%.2f ms", (end - start) / 1e6));
                });
            }
        });

        // 2. Arama kutusunda ENTER
        searchField.setOnAction(e -> {
            String w = searchField.getText().trim();
            if(!w.isEmpty()) handleWordSelection(w);
        });

        // 3. Arama kutusunda AŞAĞI OK (DOWN)
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN && !suggestionList.getItems().isEmpty()) {
                suggestionList.requestFocus();
                suggestionList.getSelectionModel().selectFirst();
            }
        });

        // 4. Listede ENTER
        suggestionList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String selected = suggestionList.getSelectionModel().getSelectedItem();
                if (selected != null) handleWordSelection(selected);
            }
        });

        // 5. Mouse Tıklamaları
        suggestionList.getSelectionModel().selectedItemProperty().addListener((o, old, newVal) -> {
            if(newVal != null) handleWordSelection(newVal);
        });

        historyListView.getSelectionModel().selectedItemProperty().addListener((o, old, newVal) -> {
            if(newVal != null) handleWordSelection(newVal);
        });

        // --- SAHNE ---
        Scene scene = new Scene(root, 1000, 700);
        try {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("CSS Yüklenemedi! Dosya konumunu kontrol edin.");
        }

        primaryStage.setTitle("Smart Dictionary");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleWordSelection(String word) {
        if (word == null || word.isEmpty()) return;

        historyManager.addWord(word);
        historyListView.getItems().setAll(historyManager.getHistory());

        isUpdatingFromList = true;
        searchField.setText(word);
        isUpdatingFromList = false;

        wordTitleLabel.setText(word.substring(0, 1).toUpperCase() + word.substring(1));

        String meaning = dlb.searchDefinition(word);
        definitionArea.setText(meaning != null ? meaning : "Sorry, definition not found.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}