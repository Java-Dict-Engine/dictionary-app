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
    
    // JSON'daki veriyi karşılayacak geçici bir sınıf (POJO)
    // JSON dosyasında "word" ve "meaning" alanları olduğu için buna uygun yaptık.
    private static class DictionaryItem {
        String word;
        String meaning;
    }

    public static void main(String[] args) {
        System.out.println("--- SÖZLÜK TESTİ BAŞLIYOR ---");

        DLB dictionary = new DLB();
        Gson gson = new Gson();

        try {
            // 1. Dosyayı Resources klasöründen oku
            InputStream inputStream = App.class.getClassLoader().getResourceAsStream("dictionary.json");
            
            if (inputStream == null) {
                System.err.println("HATA: dictionary.json dosyası bulunamadı!");
                return;
            }

            // 2. JSON verisini Listeye çevir
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            Type listType = new TypeToken<List<DictionaryItem>>(){}.getType();
            List<DictionaryItem> items = gson.fromJson(reader, listType);

            System.out.println("JSON'dan " + items.size() + " kelime okundu. Ağaca ekleniyor...");

            // 3. Okunan verileri DLB Trie yapısına ekle
            for (DictionaryItem item : items) {
                // Not: JSON'da 'type' alanı yok, o yüzden şimdilik null veya "Genel" gönderiyoruz.
                dictionary.add(item.word, item.meaning, "Genel"); 
            }

            System.out.println("Veri yükleme tamamlandı.\n");

            // 4. Test Sorguları (Ağaçtan veri çekme)
            testSearch(dictionary, "apple");       // Var olan kelime
            testSearch(dictionary, "banana");      // Var olan kelime
            testSearch(dictionary, "application"); // Prefix benzerliği olan kelime
            testSearch(dictionary, "araba");       // Olmayan kelime
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("\n--- TEST BİTTİ ---");
    }

    // Testi kolaylaştıran yardımcı metod
    private static void testSearch(DLB dlb, String word) {
        String definition = dlb.searchDefinition(word);
        if (definition != null) {
            System.out.println("✅ BULUNDU [" + word + "]: " + definition);
        } else {
            System.out.println("❌ BULUNAMADI [" + word + "]");
        }
    }
}