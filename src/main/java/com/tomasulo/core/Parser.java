package com.tomasulo.core;

import java.util.Locale;

public class Parser {

    public static Instruction parse(String line, int index) {

        if (line == null) return null;

        String s = line.trim();
        if (s.isEmpty()) return null;
        if (s.startsWith("#")) return null;

        // Remove commas to simplify parsing
        s = s.replace(",", " ");

        // Normalize whitespace
        while (s.contains("  "))
            s = s.replace("  ", " ");

        String[] tok = s.split(" ");
        if (tok.length == 0) return null;

        String op = tok[0].toUpperCase(Locale.ROOT);

        // ============================================================
        //                     LOAD INSTRUCTIONS
        // ============================================================
        //                    BRANCH INSTRUCTIONS
        // ============================================================
        if (op.equals("BEQ") || op.equals("BNE")) {

            if (tok.length < 4) return null;

            String rs = tok[1].toUpperCase();
            String rt = tok[2].toUpperCase();
            String target = tok[3]; // could be numeric or label

            int imm = 0;
            try {
                imm = Integer.parseInt(target);
            } catch (Exception e) {
                // If not numeric, we'll allow loadProgram to replace label with index before parsing
                try {
                    imm = Integer.parseInt(target);
                } catch (Exception ignored) {
                    imm = 0;
                }
            }

            InstructionType type = op.equals("BEQ") ? InstructionType.BEQ : InstructionType.BNE;
            return new Instruction(type, index, op, null, rs, rt, imm);
        }

        if (op.equals("L.D") || op.equals("LD") || op.equals("L_S") || op.equals("LW")) {

            if (tok.length < 3) return null;

            String rd = tok[1].toUpperCase();  // destination FP register
            String addr = tok[2];

            int offset = parseOffset(addr);
            String base = parseBase(addr);

            return new Instruction(
                    InstructionType.L_D,
                    index, op, rd, base, null, offset
            );
        }

        // ============================================================
        //                     STORE INSTRUCTIONS
        // ============================================================
        if (op.equals("S.D") || op.equals("SD") || op.equals("S_S") || op.equals("SW")) {

            if (tok.length < 3) return null;

            String rt = tok[1].toUpperCase();  // value to store (RT)
            String addr = tok[2];

            int offset = parseOffset(addr);
            String base = parseBase(addr);

            return new Instruction(
                    InstructionType.S_D,
                    index, op, base, rt, null, offset
            );
        }

        // ============================================================
        //                    FP 3-REGISTER OPS
        // ============================================================
        if (op.equals("ADD.D") || op.equals("SUB.D")
            || op.equals("MUL.D") || op.equals("DIV.D")
            || op.equals("ADD.S") || op.equals("SUB.S")
            || op.equals("MUL.S") || op.equals("DIV.S")) {

            if (tok.length < 4) return null;

            String rd = tok[1].toUpperCase();
            String rs = tok[2].toUpperCase();
            String rt = tok[3].toUpperCase();

            InstructionType type = switch (op) {
                case "ADD.D" -> InstructionType.ADD_D;
                case "SUB.D" -> InstructionType.SUB_D;
                case "MUL.D" -> InstructionType.MUL_D;
                case "ADD.S" -> InstructionType.ADD_S;
                case "SUB.S" -> InstructionType.SUB_S;
                case "MUL.S" -> InstructionType.MUL_S;
                case "DIV.S" -> InstructionType.DIV_S;
                default -> InstructionType.DIV_D;
            };

            return new Instruction(type, index, op, rd, rs, rt, 0);
        }

        // ============================================================
        //                INTEGER IMMEDIATE OPERATIONS
        // ============================================================
        if (op.equals("ADDI") || op.equals("DADDI")
                || op.equals("SUBI") || op.equals("DSUBI")) {

            if (tok.length < 4) return null;

            String rd = tok[1].toUpperCase();
            String rs = tok[2].toUpperCase();
            int imm = Integer.parseInt(tok[3]);

            InstructionType type = switch (op) {
                case "ADDI" -> InstructionType.ADDI;
                case "SUBI" -> InstructionType.SUBI;
                case "DADDI" -> InstructionType.DADDI;
                default -> InstructionType.DSUBI;
            };

            return new Instruction(type, index, op, rd, rs, null, imm);
        }

        return null;
    }

    // ============================================================
    //               Helpers for OFFSET(BASE) syntax
    // ============================================================
    private static int parseOffset(String s) {
        try {
            int idx = s.indexOf("(");
            if (idx == -1) return 0;
            return Integer.parseInt(s.substring(0, idx));
        } catch (Exception e) {
            return 0;
        }
    }

    private static String parseBase(String s) {
        try {
            int l = s.indexOf("(");
            int r = s.indexOf(")");
            return s.substring(l + 1, r).toUpperCase();
        } catch (Exception e) {
            return "R0";
        }
    }
}
