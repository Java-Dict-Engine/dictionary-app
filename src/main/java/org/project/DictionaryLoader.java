package org.project;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.project.model.DLB;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map; // List yerine Map import ettik

public class DictionaryLoader {

    private final DLB dlb;

    public DictionaryLoader(DLB dlb) {
        this.dlb = dlb;
    }

    /**
     * resources/dictionary.json dosyasını okur ve DLB Trie yapısına yükler.
     * YENİ FORMAT: {"kelime": "anlam"} şeklindeki Map yapısını okur.
     */
    public void loadData() {
        try {
            System.out.println("Sözlük yükleniyor... (Bu işlem dosya boyutuna göre 1-2 saniye sürebilir)");
            long start = System.currentTimeMillis();

            // 1. Dosya Yolu: getResourceAsStream ile resources klasörüne erişim
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("dictionary.json");

            if (inputStream == null) {
                System.err.println("HATA: 'dictionary.json' dosyası resources altında bulunamadı!");
                return;
            }

            // 2. Okuma ve Gson Kurulumu
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            Gson gson = new Gson();

            // DEĞİŞİKLİK BURADA:
            // JSON artık bir Liste ([...]) değil, bir Harita ({ "word": "def", ... })
            // Bu yüzden TypeToken'ı Map<String, String> yaptık.
            Type mapType = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> dictionaryMap = gson.fromJson(reader, mapType);

            // 3. Veri Ekleme: Okunan verileri DLB ağacına aktarma
            int count = 0;
            if (dictionaryMap != null) {
                for (Map.Entry<String, String> entry : dictionaryMap.entrySet()) {
                    String word = entry.getKey();       // Anahtar (Kelime)
                    String meaning = entry.getValue();  // Değer (Anlam)

                    if (word != null && meaning != null) {
                        // Yeni dosyada 'type' (isim/fiil) bilgisi yok, varsayılan olarak "General" diyoruz.
                        dlb.add(word.toLowerCase(), meaning, "General");
                        count++;
                    }
                }
            }

            long end = System.currentTimeMillis();
            System.out.println("✅ Sözlük başarıyla yüklendi.");
            System.out.println("📦 Toplam kelime: " + count);
            System.out.println("⏱️ Yükleme süresi: " + (end - start) + " ms");

            reader.close();

        } catch (IOException e) {
            System.err.println("Dosya okuma hatası (IO): " + e.getMessage());
        } catch (Exception e) {
            System.err.println("JSON parse hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // NOT: Eski 'DictionaryEntry' sınıfını sildik çünkü yeni JSON formatında
    // verilere doğrudan Map üzerinden (Key-Value) erişiyoruz.
}