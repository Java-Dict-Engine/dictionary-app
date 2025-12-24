package org.project;

import org.project.util.SortUtil;
import javafx.application.Application;
import javafx.application.Platform;
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

    // Arayüz Bileşenleri
    private TextField searchField;
    private ListView<String> suggestionList;
    private TextArea definitionArea;
    private Label wordTitleLabel;
    private Label performanceLabel;
    private ListView<String> historyListView;

    // Model ve Yardımcı Sınıflar
    private DLB dlb;
    private HistoryManager historyManager;

    // Listeden seçim yapıldığında tekrar aramayı tetiklememek için bayrak
    private boolean isUpdatingFromList = false;

    @Override
    public void start(Stage primaryStage) {
        // --- 1. BACKEND BAŞLATMA ---
        dlb = new DLB();
        
        // JSON verisi yükleme
        DictionaryLoader loader = new DictionaryLoader(dlb); 
        loader.loadData(); 
        
        historyManager = new HistoryManager();

        // --- 2. ARAYÜZ DÜZENİ (LAYOUT) ---
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(30));

        // --- A. Üst Kısım (Header + Arama Çubuğu) ---
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

        // --- B. Sol Kısım (Geçmiş ve Sonuçlar Paneli) ---
        VBox sidebarCard = new VBox(10);
        sidebarCard.getStyleClass().add("card");
        sidebarCard.setPrefWidth(300);
        sidebarCard.setMinWidth(250);

        // 1. Geçmiş (History)
        Label historyHeader = new Label("SEARCH HISTORY");
        historyHeader.getStyleClass().add("section-title");

        historyListView = new ListView<>();
        historyListView.setPrefHeight(150);
        historyListView.setPlaceholder(new Label("No History"));
        historyListView.getItems().setAll(historyManager.getHistory());

        // 2. Sonuçlar (Results)
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

        // --- 3. ETKİLEŞİM VE OLAYLAR (EVENTS) ---

        // 3.1. Arama kutusuna yazı yazıldığında (DÜZELTİLMİŞ KISIM)
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Eğer güncelleme kod tarafından yapılıyorsa (listeden seçince), arama yapma
            if (isUpdatingFromList) return; 

            if (newValue == null || newValue.isEmpty()) {
                suggestionList.getItems().clear();
                performanceLabel.setText("");
            } else {
                long start = System.nanoTime();
                
                // 1. DLB'den veriyi çek
                List<String> results = dlb.suggest(newValue); 
                
                // 2. SIRALAMA (SortUtil Entegrasyonu)
                // "LENGTH" -> Önce kısa kelimeler, sonra uzunlar (MergeSort)
                // "AZ" -> Alfabetik (QuickSort)
                SortUtil.sort(results, "LENGTH"); 

                long end = System.nanoTime();

                // UI Güncellemesi (Main Thread'de çalışmalı)
                Platform.runLater(() -> {
                    suggestionList.getSelectionModel().clearSelection();
                    suggestionList.getItems().setAll(results);
                    performanceLabel.setText(String.format("%.2f ms", (end - start) / 1e6));
                });
            }
        });

        // 3.2. Arama kutusunda ENTER
        searchField.setOnAction(e -> {
            String w = searchField.getText().trim();
            if(!w.isEmpty()) handleWordSelection(w);
        });

        // 3.3. Arama kutusunda AŞAĞI OK (Listeye odaklan)
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN && !suggestionList.getItems().isEmpty()) {
                suggestionList.requestFocus();
                suggestionList.getSelectionModel().selectFirst();
            }
        });

        // 3.4. Sonuç Listesinde ENTER
        suggestionList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String selected = suggestionList.getSelectionModel().getSelectedItem();
                if (selected != null) handleWordSelection(selected);
            }
        });

        // 3.5. Fare Tıklamaları (Sonuç Listesi)
        suggestionList.getSelectionModel().selectedItemProperty().addListener((o, old, newVal) -> {
            if(newVal != null) handleWordSelection(newVal);
        });

        // 3.6. Fare Tıklamaları (Geçmiş Listesi)
        historyListView.getSelectionModel().selectedItemProperty().addListener((o, old, newVal) -> {
            if(newVal != null) handleWordSelection(newVal);
        });

        // --- 4. SAHNE AYARLARI ---
        Scene scene = new Scene(root, 1000, 700);
        
        try {
            if (getClass().getResource("/style.css") != null) {
                scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            } else {
                System.err.println("Uyarı: style.css dosyası resources kök dizininde bulunamadı.");
            }
        } catch (Exception e) {
            System.err.println("CSS yüklenirken hata oluştu: " + e.getMessage());
        }

        primaryStage.setTitle("Smart Dictionary");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Bir kelime seçildiğinde yapılacak işlemler:
     * 1. Geçmişe ekle
     * 2. Arama kutusunu güncelle
     * 3. Tanımı getir ve göster
     */
    private void handleWordSelection(String word) {
        if (word == null || word.isEmpty()) return;

        // Geçmişi güncelle
        historyManager.addWord(word);
        historyListView.getItems().setAll(historyManager.getHistory());

        // Arama kutusunu güncelle (Listener'ı tetiklemeden)
        isUpdatingFromList = true;
        searchField.setText(word);
        isUpdatingFromList = false;

        // Başlık ve Tanımı güncelle
        wordTitleLabel.setText(word.substring(0, 1).toUpperCase() + word.substring(1));

        String meaning = dlb.searchDefinition(word);
        definitionArea.setText(meaning != null ? meaning : "Sorry, definition not found.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}