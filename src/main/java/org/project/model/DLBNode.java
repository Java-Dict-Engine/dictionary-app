package org.project.model;

public class DLBNode {

    private char data;
    private DLBNode child;
    private DLBNode sibling;
    private boolean isWordEnd;   // Partnerin isWordEnd kullanmış
    private String definition;   // Partnerin definition kullanmış
    private String type;

    public DLBNode() {
        this('\0');
    }

    public DLBNode(char data) {
        this.data = data;
    }

    // Getter ve Setterlar
    public char getData() { return data; }
    public void setData(char data) { this.data = data; }

    public DLBNode getChild() { return child; }
    public void setChild(DLBNode child) { this.child = child; }

    public DLBNode getSibling() { return sibling; }
    public void setSibling(DLBNode sibling) { this.sibling = sibling; }

    public boolean isWordEnd() { return isWordEnd; }
    public void setWordEnd(boolean wordEnd) { isWordEnd = wordEnd; }

    public String getDefinition() { return definition; }
    public void setDefinition(String definition) { this.definition = definition; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}