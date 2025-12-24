package org.project.util;

import java.util.ArrayList;
import java.util.List;

public class SortUtil {

    // MainApp'ten çağıracağımız TEK metod bu olacak.
    // Algoritmayı seçimi burada yapılır.
    public static void sort(List<String> list, String strategy) {
        if (list == null || list.size() < 2) return;

        switch (strategy) {
            case "AZ": // A'dan Z'ye (QuickSort kullanır)
                quickSortAZ(list, 0, list.size() - 1);
                break;
            case "ZA": // Z'den A'ya (MergeSort kullanır)
                mergeSort(list, "ZA");
                break;
            case "LENGTH": // Uzunluğa göre (MergeSort kullanır)
                mergeSort(list, "LENGTH");
                break;
            default:
                // Varsayılan olarak A-Z
                quickSortAZ(list, 0, list.size() - 1);
                break;
        }
    }

    // --- 1. QUICKSORT ALGORİTMASI (A-Z İçin İdeal) ---
    // Neden? Genelde yerinde (in-place) sıralama yaptığı için belleği az yorar.
    private static void quickSortAZ(List<String> list, int low, int high) {
        if (low < high) {
            int pi = partition(list, low, high);
            quickSortAZ(list, low, pi - 1);
            quickSortAZ(list, pi + 1, high);
        }
    }

    private static int partition(List<String> list, int low, int high) {
        String pivot = list.get(high);
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            // A-Z Karşılaştırması
            if (list.get(j).compareToIgnoreCase(pivot) < 0) {
                i++;
                swap(list, i, j);
            }
        }
        swap(list, i + 1, high);
        return i + 1;
    }

    private static void swap(List<String> list, int i, int j) {
        String temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    // --- 2. MERGESORT ALGORİTMASI (Stabil Sıralama İçin) ---
    // Neden? Uzunluk sıralamasında eşit uzunluktaki kelimelerin sırasını bozmaz (Stable).
    private static void mergeSort(List<String> list, String criteria) {
        if (list.size() < 2) {
            return;
        }
        int mid = list.size() / 2;
        
        // Listeyi ikiye böl
        List<String> left = new ArrayList<>(list.subList(0, mid));
        List<String> right = new ArrayList<>(list.subList(mid, list.size()));

        mergeSort(left, criteria);
        mergeSort(right, criteria);

        merge(list, left, right, criteria);
    }

    private static void merge(List<String> list, List<String> left, List<String> right, String criteria) {
        int i = 0, j = 0, k = 0;
        
        while (i < left.size() && j < right.size()) {
            boolean condition = false;

            if (criteria.equals("ZA")) {
                // Z-A: Sol > Sağ
                condition = left.get(i).compareToIgnoreCase(right.get(j)) > 0;
            } else if (criteria.equals("LENGTH")) {
                // Kısa olan öne gelir. Uzunluk eşitse alfabetik bakar.
                int lenLeft = left.get(i).length();
                int lenRight = right.get(j).length();
                
                if (lenLeft != lenRight) {
                    condition = lenLeft < lenRight;
                } else {
                    // Uzunluklar eşitse alfabetik (A-Z) olsun
                    condition = left.get(i).compareToIgnoreCase(right.get(j)) < 0;
                }
            }

            if (condition) {
                list.set(k++, left.get(i++));
            } else {
                list.set(k++, right.get(j++));
            }
        }
        
        // Kalanları ekle
        while (i < left.size()) {
            list.set(k++, left.get(i++));
        }
        while (j < right.size()) {
            list.set(k++, right.get(j++));
        }
    }
}