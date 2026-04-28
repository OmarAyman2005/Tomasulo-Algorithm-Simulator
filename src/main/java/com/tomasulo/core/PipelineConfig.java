package com.tomasulo.core;

public class PipelineConfig {

    // ================== Station Counts ==================
    public int addStations;   // used by Controller + TomasuloCore
    public int mulStations;
    public int intStations;

    public int loadBuffers;
    public int storeBuffers;

    // ================== Latencies ==================
    public int latencyAdd;    // used by RSAdd
    public int latencySub;
    public int latencyMul;    // used by RSMul
    public int latencyDiv;
    public int latencyLoad;   // used by LoadBuffer
    public int latencyStore;
    public int latencyInt;    // used by RSInt

    // ================== Memory Size ==================
    public int memorySizeBytes = 4096; // you can adjust default

    // ================== EXTRA 1 (NEW FIELDS) ==================
    public int cacheHitLatency;
    public int cacheMissPenalty;
    // Cache sizing (bytes) and block size (bytes)
    public int cacheSizeBytes = 1024; // default 1KB
    public int cacheBlockSize = 16;   // default 16 bytes per block

    public PipelineConfig() {}

    // ================== copy() METHOD ==================
    public PipelineConfig copy() {
        PipelineConfig c = new PipelineConfig();

        c.addStations = this.addStations;
        c.mulStations = this.mulStations;
        c.intStations = this.intStations;

        c.loadBuffers = this.loadBuffers;
        c.storeBuffers = this.storeBuffers;

        c.latencyAdd = this.latencyAdd;
        c.latencySub = this.latencySub;
        c.latencyMul = this.latencyMul;
        c.latencyDiv = this.latencyDiv;
        c.latencyLoad = this.latencyLoad;
        c.latencyStore = this.latencyStore;
        c.latencyInt = this.latencyInt;

        c.memorySizeBytes = this.memorySizeBytes;

        // NEW FIELDS
        c.cacheHitLatency = this.cacheHitLatency;
        c.cacheMissPenalty = this.cacheMissPenalty;
        c.cacheSizeBytes = this.cacheSizeBytes;
        c.cacheBlockSize = this.cacheBlockSize;

        return c;
    }
}
