package com.tomasulo.gui;

import javafx.beans.property.SimpleStringProperty;

public class CacheAddressRow {
    private final SimpleStringProperty ea;
    private final SimpleStringProperty tag;
    private final SimpleStringProperty index;
    private final SimpleStringProperty offset;

    public CacheAddressRow(String ea, String tag, String index, String offset) {
        this.ea = new SimpleStringProperty(ea == null ? "" : ea);
        this.tag = new SimpleStringProperty(tag == null ? "" : tag);
        this.index = new SimpleStringProperty(index == null ? "" : index);
        this.offset = new SimpleStringProperty(offset == null ? "" : offset);
    }

    public String getEa() { return ea.get(); }
    public String getTag() { return tag.get(); }
    public String getIndex() { return index.get(); }
    public String getOffset() { return offset.get(); }

    // setters (not required but handy)
    public void setEa(String v) { ea.set(v == null ? "" : v); }
    public void setTag(String v) { tag.set(v == null ? "" : v); }
    public void setIndex(String v) { index.set(v == null ? "" : v); }
    public void setOffset(String v) { offset.set(v == null ? "" : v); }
}
