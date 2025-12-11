package org.project.model;

public class DLBNode {
    
    
    private char data;           // Bu düğümdeki harf
    private DLBNode sibling;     // Kardeş düğüm (Sağdaki pointer)
    private DLBNode child;       // Çocuk düğüm (Aşağıdaki pointer)
    private boolean isWordEnd;   // Kelime bitişi mi?
    
    //Constructor
    public DLBNode(char data) {
        this.data = data;
        this.sibling = null;
        this.child = null;
        this.isWordEnd = false;
    }
    
    // Boş constructor
    public DLBNode() {
        this.sibling = null;
        this.child = null;
        this.isWordEnd = false;
    }

    // 3. Getter ve Setter Metotları
    public char getData() {
        return data;
    }

    public void setData(char data) {
        this.data = data;
    }

    public DLBNode getSibling() {
        return sibling;
    }

    public void setSibling(DLBNode sibling) {
        this.sibling = sibling;
    }

    public DLBNode getChild() {
        return child;
    }

    public void setChild(DLBNode child) {
        this.child = child;
    }

    public boolean isWordEnd() {
        return isWordEnd;
    }

    public void setWordEnd(boolean wordEnd) {
        isWordEnd = wordEnd;
    }
}