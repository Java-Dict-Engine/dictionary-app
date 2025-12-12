package org.project.model;
import java.util.ArrayList;
import java.util.List;

/**
 * De La Briandais (DLB) Trie veri yapısı.
 * Bu sınıf, kelimeleri ve tanımlarını ağaç yapısında saklar.
 * Standart Trie'den farkı, her düğümün child ve sibling (kardeş) mantığıyla bağlanmasıdır.
 */

public class DLB {
    
    private DLBNode root;
    
    public DLB() {
        // Kök düğüm boş bir başlatıcıdır
        this.root = new DLBNode();
    }
    /**
     * Sözlüğe yeni bir kelime, anlamı ve türünü ekler.
     * * @param word       Eklenecek kelime (örn: "computer")
     * @param definition Kelimenin anlamı
     * @param type       Kelimenin türü (örn: "noun")
     */
    
    //kelime ekleme metodu
    public void add(String word, String definition, String type) {
        if(word == null || word.isEmpty()) return;
        
        DLBNode currentNode = root;
        
        // Kelimenin her harfi için dön
        for(int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            
            // 1. ADIM: Mevcut düğümün çocuğuna bak (Alt seviyeye in)
            DLBNode child = currentNode.getChild();
            
            // Eğer hiç çocuk yoksa, hemen bu harfi çocuk olarak ekle
            if (child == null) {
                child = new DLBNode(c);
                currentNode.setChild(child);
            } 
            else {
                // Çocuğu var ama aradığımız harf mi? (Kardeşlerde ara)
                DLBNode prev = null;
                while (child != null && child.getData() != c) {
                    prev = child;
                    child = child.getSibling();
                }
                
                // Kardeşleri gezdik, harfi bulamadık -> Sona ekle
                if (child == null) {
                    child = new DLBNode(c);
                    prev.setSibling(child); // Öncekinin kardeşi yap
                }
            }
            
            // 2. ADIM: Artık harfi bulduk veya oluşturduk. Oraya ilerle.
            currentNode = child;
        }
        
        // Döngü bitti, currentNode şu an son harf. Bilgileri kaydet.
        currentNode.setWordEnd(true);
        currentNode.setDefinition(definition);
        currentNode.setType(type);
    }
    
    /**
     * Verilen kelimeyi arar ve varsa tanımını döndürür.
     * * @param word Aranacak kelime
     * @return Kelimenin tanımı (String). Kelime yoksa null döner.
     */
    
    // --- ARAMA METODU ---
    public String searchDefinition(String word) {
        if (word == null || word.isEmpty()) return null;

        DLBNode currentNode = root;

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            
            // Alt seviyeye in (Çocuklara bak)
            DLBNode child = currentNode.getChild();
            
            // Kardeşler arasında bu harfi ara
            while (child != null && child.getData() != c) {
                child = child.getSibling();
            }
            
            // Eğer harf bulunamadıysa, kelime yok demektir
            if (child == null) {
                return null;
            }
            
            // Harfi bulduk, bir sonraki harf için o düğüme geç
            currentNode = child;
        }
        
        // Kelime bitti. Burası bir kelime sonu mu?
        if (currentNode != null && currentNode.isWordEnd()) {
            return currentNode.getDefinition();
        }

        return null; // Kelime prefix olarak var ama tam kelime değil
    }
    /**
     * Verilen prefix (ön ek) ile başlayan tüm kelimeleri bulur.
     * Örn: "app" -> ["apple", "application", "apply"]
     * @param prefix Başlangıç harfleri
     * @return Kelime listesi
     */
    public List<String> suggest(String prefix) {
        List<String> suggestions = new ArrayList<>();
        if (prefix == null || prefix.isEmpty()) return suggestions;

        DLBNode currentNode = root;

        // 1. Adım: Prefix'in sonuna kadar ağaçta ilerle
        // (Bu kısım search mantığıyla aynı)
        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            DLBNode child = currentNode.getChild();
            
            while (child != null && child.getData() != c) {
                child = child.getSibling();
            }

            if (child == null) {
                return suggestions; // Prefix bile yoksa boş liste dön
            }
            currentNode = child;
        }

        // 2. Adım: Eğer prefix'in kendisi de bir kelimeyse listeye ekle
        // Örn: "pan" aradık, "pan" diye bir kelime de var.
        if (currentNode.isWordEnd()) {
            suggestions.add(prefix);
        }

        // 3. Adım: Bu noktadan sonraki tüm olasılıkları topla (Backtracking)
        // StringBuilder kullanarak performanslı string birleştirme yapıyoruz
        collectWords(currentNode.getChild(), new StringBuilder(prefix), suggestions);

        return suggestions;
    }

    /**
     * Yardımcı Metot (Recursive): Ağacın derinliklerine inip kelimeleri toplar.
     */
    private void collectWords(DLBNode node, StringBuilder currentWord, List<String> results) {
        if (node == null) return;

        DLBNode temp = node;

        // Mevcut seviyedeki tüm kardeşleri gez (Sibling Loop)
        while (temp != null) {
            // Harfi ekle
            currentWord.append(temp.getData());

            // Kelime bitti mi? Listeye ekle
            if (temp.isWordEnd()) {
                results.add(currentWord.toString());
            }

            // Aşağıya in (Child - Recursion)
            collectWords(temp.getChild(), currentWord, results);

            // Geri dön (Backtrack): Son eklediğimiz harfi silip yan kardeşe geçeceğiz
            currentWord.setLength(currentWord.length() - 1);

            // Yan kardeşe geç
            temp = temp.getSibling();
        }
    }
}