# 📚 Java Based Smart Autocomplete & Dictionary (Akıllı Sözlük)

![Java](https://img.shields.io/badge/Java-17%2B-orange) ![Maven](https://img.shields.io/badge/Maven-3.x-blue) ![JavaFX](https://img.shields.io/badge/JavaFX-GUI-green) ![License](https://img.shields.io/badge/License-MIT-lightgrey)

> **Veri Yapıları ve Algoritmalar Dersi Final Projesi**
>
> *Büyük veri setleri üzerinde milisaniyeler içinde arama yapabilen, bellek dostu ve yüksek performanslı sözlük uygulaması.*

## 🚀 Proje Hakkında

[cite_start]Bu proje, geleneksel veri yapılarının (Standart Trie veya Diziler) büyük kelime havuzlarında yarattığı bellek israfını ve performans sorunlarını çözmek amacıyla geliştirilmiştir [cite: 40-42]. [cite_start]Projenin temel amacı, **De La Briandais (DLB) Trie** mimarisini kullanarak "seyrek" (sparse) veri setlerinde bellek kullanımını minimize etmek ve arama hızını maksimize etmektir [cite: 44-46].

Uygulama, kullanıcının girdiği harflere göre anlık otomatik tamamlama (autocomplete) sunar ve sonuçları **O(L)** (Kelime Uzunluğu) zaman karmaşıklığında getirir.

---

## ✨ Temel Özellikler

* **⚡ Yüksek Performans:** Arama hızı toplam veri boyutundan bağımsızdır; sadece aranan kelimenin uzunluğuna bağlıdır.
* [cite_start]**🧠 Bellek Optimizasyonu:** DLB Trie mimarisi ve "Bağlı Liste" (Linked-List) mantığı sayesinde `null pointer` israfı önlenmiştir[cite: 52].
* [cite_start]**🔍 Akıllı Otomatik Tamamlama:** DFS (Derinlik Öncelikli Arama) ve Backtracking algoritmaları ile eksiksiz öneri sistemi [cite: 53-54].
* **📂 Dinamik Veri Yönetimi:** Veriler kod içine gömülü değildir; [cite_start]`dictionary.json` dosyasından dinamik olarak yüklenir (Gson ile)[cite: 55].
* [cite_start]**🎨 Modern Arayüz:** JavaFX ile geliştirilmiş, kullanıcı dostu grafik arayüz (GUI)[cite: 56].
* [cite_start]**📊 Sıralama Seçenekleri:** Sonuçları **MergeSort** (Uzunluğa göre - Stable) veya **QuickSort** (Alfabetik) algoritmalarıyla sıralama imkanı [cite: 88-92].

---

## 🛠️ Mimari ve Algoritmalar

### 1. De La Briandais (DLB) Trie
Standart Trie yapısında her düğümde alfabe boyutu kadar (örn: 26) dizi tutulur, bu da boş alan israfına yol açar. [cite_start]Projemizde kullandığımız DLB Trie ise **Bağlı Liste (Linked List)** mantığıyla çalışır [cite: 71-74].

* [cite_start]**Child (Çocuk):** Kelimeyi tamamlamak için bir alt seviyeye iner[cite: 76].
* [cite_start]**Sibling (Kardeş):** Aynı seviyedeki alternatif harfleri tutar[cite: 77].

![DLB Trie Diagram](https://raw.githubusercontent.com/Java-Dict-Engine/dictionary-app/main/docs/dlb_structure.png)
*(Şematik gösterim: Standart Trie vs DLB Trie)*

### 2. Arama ve Suggest Algoritması
Kullanıcı bir ön ek (prefix) girdiğinde:
1.  Ön ek ağaç üzerinde takip edilir.
2.  Son düğümden itibaren **DFS** ile aşağı inilir.
3.  [cite_start]**StringBuilder** kullanılarak kelimeler birleştirilir (Bellek yönetimi optimizasyonu)[cite: 86].
4.  [cite_start]Geri dönerken **Backtracking** uygulanır[cite: 87].

---

## ⚙️ Kullanılan Teknolojiler

| Teknoloji | Açıklama |
| :--- | :--- |
| **Java 17** | [cite_start]Ana programlama dili [cite: 61] |
| **Maven** | [cite_start]Bağımlılık yönetimi ve derleme [cite: 64] |
| **JavaFX** | [cite_start]Grafiksel Kullanıcı Arayüzü (GUI) [cite: 65] |
| **Google Gson** | [cite_start]JSON Parsing ve Veri Yükleme [cite: 66] |

---

## 💻 Kurulum ve Çalıştırma

[cite_start]Projeyi yerel makinenizde çalıştırmak için aşağıdaki adımları izleyin[cite: 170]:

### Gereksinimler
* Java JDK 17 veya üzeri
* Apache Maven 3.x

### Adım 1: Projeyi Klonlayın
```bash
git clone [https://github.com/Java-Dict-Engine/dictionary-app.git](https://github.com/Java-Dict-Engine/dictionary-app.git)
cd dictionary-app
```

### Adım 2: Bağımlılıkları İndirin ve Çalıştırın

Terminal veya komut satırında proje dizinindeyken:

```bash
mvn clean javafx:run
```

## 📂 Proje Yapısı

Proje, **MVC (Model-View-Controller)** prensiplerine uygun olarak organize edilmiştir. Dosya hiyerarşisi aşağıdaki gibidir:
```bash
src/main/java/org/project
├── model/              # Veri Yapıları (Backend) [cite: 188]
│   ├── DLB.java        # Trie Ağacı ve Algoritmalar [cite: 189]
│   ├── DLBNode.java    # Düğüm Yapısı [cite: 190]
│   └── HistoryManager.java
├── util/               # Yardımcı Araçlar [cite: 193]
│   └── SortUtil.java   # QuickSort ve MergeSort [cite: 194]
├── DictionaryLoader.java # JSON Veri Yükleyici (ETL) [cite: 197]
├── MainApp.java        # JavaFX Controller [cite: 199]
└── resources/          # Kaynak Dosyalar [cite: 202]
    ├── dictionary.json # Veri Seti [cite: 203]
    └── style.css       # Tasarım Dosyası [cite: 204]
```
## 📊 Performans Analizi

[cite_start]Test sonuçlarına göre sistemin zaman ve alan karmaşıklığı analizi şöyledir[cite: 96]:

| Operasyon | Veri Yapısı | Zaman Karmaşıklığı | Açıklama |
| :--- | :--- | :--- | :--- |
| **Ekleme (Insert)** | DLB Trie | **O(L)** | [cite_start]L: Kelime Uzunluğu [cite: 97] |
| **Arama (Suggest)** | DLB Trie | **O(L)** | [cite_start]Veri setinden bağımsız [cite: 97] |
| **Sıralama** | Merge/Quick Sort | **O(N log N)** | [cite_start]N: Sonuç sayısı [cite: 97] |

> [cite_start]**Test Sonucu:** "Suggest" algoritmasının tepki süresi ortalama **0.5ms** altındadır[cite: 139].

---

## 🎥 Demo Video

[cite_start]Projenin çalışma mantığını ve performansını gösteren demo videosuna aşağıdaki bağlantıdan ulaşabilirsiniz[cite: 131]:

[▶️ Proje Demo Videosunu İzle](https://drive.google.com/file/d/12_8Ywhw_Ld9s65TBKPDQB4HdxbgCm3DG/view?usp=sharing)

---

## 👥 Ekip

* [cite_start]**Zeynep TOPAL** (24120205054) [cite: 5]
* [cite_start]**Hatice Sude POLAT** (24120205032) [cite: 6]

[cite_start]**Ders:** Veri Yapıları ve Algoritmalar [cite: 3]
**Danışman:** Dr. Öğr. [cite_start]Üyesi Muhammet Sinan BAŞARSLAN [cite: 8]
[cite_start]**Kurum:** İstanbul Medeniyet Üniversitesi [cite: 1]

---

## 🔗 Kaynaklar ve Teşekkür

* Bu projede kullanılan sözlük verisi Matthew Reagan'ın *WebstersEnglishDictionary* reposundan alınmıştır.
* De La Briandais, R. (1959). [cite_start]*File Searching Using Variable Length Keys*[cite: 155].