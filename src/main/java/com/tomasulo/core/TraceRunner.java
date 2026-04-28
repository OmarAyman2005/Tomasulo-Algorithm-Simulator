package com.tomasulo.core;

import java.util.List;

public class TraceRunner {

    public static void main(String[] args) {
        PipelineConfig cfg = new PipelineConfig();
        cfg.addStations = 3;
        cfg.mulStations = 2;
        cfg.intStations = 0;
        cfg.loadBuffers = 3;
        cfg.storeBuffers = 0;
        cfg.latencyAdd = 2; // FP add/sub
        cfg.latencySub = 2;
        cfg.latencyMul = 3;
        cfg.latencyDiv = 4;
        cfg.cacheHitLatency = 1;
        cfg.cacheMissPenalty = 2;
        cfg.cacheSizeBytes = 32;
        cfg.cacheBlockSize = 8;

        TomasuloCore core = new TomasuloCore(cfg, 256);
        core.initStructures();


        // preload FP regs F0..F10 = 0..10
        for (int i = 0; i <= 10; i++) core.getFpRF().setValue(i, (double)i);
        // preload integer regs R0..R3
        for (int i = 0; i <= 3; i++) core.getIntRF().setValue(i, i);

        List<String> prog = List.of(
                "L.D F6,32(R2)",
                "MUL.D F0,F2,F4",
                "SUB.D F8,F2,F6",
                "DIV.D F10,F10,F6",
                "ADD.D F6,F8,F2"
        );
        core.loadProgram(prog);

        System.out.println("=== Initial State (Cycle 0) ===\n");
        printState(core);

        // run cycles until 12
        for (int step = 1; step <= 12; step++) {
            core.step();
            System.out.println("\n=== Cycle " + core.getCycle() + " ===");
            String ev = core.getLastCacheEvent();
            if (ev != null) System.out.println(ev);
            printState(core);
        }

        System.out.println("\nSimulation finished at cycle " + core.getCycle());
    }

    private static void printState(TomasuloCore core) {
        System.out.println("-- Queue --");
        List<QueueEntry> q = core.getProgramView();
        for (QueueEntry e : q) {
            System.out.printf("%2d: %-30s | Issue=%s Execute=%s Write=%s\n",
                e.index, e.getInstructionText(), e.getIssueString(), e.getExecuteString(), e.getWriteString());
        }

        System.out.println("-- ADD RS --");
        for (RSAdd r : core.getAddRS()) {
            System.out.printf("%s busy=%d op=%s Vj=%s Vk=%s Qj=%s Qk=%s Timer=%d\n",
                    r.name, r.busy?1:0, r.op, r.Vj, r.Vk, r.Qj, r.Qk, r.getTimer());
        }

        System.out.println("-- MUL RS --");
        for (RSMul r : core.getMulRS()) {
            System.out.printf("%s busy=%d op=%s Vj=%s Vk=%s Qj=%s Qk=%s Timer=%d\n",
                    r.name, r.busy?1:0, r.op, r.Vj, r.Vk, r.Qj, r.Qk, r.getTimer());
        }

        System.out.println("-- Load Buffers --");
        for (LoadBuffer lb : core.getLoadBuffers()) {
            System.out.printf("%s busy=%d address=%d Qi=%s rem=%d stage=%s\n",
                    lb.name, lb.busy?1:0, lb.address, lb.Qi, lb.remainingCycles, lb.stage);
        }

        System.out.println("-- Registers (FP) --");
        RegisterFileFloat frf = core.getFpRF();
        for (int i = 0; i <= 10; i++) {
            System.out.printf("F%-2d = %6.1f Qi=%s\n", i, frf.getValue(i), frf.getQi(i));
        }
    }
}
