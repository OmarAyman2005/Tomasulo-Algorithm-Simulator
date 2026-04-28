package com.tomasulo.core;

import java.util.Arrays;

/**
 * A fully byte-addressed memory model.
 *
 * Memory is an array of bytes (8-bit values).
 * Loads/stores of double (8 bytes) are handled manually.
 *
 * Endianness: Little-endian (least significant byte first)
 */
public class MemoryByteAddressed {

    private final byte[] mem;

    public MemoryByteAddressed(int numBytes) {
        mem = new byte[numBytes];
        Arrays.fill(mem, (byte)0);
    }

    /** Load 8 bytes as a little-endian double */
    public double loadDouble(int address) {
        if (address < 0 || address + 7 >= mem.length) return 0.0;

        long bits = 0;
        for (int i = 0; i < 8; i++) {
            bits |= ((long)(mem[address + i] & 0xFF)) << (i * 8);
        }
        return Double.longBitsToDouble(bits);
    }

    /** Store double as 8 bytes little-endian */
    public void storeDouble(int address, double value) {
        if (address < 0 || address + 7 >= mem.length) return;

        long bits = Double.doubleToLongBits(value);
        for (int i = 0; i < 8; i++) {
            mem[address + i] = (byte)((bits >> (i * 8)) & 0xFF);
        }
    }

    /** Read a single byte */
    public byte loadByte(int address) {
        if (address < 0 || address >= mem.length) return 0;
        return mem[address];
    }

    /** Write a single byte */
    public void storeByte(int address, byte value) {
        if (address < 0 || address >= mem.length) return;
        mem[address] = value;
    }

    public int size() {
        return mem.length;
    }
}
