package org.project.model;

import java.util.LinkedList;

/**
 * Kullanıcının son aramalarını yöneten sınıf.
 * İş kuralı: En fazla 5 kelime tutar, mükerrer kayıtlarda kelimeyi en üste taşır.
 */
public class HistoryManager {
    private final LinkedList<String> historyList;
    private static final int MAX_SIZE = 5;

    public HistoryManager() {
        this.historyList = new LinkedList<>();
    }

    /**
     * Yeni bir kelimeyi geçmişe ekler.
     * @param word Eklenmek istenen kelime
     */
    public void addWord(String word) {
        if (word == null || word.trim().isEmpty()) return;

       
        if (historyList.contains(word)) {
            historyList.remove(word);
        } 
      
        else if (historyList.size() >= MAX_SIZE) {
            historyList.removeLast();
        }

        historyList.addFirst(word);
    }

    public LinkedList<String> getHistory() {
        return new LinkedList<>(historyList);
    }
}