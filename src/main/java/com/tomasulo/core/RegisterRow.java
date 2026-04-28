package com.tomasulo.core;



import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RegisterRow {

    private final StringProperty name;
    private final StringProperty value;
    private final StringProperty qi;

    public RegisterRow(String name, double val, String qiTag) {
        this.name = new SimpleStringProperty(name);
        this.value = new SimpleStringProperty(String.valueOf(val));
        this.qi = new SimpleStringProperty(qiTag == null ? "" : qiTag);
    }

    // Alternate constructor allowing direct string value (used for integer RF display)
    public RegisterRow(String name, String valueStr, String qiTag) {
        this.name = new SimpleStringProperty(name);
        this.value = new SimpleStringProperty(valueStr == null ? "" : valueStr);
        this.qi = new SimpleStringProperty(qiTag == null ? "" : qiTag);
    }

    // -----------------------------
    // Property getters (required!)
    // -----------------------------

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty valueProperty() {
        return value;
    }

    public StringProperty qiProperty() {
        return qi;
    }

    // -----------------------------
    // Optional direct getters
    // -----------------------------
    public String getName() {
        return name.get();
    }

    public String getValue() {
        return value.get();
    }

    public String getQi() {
        return qi.get();
    }

    // -----------------------------
    // Optional setters
    // -----------------------------
    public void setValue(double v) {
        this.value.set(String.valueOf(v));
    }

    // Set integer value (store textual integer without decimal part)
    public void setValueInt(int v) {
        this.value.set(String.valueOf(v));
    }

    // Set raw string value (used when committing edits without immediate parsing)
    public void setValueString(String s) {
        this.value.set(s == null ? "" : s);
    }

    public void setQi(String tag) {
        this.qi.set(tag == null ? "" : tag);
    }
}
