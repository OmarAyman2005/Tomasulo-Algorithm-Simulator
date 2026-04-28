package com.tomasulo.core;

public class RSAdd extends ReservationStationBase {

    public RSAdd(String name, PipelineConfig cfg) {
        super(name, cfg);
    }

    @Override
    public int getLatency() {
        if (op == null) return cfg.latencyAdd;

        switch (op) {
            case "ADD.D": return cfg.latencyAdd;
            case "SUB.D": return cfg.latencySub;
            default:      return cfg.latencyAdd;
        }
    }
}
