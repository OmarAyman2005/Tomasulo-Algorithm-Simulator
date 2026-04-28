package com.tomasulo.core;

public class RSMul extends ReservationStationBase {

    public RSMul(String name, PipelineConfig cfg) {
        super(name, cfg);
    }

    @Override
    public int getLatency() {
        if (op == null) return cfg.latencyMul;

        switch (op) {
            case "MUL.D": return cfg.latencyMul;
            case "DIV.D": return cfg.latencyDiv;
            default:      return cfg.latencyMul;
        }
    }
}
