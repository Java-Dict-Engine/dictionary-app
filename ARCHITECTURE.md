# 🏗️ Proje Mimarisi ve Algoritma Mantığı

Bu proje, standart Trie yapısı yerine bellek optimizasyonu sağlayan **De La Briandais (DLB) Trie** veri yapısını kullanır.

## 1. Neden DLB Trie? (Veri Yapısı Tercihi)
Standart bir Trie yapısında, her düğüm alfabedeki harf sayısı kadar (örneğin İngilizce için 26) boşluk tutan bir diziye (Array) sahiptir.
* **Sorun:** Kelime olmasa bile `null` değerler hafızada yer kaplar. Seyrek (sparse) verilerde bellek israfıdır.
* **Çözüm (DLB):** Dizi yerine **Bağlı Liste (Linked List)** mantığı kullanılır.
    * **Child (Çocuk):** Kelimenin bir sonraki harfine giden daldır.
    * **Sibling (Kardeş):** Aynı seviyedeki alternatif harflerdir.

> **Özet:** Sadece var olan harfler kadar yer kaplarız. "a" harfinden sonra sadece "b" geliyorsa, diğer 25 harf için yer ayrılmaz.

## 2. 'Suggest' (Öneri) Algoritması Nasıl Çalışır?
Otomatik tamamlama fonksiyonu (`suggest`), **Depth First Search (DFS)** ve **Backtracking (Geri İzleme)** yöntemlerini kullanır.

### Mantıksal Akış:
1.  **Prefix Bulma:** Önce kullanıcının girdiği ön ek (örn: "ba") ağaçta bulunur.
2.  **Toplama (Recursion):** O noktadan aşağıya sarkan tüm dallar gezilir.
3.  **Backtracking:**
    * Bir yola girilir (Child'a inilir).
    * Yol bitince geri dönülür.
    * **Kritik Nokta:** Geri dönerken, o yolda eklenen son harf silinir (`StringBuilder`'dan çıkarılır) ki algoritma yan kardeşe (Sibling) geçtiğinde temiz bir kelimeyle devam edebilsin.

### Performans Tercihi: StringBuilder
Algoritma içinde kelimeleri birleştirirken `String` yerine `StringBuilder` kullanılmıştır.
* **Sebep:** Java'da `String` değiştirilemez (immutable). Döngü içinde sürekli `+` operatörü kullanmak bellekte sürekli yeni nesneler yaratır ve Garbage Collector'ı yorar. `StringBuilder` ise tek bir nesne üzerinde çalışarak performansı artırır.

## 3. JSON Entegrasyonu
Veriler kodun içine gömülmek yerine (Hardcoded), `src/main/resources/dictionary.json` dosyasından dinamik olarak okunur.
* **Kütüphane:** Google Gson
* **Yapı:** Kelime (word) ve Anlam (meaning) alanlarını içeren JSON objeleri DLB ağacına parse edilir.
