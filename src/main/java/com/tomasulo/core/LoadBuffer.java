package com.tomasulo.core;

public class LoadBuffer {

    public enum Stage {
        ADDR_READY,
        MEM,
        DONE
    }

    public boolean busy = false;
    public String name;

    public int address = 0;
    public String Qi = null;
    public int numBytes = 8; // default to 8 (double)

    public int remainingCycles = 0;
    public double result = 0.0;

    public Stage stage = null;

    private TomasuloCore core;

    // cycle when this buffer was issued
    public int issueCycle = -1;
    // program instruction index when this buffer was issued
    public int issueIndex = -1;
    // cycle when this buffer will start its MEM execution (if set by core)
    public int execStartCycle = -1;
    // original latency assigned when MEM execution started
    public int execLatency = 0;

    // Original constructor (KEEP IT)
    public LoadBuffer(String name) {
        this.name = name;
    }

    // NEW constructor required by TomasuloCore
    public LoadBuffer(String name, TomasuloCore core) {
        this.name = name;
        this.core = core;
    }

    public void clear() {
        busy = false;
        address = 0;
        Qi = null;
        remainingCycles = 0;
        result = 0.0;
        stage = null;
        issueCycle = -1;
        issueIndex = -1;
        execStartCycle = -1;
        execLatency = 0;
    }
}
