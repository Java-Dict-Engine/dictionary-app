package org.project.model; 
public class DLBNode {
    
    
    private char data;           // Harf
    private DLBNode sibling;     // Kardeş
    private DLBNode child;       // Çocuk
    private boolean isWordEnd;   // Kelime bitişi mi?
    
    
    private String definition;   // Kelimenin anlamı
    private String type;         // Türü (İsim, Fiil, Sıfat vb.)

    
    public DLBNode(char data) {
        this.data = data;
        this.sibling = null;
        this.child = null;
        this.isWordEnd = false;
        this.definition = null; 
        this.type = null;
    }
    
    
    public DLBNode() {
        this( (char) 0 ); 
    }


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
    

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}