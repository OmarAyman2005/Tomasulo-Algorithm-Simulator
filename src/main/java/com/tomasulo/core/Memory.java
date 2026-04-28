package com.tomasulo.core;

/**
 * Byte-addressable memory.
 * Internally represented as a byte[].
 * LOAD/STORE work on 8 bytes (double precision).
 */
public class Memory {

    private final byte[] mem;

    public Memory(int sizeBytes) {
        this.mem = new byte[sizeBytes];
    }

    // ============================
    // Helpers
    // ============================

    private void checkRange(int addr) {
        if (addr < 0 || addr + 7 >= mem.length) {
            throw new IllegalArgumentException("Memory access out of range: " + addr);
        }
    }

    // ============================
    // Load double (8 bytes)
    // ============================
    public double loadDouble(int addr) {
        checkRange(addr);

        long bits = 0;

        bits |= ((long) (mem[addr] & 0xFF))       << 56;
        bits |= ((long) (mem[addr + 1] & 0xFF))   << 48;
        bits |= ((long) (mem[addr + 2] & 0xFF))   << 40;
        bits |= ((long) (mem[addr + 3] & 0xFF))   << 32;
        bits |= ((long) (mem[addr + 4] & 0xFF))   << 24;
        bits |= ((long) (mem[addr + 5] & 0xFF))   << 16;
        bits |= ((long) (mem[addr + 6] & 0xFF))   << 8;
        bits |= ((long) (mem[addr + 7] & 0xFF));

        return Double.longBitsToDouble(bits);
    }

    // ============================
    // Store double (8 bytes)
    // ============================
    public void storeDouble(int addr, double value) {
        checkRange(addr);

        long bits = Double.doubleToLongBits(value);

        mem[addr]     = (byte) (bits >> 56);
        mem[addr + 1] = (byte) (bits >> 48);
        mem[addr + 2] = (byte) (bits >> 40);
        mem[addr + 3] = (byte) (bits >> 32);
        mem[addr + 4] = (byte) (bits >> 24);
        mem[addr + 5] = (byte) (bits >> 16);
        mem[addr + 6] = (byte) (bits >> 8);
        mem[addr + 7] = (byte) (bits);
    }

    // ============================
    // Optional debug helper
    // ============================
    public int size() {
        return mem.length;
    }
}
