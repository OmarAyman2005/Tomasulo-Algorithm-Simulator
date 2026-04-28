package com.tomasulo.core;

/**
 * Simple byte-addressed direct-mapped cache model.
 * This implementation provides a minimal API used by the rest of the
 * project while keeping behavior predictable for simulation and testing.
 */
public class Cache {

    public static class Line {
        public boolean valid;
        public int tag;
        public byte[] data;
        // store the effective address (8-bit) that last populated this line's data
        // so the GUI can display the block as the user requested (starting at EA).
        public int lastLoadedEA = -1;

        public Line(int blockSize) {
            this.valid = false;
            this.tag = 0;
            this.data = new byte[blockSize];
        }
    }

    private final int blockSize;
    private final Line[] lines;
    private final MemoryByteAddressed memory;
    // Transient UI state: current input address table (visible only on the access start cycle)
    private Integer currentEA = null; // decimal EA
    private String currentTagBits = null;
    private String currentIndexBits = null;
    private String currentOffsetBits = null;
    // record the cycle when this transient was started; visible only when restored for that exact cycle
    private int currentInputStartCycle = -1;

    public Cache(PipelineConfig cfg, MemoryByteAddressed memory) {
        this.blockSize = Math.max(1, cfg.cacheBlockSize);
        int numBlocks = Math.max(1, cfg.cacheSizeBytes / this.blockSize);
        this.lines = new Line[numBlocks];
        for (int i = 0; i < numBlocks; i++) {
            lines[i] = new Line(this.blockSize);
        }
        this.memory = memory;
    }

    /** Result returned when processing a load through the cache. */
    public static class ProcessResult {
        public final boolean hit;
        public final boolean replacement; // true if replaced an existing valid line
        public final int setIndex;
        public final int tag;
        public final int latency;
        public final byte[] block; // snapshot of block bytes after any update
        public final String breakdown; // human text breakdown to show in UI/Event log

        public ProcessResult(boolean hit, boolean replacement, int setIndex, int tag, int latency, byte[] block, String breakdown) {
            this.hit = hit; this.replacement = replacement; this.setIndex = setIndex; this.tag = tag; this.latency = latency; this.block = block; this.breakdown = breakdown;
        }
    }

    public int getBlockSize() {
        return blockSize;
    }

    public int getNumBlocks() {
        return lines.length;
    }

    public Line getLine(int index) {
        return lines[index];
    }

    /** Read a single byte from the cache (simple direct-mapped behavior).
     * On miss it fetches the whole block from memory into the cache line.
     */
    public byte loadByte(int address) {
        if (memory == null) return 0;
        // normalize to 8-bit effective address space to match processLoad behavior
        int ea = address & 0xFF;

        int blockIndex = (ea / blockSize) % lines.length;
        int tag = ea / (blockSize * lines.length);
        Line line = lines[blockIndex];
        if (!line.valid || line.tag != tag) {
            // Miss: bring block from memory using 8-bit wrap semantics
            int blockStart = (ea / blockSize) * blockSize;
            for (int i = 0; i < blockSize; i++) {
                int addr = (blockStart + i) & 0xFF; // wrap to 8-bit
                if (addr >= 0 && addr < memory.size()) {
                    line.data[i] = memory.loadByte(addr);
                } else line.data[i] = 0;
            }
            line.valid = true;
            line.tag = tag;
            // record effective addr that caused the fill (use lower 8 bits)
            line.lastLoadedEA = ea & 0xFF;
        }
        int offset = ea % blockSize;
        return line.data[offset];
    }

