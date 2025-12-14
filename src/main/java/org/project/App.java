package org.project;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.project.model.DLB;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class App {
    
    private static class DictionaryItem {
        String word;
        String meaning;
    }

    public static void main(String[] args) {
        System.out.println("--- SÖZLÜK VE TAMAMLAMA TESTİ ---");

        DLB dictionary = new DLB();
        Gson gson = new Gson();

        try {
            // 1. Dosyayı Oku
            InputStream inputStream = App.class.getClassLoader().getResourceAsStream("dictionary.json");
            if (inputStream == null) {
                System.err.println("HATA: dictionary.json bulunamadı!");
                return;
            }

            // 2. Verileri Yükle
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            Type listType = new TypeToken<List<DictionaryItem>>(){}.getType();
            List<DictionaryItem> items = gson.fromJson(reader, listType);

            for (DictionaryItem item : items) {
                dictionary.add(item.word, item.meaning, "Genel"); 
            }
            System.out.println("Veriler yüklendi (" + items.size() + " kelime).\n");

            // 3. SUGGEST TESTLERİ (Asıl Merak Ettiğimiz Kısım)
            System.out.println(">>> Test 1: 'ban' ile başlayanlar");
            testSuggest(dictionary, "ban"); 
            // Beklenen: banana, band

            System.out.println("\n>>> Test 2: 'app' ile başlayanlar");
            testSuggest(dictionary, "app"); 
            // Beklenen: apple, application

            System.out.println("\n>>> Test 3: 'z' ile başlayanlar (Olmayan)");
            testSuggest(dictionary, "z"); 
            // Beklenen: (Boş Liste)

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("\n--- TEST BİTTİ ---");
    }

    // Listeyi ekrana basan yardımcı metod
 // Listeyi ekrana basan ve HIZINI ÖLÇEN yardımcı metod
    private static void testSuggest(DLB dlb, String prefix) {
        // 1. KRONOMETREYİ BAŞLAT
        long startTime = System.nanoTime();

        // Asıl arama işlemi
        List<String> results = dlb.suggest(prefix);
        
        // 2. KRONOMETREYİ DURDUR
        long endTime = System.nanoTime();

        // 3. HESAPLA (Nanosaniye cinsinden)
        long duration = (endTime - startTime);
        
        // Okunabilir formata çevir (Milisaniye)
        double durationMs = duration / 1_000_000.0;

        // --- SONUÇLARI YAZDIR ---
        if (results.isEmpty()) {
            System.out.println("Sonuç bulunamadı: " + prefix);
        } else {
            for (String s : results) {
                System.out.println(" -> " + s);
            }
        }
        
        // PERFORMANS ÇIKTISI 
        System.out.println(String.format("[PERFORMANS] '%s' araması %.4f ms (%d ns) sürdü.", 
                prefix, durationMs, duration));
    }
    }
