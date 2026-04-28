package com.tomasulo.core;

public class StoreBuffer {

    public enum Stage {
        ADDR_READY,
        MEM,
        COMPLETED
    }

    public boolean busy = false;
    public String name;

    public int address = 0;
    public double V = 0.0;
    public String Q = null;

    // Remaining cycles while performing memory access (for Timer display)
    public int remainingCycles = 0;

    public Stage stage = Stage.ADDR_READY;

    private TomasuloCore core;

    // cycle when this buffer was issued
    public int issueCycle = -1;
    // program instruction index when this buffer was issued
    public int issueIndex = -1;
    // cycle when MEM stage should start (visual scheduling like loads)
    public int execStartCycle = -1;
    // recorded exec latency determined at MEM-start
    public int execLatency = 0;

    public StoreBuffer(String name) {
        this.name = name;
    }

    public StoreBuffer(String name, TomasuloCore core) {
        this.name = name;
        this.core = core;
    }

    public void clear() {
        busy = false;
        address = 0;
        V = 0.0;
        Q = null;
        stage = Stage.ADDR_READY;
        issueCycle = -1;
        issueIndex = -1;
    }
}
