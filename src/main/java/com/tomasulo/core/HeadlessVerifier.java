package com.tomasulo.core;

import java.util.Arrays;
import java.util.List;

public class HeadlessVerifier {

    public static void main(String[] args) {
        PipelineConfig cfg = new PipelineConfig();
        // basic station counts
        cfg.addStations = 3;
        cfg.mulStations = 2;
        cfg.intStations = 2;
        cfg.loadBuffers = 3;
        cfg.storeBuffers = 2;
        // latencies
        cfg.latencyAdd = 2; cfg.latencySub = 2; cfg.latencyMul = 10; cfg.latencyDiv = 20; cfg.latencyLoad = 1; cfg.latencyStore = 1; cfg.latencyInt = 1;
        // memory/cache
        cfg.memorySizeBytes = 256;
        cfg.cacheHitLatency = 1;
        cfg.cacheMissPenalty = 6;
        cfg.cacheSizeBytes = 64;
        cfg.cacheBlockSize = 16;

        TomasuloCore core = new TomasuloCore(cfg, cfg.memorySizeBytes);
        core.initStructures();

        List<String> program = Arrays.asList(
                "L.D F0, 0(R1)",
                "L.D F1, 8(R1)",
                "ADD.D F2, F0, F1",
                "S.D F2, 16(R1)",
                "L.D F3, 0(R1)"
        );

        core.loadProgram(program);

        System.out.println("Starting headless verification run...");

        int maxCycles = 50;
        for (int i = 0; i < maxCycles && !core.isFinished(); i++) {
            core.step();
            TomasuloCore.CoreState s = core.exportState();
            System.out.println("--- Cycle " + s.cycle + " ---");
            String ev = core.getLastCacheEvent();
            if (ev != null) System.out.println("Cache Event:\n" + ev);
            else System.out.println("Cache Event: <none>");

            System.out.println("Transient Current EA: " + s.currentEA + " startCycle=" + s.currentInputStartCycle);

            System.out.println("Cache lines:");
            for (int bi = 0; bi < core.getCache().getNumBlocks(); bi++) {
                Cache.Line L = core.getCache().getLine(bi);
                System.out.printf("  block %d: valid=%b tag=%d lastEA=%s\n", bi, L.valid, L.tag, (L.lastLoadedEA>=0?L.lastLoadedEA:"-"));
            }
            System.out.println();
        }

        System.out.println("Run finished at cycle " + core.getCycle());
    }
}