    /**
     * Process a load that is starting execution at effective address `address`.
     * This performs address decoding (8-bit EA), checks for hit/compulsory/replacement miss,
     * updates cache lines on misses, fetches the block from `memory` when needed and
     * returns a `ProcessResult` describing the outcome and the latency to use.
     *
     * Latency policy: hit -> hitLatency; miss -> hitLatency + missPenalty
     */
    public ProcessResult processLoad(int address, int hitLatency, int missPenalty) {
        int ea = address & 0xFF; // 8-bit EA

        // compute offset/index/tag bit widths
        int offsetBits = 0; int b = blockSize; while ((1 << offsetBits) < b) offsetBits++;
        int numBlocks = lines.length; int indexBits = 0; while ((1 << indexBits) < numBlocks) indexBits++;
        int tagBits = 8 - indexBits - offsetBits;

        int offset = ea % blockSize;
        int index = (ea / blockSize) % numBlocks;
        int tag = ea / (blockSize * numBlocks);

        Line L = lines[index];
        boolean wasValid = L.valid;
        boolean hit = (wasValid && L.tag == tag);
        boolean replacement = wasValid && L.tag != tag;

        if (!hit) {
            // fetch block from memory into cache line
            int blockStart = (ea / blockSize) * blockSize;
            for (int i = 0; i < blockSize; i++) {
                int addr = (blockStart + i) & 0xFF; // wrap to 8-bit space
                if (addr >= 0 && addr < memory.size()) {
                    L.data[i] = memory.loadByte(addr);
                } else L.data[i] = 0;
            }
            L.valid = true;
            L.tag = tag;
            // record the effective address (ea) that caused this load so the GUI
            // will list the block starting at the EA and the following bytes.
            L.lastLoadedEA = ea & 0xFF;
        }

        int latency = hit ? hitLatency : (hitLatency + missPenalty);

        // Prepare a snapshot of the block for the UI (decimal values)
        byte[] blockSnap = new byte[blockSize];
        System.arraycopy(L.data, 0, blockSnap, 0, blockSize);

        // Build breakdown string matching the required format
        String bin = String.format("%8s", Integer.toBinaryString(ea)).replace(' ', '0');
        String tagBin = tagBits > 0 ? String.format("%" + tagBits + "s", Integer.toBinaryString(tag)).replace(' ', '0') : "";
        String idxBin = indexBits > 0 ? String.format("%" + indexBits + "s", Integer.toBinaryString(index)).replace(' ', '0') : "";
        String offBin = offsetBits > 0 ? String.format("%" + offsetBits + "s", Integer.toBinaryString(offset)).replace(' ', '0') : "";

        StringBuilder bd = new StringBuilder();
        // include a raw EA= line so the event-log builder treats the following breakdown as raw
        bd.append("EA=").append(ea).append(" --> BIN=").append(bin).append("\n");
        bd.append(ea).append("  ->  ").append(bin).append("\n\n");
        bd.append("Tag | Index | Offset\n");
        // align spacing similar to examples: three groups separated by (3 spaces)
        bd.append(tagBin).append("   ").append(idxBin).append("      ").append(offBin).append("\n");
        // Do not set transient UI state here; caller (core/controller) will set visibility for this cycle
        return new ProcessResult(hit, replacement, index, tag, latency, blockSnap, bd.toString());
    }

    /** Decode an 8-bit EA into padded tag/index/offset binary strings. */
    public String[] decodeAddressBits(int address) {
        int ea = address & 0xFF;
        int offsetBits = 0; int b = blockSize; while ((1 << offsetBits) < b) offsetBits++;
        int numBlocks = lines.length; int indexBits = 0; while ((1 << indexBits) < numBlocks) indexBits++;
        int tagBits = 8 - indexBits - offsetBits;
        int offset = ea % blockSize;
        int index = (ea / blockSize) % numBlocks;
        int tag = ea / (blockSize * numBlocks);
        String tagBin = tagBits > 0 ? String.format("%" + tagBits + "s", Integer.toBinaryString(tag)).replace(' ', '0') : "";
        String idxBin = indexBits > 0 ? String.format("%" + indexBits + "s", Integer.toBinaryString(index)).replace(' ', '0') : "";
        String offBin = offsetBits > 0 ? String.format("%" + offsetBits + "s", Integer.toBinaryString(offset)).replace(' ', '0') : "";
        return new String[] { tagBin, idxBin, offBin };
    }

    /** Caller indicates that an access started and the Current Input Address table should be shown for this cycle only. */
    public void setCurrentInput(int address, String tagBits, String indexBits, String offsetBits, int startCycle) {
        this.currentEA = address & 0xFF;
        this.currentTagBits = tagBits;
        this.currentIndexBits = indexBits;
        this.currentOffsetBits = offsetBits;
        // Record the start cycle; UI should show this transient only when the view is at this cycle.
        this.currentInputStartCycle = startCycle;
    }

