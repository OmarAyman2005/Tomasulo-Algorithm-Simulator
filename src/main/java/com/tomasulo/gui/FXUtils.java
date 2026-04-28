package com.tomasulo.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.lang.reflect.Field;

public class FXUtils {

    /**
     * Returns a StringProperty for a given object's field name.
     * Used to populate dynamic TableView columns for ReservationStations,
     * LoadBuffers, StoreBuffers, etc.
     */
    public static StringProperty stringPropertyOf(Object obj, String fieldName) {
        try {
            Field f = obj.getClass().getField(fieldName);
            f.setAccessible(true);

            Object value = f.get(obj);

            if (value == null)
                return new SimpleStringProperty("");

            // For boolean fields (e.g., busy) display 1/0 instead of true/false
            if (value instanceof Boolean) {
                return new SimpleStringProperty(((Boolean) value) ? "1" : "0");
            }

            return new SimpleStringProperty(String.valueOf(value));

        } catch (Exception e) {
            // Try superclass (for RS subclasses)
            try {
                Field f = obj.getClass().getSuperclass().getField(fieldName);
                f.setAccessible(true);

                Object value = f.get(obj);

                if (value == null)
                    return new SimpleStringProperty("");

                return new SimpleStringProperty(String.valueOf(value));

            } catch (Exception ignored) {
                return new SimpleStringProperty("");
            }
        }
    }
}
