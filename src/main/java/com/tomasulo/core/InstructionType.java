package com.tomasulo.core;


public enum InstructionType {

    // Floating point arithmetic
    ADD_D,
    SUB_D,
    MUL_D,
    DIV_D,
    ADD_S,
    SUB_S,
    MUL_S,
    DIV_S,

    // Loads
    LD,
    L_D,
    L_S,
    LW,

    // Stores
    SD,
    S_D,
    S_S,
    SW,

    // Integer arithmetic
    ADDI,
    SUBI,
    DADDI,
    DSUBI,

    // Others
    NOP
    , BEQ
    , BNE
}