    /** Clear the transient current input table immediately. */
    public void clearCurrentInput() {
        this.currentEA = null;
        this.currentTagBits = null;
        this.currentIndexBits = null;
        this.currentOffsetBits = null;
        this.currentInputStartCycle = -1;
    }

    /** Called by the core at cycle start to expire the transient current input when needed. */
    public void onCycleStart(int cycle) {
        // expire the transient if the UI has advanced past the start cycle
        if (this.currentInputStartCycle >= 0 && cycle > this.currentInputStartCycle) {
            clearCurrentInput();
        }
    }

    /** Return a formatted 2x4 table string for the Current Input Address table, or empty string if none. */
    public String getCurrentInputTableString() {
        if (currentEA == null) return "";
        // Row1: (empty) | Tag | Index | Offset
        // Row2: EA decimal | tagBits | indexBits | offsetBits
        StringBuilder sb = new StringBuilder();
        sb.append("(Current Input Address Table)\n");
        sb.append(String.format("%-8s| %-4s| %-5s| %-6s\n", "", "Tag", "Index", "Offset"));
        sb.append(String.format("%-8s| %-4s| %-5s| %-6s\n", String.valueOf(currentEA), currentTagBits == null ? "" : currentTagBits, currentIndexBits == null ? "" : currentIndexBits, currentOffsetBits == null ? "" : currentOffsetBits));
        return sb.toString();
    }

    public Integer getCurrentEA() { return currentEA; }
    public String getCurrentTagBits() { return currentTagBits; }
    public String getCurrentIndexBits() { return currentIndexBits; }
    public String getCurrentOffsetBits() { return currentOffsetBits; }
    public int getCurrentInputStartCycle() { return currentInputStartCycle; }
    

    /** Return structured cache line rows as maps for GUI: keys = set, valid, tag, block */
    public java.util.List<java.util.Map<String,String>> getLineRows() {
        java.util.List<java.util.Map<String,String>> out = new java.util.ArrayList<>();
        int numBlocks = lines.length;
        int indexBits = 0; while ((1 << indexBits) < numBlocks) indexBits++;
        int offsetBits = 0; while ((1 << offsetBits) < blockSize) offsetBits++;
        int tagWidth = Math.max(0, 8 - indexBits - offsetBits);
        for (int i = 0; i < lines.length; i++) {
            Line L = lines[i];
            java.util.Map<String,String> m = new java.util.HashMap<>();
            String setBin = String.format("%" + Math.max(1, indexBits) + "s", Integer.toBinaryString(i)).replace(' ', '0');
            m.put("set", setBin);
            m.put("valid", L.valid ? "1" : "0");
            if (L.valid) {
                m.put("tag", toBinaryTag(L.tag, tagWidth));
                    // Prefer to display the block starting at the effective address
                    // that last populated this line (if available). Otherwise fall
                    // back to the canonical blockStart computed from tag/index.
                    int blockStart;
                    if (L.lastLoadedEA >= 0) {
                        blockStart = L.lastLoadedEA & 0xFF;
                    } else {
                        blockStart = ((L.tag * numBlocks) + i) * blockSize;
                        blockStart = blockStart & 0xFF;
                    }
                    StringBuilder sb = new StringBuilder(); sb.append("[");
                    for (int b = 0; b < blockSize; b++) {
                        if (b > 0) sb.append(",");
                        int addr = (blockStart + b) & 0xFF;
                        sb.append(addr);
                    }
                    sb.append("]");
                    m.put("block", sb.toString());
            } else {
                m.put("tag", "");
                m.put("block", "");
            }
            out.add(m);
        }
        return out;
    }

    /** Return true if the address currently hits in cache (line valid and tag matches). */
    public boolean isHit(int address) {
        if (memory == null) return false;
        int ea = address & 0xFF;
        int blockIndex = (ea / blockSize) % lines.length;
        int tag = ea / (blockSize * lines.length);
        Line line = lines[blockIndex];
        return line.valid && line.tag == tag;
    }

