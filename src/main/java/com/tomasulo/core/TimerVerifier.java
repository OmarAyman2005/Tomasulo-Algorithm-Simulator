package com.tomasulo.core;

import java.util.List;

public class TimerVerifier {

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

        System.out.println("=== TimerVerifier: cycles 0..12 ===\n");

        // header
        System.out.printf("%5s | %6s | %6s | %6s | %6s | %6s | %s\n", "Cycle", "L1(T)", "M1(T)", "A1(T)", "M2(T)", "A2(T)", "Notes");

        for (int step = 0; step <= 12; step++) {
            if (step > 0) core.step();
            int cycle = core.getCycle();

            String note = core.getLastCacheEvent() == null ? "" : core.getLastCacheEvent().split("\n")[0];
            int tL1 = findTimerForIssue(core.getLoadBuffers(), 0, cycle);
            int tM1 = findTimerForIssue(core.getMulRS(), 1, cycle);
            int tA1 = findTimerForIssue(core.getAddRS(), 2, cycle);
            int tM2 = findTimerForIssue(core.getMulRS(), 3, cycle);
            int tA2 = findTimerForIssue(core.getAddRS(), 4, cycle);

            System.out.printf("%5d | %6d | %6d | %6d | %6d | %6d | %s\n", cycle, tL1, tM1, tA1, tM2, tA2, note);
        }

        System.out.println("\nDone.");
    }

    private static <T> int findTimerForIssue(List<T> list, int issueIndex, int cycle) {
        for (T o : list) {
            if (o instanceof LoadBuffer) {
                LoadBuffer lb = (LoadBuffer)o;
                if (lb.issueIndex == issueIndex) return computeTimer(lb, cycle);
            } else if (o instanceof ReservationStationBase) {
                ReservationStationBase r = (ReservationStationBase)o;
                if (r.issueIndex == issueIndex) return computeTimer(r, cycle);
            } else if (o instanceof StoreBuffer) {
                StoreBuffer sb = (StoreBuffer)o;
                if (sb.issueIndex == issueIndex) return computeTimer(sb, cycle);
            }
        }
        return 0;
    }

    private static int computeTimer(ReservationStationBase r, int cycle) {
        if (r.execStartCycle >= 0) {
            int latency = Math.max(1, r.getLatency());
            int endCycle = r.execStartCycle + latency - 1;
            int remaining = endCycle - cycle;
            return Math.max(0, remaining);
        } else if (r.executing) {
            return r.getTimer();
        }
        return 0;
    }

    private static int computeTimer(LoadBuffer lb, int cycle) {
        if (lb.execStartCycle >= 0 && lb.execLatency > 0) {
            int latency = Math.max(1, lb.execLatency);
            int endCycle = lb.execStartCycle + latency - 1;
            int remaining = endCycle - cycle;
            return Math.max(0, remaining);
        }
        return Math.max(0, lb.remainingCycles);
    }

    private static int computeTimer(StoreBuffer sb, int cycle) {
        if (sb.execStartCycle >= 0 && sb.execLatency > 0) {
            int latency = Math.max(1, sb.execLatency);
            int endCycle = sb.execStartCycle + latency - 1;
            int remaining = endCycle - cycle;
            return Math.max(0, remaining);
        }
        return Math.max(0, sb.remainingCycles);
    }
}
