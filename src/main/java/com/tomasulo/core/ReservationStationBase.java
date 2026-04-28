package com.tomasulo.core;

/**
 * Base class for all Reservation Stations.
 * Subclasses:
 *   - RSAdd
 *   - RSMul
 *   - RSInt
 *
 * Fields are public for FXUtils dynamic reflection.
 */
public abstract class ReservationStationBase {

    public boolean busy = false;
    public String name;

    // cycle when this RS was issued (used for CDB arbitration)
    public int issueCycle = -1;
    // program index (instruction index) when issued — used for oldest-wins arbitration
    public int issueIndex = -1;

    public String op = null;

    public double Vj = 0;
    public double Vk = 0;

    public String Qj = null;
    public String Qk = null;

    public int A = 0;                 // Immediate for INT ops

    public boolean executing = false;
    public boolean resultReady = false;

    public double result = 0;
    public ExecutionUnit fu = new ExecutionUnit();
    // cycle when this reservation station started executing
    public int execStartCycle = -1;

    /** Return cycles left in the attached FU when executing, otherwise 0. */
    public int getTimer() {
        if (executing && fu != null) return fu.getCyclesLeft();
        return 0;
    }

    /** shared pipeline configuration */
    protected final PipelineConfig cfg;

    public ReservationStationBase(String name, PipelineConfig cfg) {
        this.name = name;
        this.cfg = cfg;
    }

    /** Subclasses return latency based on op type */
    public abstract int getLatency();

    /**
     * Ready when both operands available & not already computing.
     */
    public boolean readyToExecute() {
        if (!busy) return false;
        if (executing) return false;
        if (resultReady) return false;

        return (Qj == null && Qk == null);
    }

    /**
     * Reset all fields for reuse.
     */
    public void clear() {
        busy = false;
        op = null;

        Vj = 0;
        Vk = 0;
        Qj = null;
        Qk = null;

        A = 0;

        executing = false;
        resultReady = false;
        result = 0;

        fu.clear();
        issueCycle = -1;
        issueIndex = -1;
        execStartCycle = -1;
    }
}
