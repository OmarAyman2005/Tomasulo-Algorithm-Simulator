package com.tomasulo.core;

/**
 * Multi-cycle functional unit used by all ReservationStations.
 * Works with any op string (ADD.D, SUB.D, MUL.D, DIV.D, ADDI, etc).
 */
public class ExecutionUnit {

    private String op;
    private double a, b;

    private int cyclesLeft = 0;
    public boolean resultReady = false;
    public double result = 0;

    private String ownerTag = ""; // station name (A1, M2, I1...)

    public ExecutionUnit() { }

    /** Clears the FU state (used after WB or RS.clear()). */
    public void clear() {
        op = null;
        a = b = 0;
        cyclesLeft = 0;
        resultReady = false;
        result = 0;
        ownerTag = "";
    }

    /** Start execution for a new operation. */
    public void startExecution(String op, double a, double b, int latency, String tag) {
        this.op = op;
        this.a = a;
        this.b = b;
        this.cyclesLeft = latency;
        this.resultReady = false;
        this.ownerTag = tag;
    }

    /** Advance one cycle. */
    public void step() {
        if (cyclesLeft <= 0 || op == null) return;
        cyclesLeft--;
        if (cyclesLeft == 0) {
            computeResult();
            resultReady = true;
        }
    }

    /** Compute result depending on op string. */
    private void computeResult() {
        try {
            switch (op) {
                case "ADD.D": result = a + b; break;
                case "SUB.D": result = a - b; break;
                case "MUL.D": result = a * b; break;
                case "DIV.D": result = b == 0 ? 0 : a / b; break;
                case "ADDI": case "DADDI": result = a + b; break;
                case "SUBI": case "DSUBI": result = a - b; break;
                default: result = a; break;
            }
        } catch (Exception e) { result = 0; }
    }

    @Override
    public String toString() {
        return "FU(" + ownerTag + " op=" + op + " a=" + a + " b=" + b + " cycles=" + cyclesLeft + " ready=" + resultReady + ")";
    }

    /** Expose remaining cycles for UI timers. */
    public int getCyclesLeft() { return cyclesLeft; }

}
