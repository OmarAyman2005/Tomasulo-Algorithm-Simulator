package com.tomasulo.core;

public class ReservationStation {
    public boolean busy = false;
    public String name;      // e.g. "A1", "M1"
    public String op;        // ADD.D, MUL.D, etc.
    public double Vj, Vk;    // operand values
    public String Qj, Qk;    // tags of producers
    public int A;            // address / immediate (if needed)
    public int remainingCycles = 0;
    public boolean executing = false; // is currently in EX
    public double result = 0.0;       // result to put on CDB when done

    public ReservationStation(String name) {
        this.name = name;
    }

    public void clear() {
        busy = false;
        op = null;
        Vj = Vk = 0.0;
        Qj = Qk = null;
        A = 0;
        remainingCycles = 0;
        executing = false;
        result = 0.0;
    }
}
