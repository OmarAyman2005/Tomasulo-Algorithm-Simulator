package com.tomasulo.core;

public class RegisterFileInt {

    private final double[] values = new double[32];   // R0..R31
    private final String[] Qi = new String[32];       // tag waiting

    public RegisterFileInt() { reset(); }

    public void reset() {
        for (int i = 0; i < 32; i++) {
            values[i] = 0.0;
            Qi[i] = null;
        }
    }

    public double getValue(int index) {
        if (index < 0 || index >= 32) return 0.0;
        return values[index];
    }

    public void setValue(int index, double val) {
        if (index < 0 || index >= 32) return;
        values[index] = val;
    }

    public String getQi(int index) {
        if (index < 0 || index >= 32) return null;
        return Qi[index];
    }

    public void setQi(int index, String tag) {
        if (index < 0 || index >= 32) return;
        Qi[index] = tag;
    }
}
