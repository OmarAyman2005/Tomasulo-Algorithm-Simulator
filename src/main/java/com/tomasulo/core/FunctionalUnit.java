package com.tomasulo.core;

/**
 * Represents a functional unit that executes an instruction with a fixed latency.
 * Each reservation station has its own functional unit instance (parallel execution).
 */
public class FunctionalUnit {

    public boolean busy = false;
    public int remainingCycles = 0;

    public double opA = 0.0;
    public double opB = 0.0;

    public String opName = null;     // ADD.D, MUL.D, etc.
    public String producingTag = null; // RS name producing this result

    public double result = 0.0;
    public boolean resultReady = false;

    public void startExecution(String op, double vj, double vk, int latency, String tag) {
        this.busy = true;
        this.opName = op;
        this.opA = vj;
        this.opB = vk;
        this.remainingCycles = latency;
        this.producingTag = tag;
        this.resultReady = false;
    }

    public void step() {
        if (!busy) return;
        if (remainingCycles > 0) {
            remainingCycles--;
            if (remainingCycles == 0) {
                // compute final result
                switch (opName) {
                    case "ADD.D": result = opA + opB; break;
                    case "SUB.D": result = opA - opB; break;
                    case "MUL.D": result = opA * opB; break;
                    case "DIV.D": result = (opB == 0 ? 0.0 : opA / opB); break;

                    case "ADD.S": result = opA + opB; break;
                    case "SUB.S": result = opA - opB; break;
                    case "MUL.S": result = opA * opB; break;
                    case "DIV.S": result = (opB == 0 ? 0.0 : opA / opB); break;

                    case "ADDI":
                    case "DADDI":
                        result = opA + opB;
                        break;

                    case "SUBI":
                    case "DSUBI":
                        result = opA - opB;
                        break;

                    case "BEQ":
                        result = (opA == opB) ? 1.0 : 0.0;
                        break;
                    case "BNE":
                        result = (opA != opB) ? 1.0 : 0.0;
                        break;

                    default:
                        result = 0.0;
                        break;
                }

                resultReady = true;
                busy = false;
            }
        }
    }

    public void clear() {
        busy = false;
        resultReady = false;
        remainingCycles = 0;
        opName = null;
        producingTag = null;
    }
}
