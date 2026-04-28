package com.tomasulo.core;

public class Instruction {

    private final InstructionType type;
    private final int index;

    private final String op;    // "ADD.D", "L.D", etc.

    private final String rd;    // Destination (loads, FP ops, integer ops)
    private final String rs;    // Source 1
    private final String rt;    // Source 2 (FP ops, store value)

    private final int offset;   // For load/store and integer imm ops


    /**
     * Full unified instruction constructor.
     *
     * @param type   InstructionType (ADD_D, LD, S_D, ADDI...)
     * @param index  Program index
     * @param op     Operation string ("ADD.D", "L.D", etc.)
     * @param rd     Destination register (or base for store)
     * @param rs     Source register 1
     * @param rt     Source register 2 (or store value)
     * @param offset Immediate or load/store offset
     */
    public Instruction(
            InstructionType type,
            int index,
            String op,
            String rd,
            String rs,
            String rt,
            int offset
    ) {
        this.type = type;
        this.index = index;

        this.op = op;

        this.rd = rd;
        this.rs = rs;
        this.rt = rt;

        this.offset = offset;
    }

    // ===== GETTERS =====

    public InstructionType getType() { return type; }
    public int getIndex() { return index; }

    public String getOpString() { return op; }

    public String getRd() { return rd; }
    public String getRs() { return rs; }
    public String getRt() { return rt; }

    public int getOffset() { return offset; }

    @Override
    public String toString() {
        return "Instruction{" +
                "type=" + type +
                ", op='" + op + '\'' +
                ", rd='" + rd + '\'' +
                ", rs='" + rs + '\'' +
                ", rt='" + rt + '\'' +
                ", offset=" + offset +
                '}';
    }
}
