package org.project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.project.model.DLB; // 1. Model sınıfını import ettik

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // --- SÖZLÜK YÜKLEME İŞLEMİ (YENİ EKLENEN KISIM) ---

        // 1. Boş bir sözlük (Trie ağacı) oluştur
        DLB dlb = new DLB();

        // 2. Loader'ı çağır ve JSON dosyasını okuyup ağaca eklemesini söyle
        DictionaryLoader loader = new DictionaryLoader(dlb);
        loader.load();

        // --------------------------------------------------

        StackPane root = new StackPane();
        // Label yazısını güncelledik ki yüklendiğini arayüzde de hisset
        root.getChildren().add(new Label("Sözlük Yüklendi! Konsola bakabilirsin."));

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Smart Dictionary & Autocomplete");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}