    /** Provide a human-readable breakdown of an 8-bit EA into binary and tag/index/offset. */
    public String formatAddressBreakdown(int address) {
        int ea = address & 0xFF; // 8-bit address space as required
        String bin = String.format("%8s", Integer.toBinaryString(ea)).replace(' ', '0');

        int offsetBits = 0;
        int b = blockSize;
        while ((1 << offsetBits) < b) offsetBits++;
        int numBlocks = lines.length;
        int indexBits = 0;
        while ((1 << indexBits) < numBlocks) indexBits++;
        int tagBits = 8 - indexBits - offsetBits;

        int offset = ea % blockSize;
        int index = (ea / blockSize) % numBlocks;
        int tag = ea / (blockSize * numBlocks);

        String tagBin = tagBits > 0 ? String.format("%" + tagBits + "s", Integer.toBinaryString(tag)).replace(' ', '0') : "";
        String idxBin = indexBits > 0 ? String.format("%" + indexBits + "s", Integer.toBinaryString(index)).replace(' ', '0') : "";
        String offBin = offsetBits > 0 ? String.format("%" + offsetBits + "s", Integer.toBinaryString(offset)).replace(' ', '0') : "";

        StringBuilder sb = new StringBuilder();
        sb.append("EA=").append(ea).append(" --> BIN=").append(bin).append("\n");
        sb.append(ea).append("  ->  ").append(bin).append("\n\n");
        sb.append("Tag | Index | Offset\n");
        sb.append(tagBin).append("   ").append(idxBin).append("      ").append(offBin).append("\n");
        return sb.toString().trim();
    }

    /** Write a single byte to memory and update cache line if present. */
    public void storeByte(int address, byte value) {
        if (memory == null) return;
        int ea = address & 0xFF;
        memory.storeByte(ea, value);

        int blockIndex = (ea / blockSize) % lines.length;
        int tag = ea / (blockSize * lines.length);
        Line line = lines[blockIndex];
        if (line.valid && line.tag == tag) {
            int offset = ea % blockSize;
            line.data[offset] = value;
        }
    }

    /** Return a human-readable summary of cache lines for the GUI. */
    public String[] getLineSummary() {
        String[] out = new String[lines.length];
        int indexBits = 0;
        int numBlocks = lines.length;
        while ((1 << indexBits) < numBlocks) indexBits++;
        for (int i = 0; i < lines.length; i++) {
            Line L = lines[i];
            String setBin = String.format("%" + indexBits + "s", Integer.toBinaryString(i)).replace(' ', '0');
            String valid = L.valid ? "1" : "0";
            int offsetBits = 0; while ((1 << offsetBits) < blockSize) offsetBits++;
            int tagWidth = Math.max(0, 8 - indexBits - offsetBits);
            String tagField = L.valid ? toBinaryTag(L.tag, tagWidth) : "-";
            // Use lastLoadedEA if present so the block list reflects the EA used
            // to populate the line (start at EA and list EA..EA+blockSize-1).
            int displayStart = (L.valid && L.lastLoadedEA >= 0) ? (L.lastLoadedEA & 0xFF) : ((i * blockSize) & 0xFF);
            String blockStr = L.valid ? byteArrayToDecList(L.data, displayStart) : "";
            // Format: Set | Valid | Tag | Block
            out[i] = String.format("%s     %s      %s    %s", setBin, valid, tagField, blockStr);
        }
        return out;
    }

    private static String toBinaryTag(int tag, int width) {
        if (width <= 0) return String.valueOf(tag);
        return String.format("%" + width + "s", Integer.toBinaryString(tag)).replace(' ', '0');
    }

    private static String byteArrayToDecList(byte[] a, int blockStart) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < a.length; i++) {
            if (i > 0) sb.append(",");
            int val = (a[i] & 0xFF);
            int addr = (blockStart + i) & 0xFF;
            sb.append(addr);
        }
        sb.append("]");
        return sb.toString();
    }

    /** Public helper to format a block snapshot as decimal-address list for event messages. */
    public String formatBlockAsDecimalList(byte[] block, int blockStart) {
        return byteArrayToDecList(block, blockStart);
    }

    private static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < a.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(String.format("%02X", a[i]));
        }
        sb.append("]");
        return sb.toString();
    }

}
