package org.project;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.project.model.DLB;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class DictionaryLoader {

    private final DLB dlb;

    public DictionaryLoader(DLB dlb) {
        this.dlb = dlb;
    }

    /**
     * resources/dictionary.json dosyasını okur ve DLB Trie yapısına yükler.
     */
    public void load() {
        try {
            // 1. Dosya Yolu: getResourceAsStream ile resources klasörüne erişim
            // Bu yöntem hem IDE içinde hem de JAR olarak paketlendiğinde çalışır.
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("dictionary.json");

            if (inputStream == null) {
                System.err.println("HATA: 'dictionary.json' dosyası resources altında bulunamadı!");
                return;
            }

            // 2. Okuma ve Gson Kurulumu
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            Gson gson = new Gson();

            // JSON bir liste ([...]) olduğu için TypeToken kullanıyoruz
            Type listType = new TypeToken<List<DictionaryEntry>>(){}.getType();
            List<DictionaryEntry> entries = gson.fromJson(reader, listType);

            // 3. Veri Ekleme: Okunan verileri DLB ağacına aktarma
            int count = 0;
            if (entries != null) {
                for (DictionaryEntry entry : entries) {
                    if (entry.word != null) {
                        // Partnerinin belirttiği add(word, meaning, type) yapısını kullanıyoruz.
                        // JSON'da 'type' yoksa null gidebilir veya varsayılan değer atayabilirsin.
                        String type = (entry.type != null) ? entry.type : "unknown";

                        dlb.add(entry.word.toLowerCase(), entry.meaning, type);
                        count++;
                    }
                }
            }

            System.out.println("Sözlük yüklendi. Toplam kelime: " + count);
            reader.close();

        } catch (IOException e) {
            System.err.println("Dosya okuma hatası (IO): " + e.getMessage());
        } catch (Exception e) {
            System.err.println("JSON parse hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // JSON verisini karşılayacak yardımcı sınıf (Inner Class)
    // JSON dosyasındaki anahtarlarla (keys) birebir aynı isimde olmalıdır.
    private static class DictionaryEntry {
        String word;
        String meaning;
        String type; // Partnerin belirttiği 'type' alanı için
    }
}