package com.tomasulo.core;

public class RSInt extends ReservationStationBase {

    public RSInt(String name, PipelineConfig cfg) {
        super(name, cfg);
    }

    @Override
    public int getLatency() {
        return cfg.latencyInt;
    }
}
