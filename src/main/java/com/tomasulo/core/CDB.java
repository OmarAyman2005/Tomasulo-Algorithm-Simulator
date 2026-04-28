package com.tomasulo.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Central Data Bus (CDB)
 * Carries:
 *   - tag (which RS produced the value)
 *   - value (double result)
 *
 * All RS and Load/Store buffers listen to the CDB.
 */
public class CDB {

    public static class CDBMessage {
        public final String tag;
        public final double value;

        public CDBMessage(String tag, double value) {
            this.tag = tag;
            this.value = value;
        }
    }

    private final List<CDBMessage> broadcasts = new ArrayList<>();

    public void broadcast(String tag, double value) {
        broadcasts.add(new CDBMessage(tag, value));
    }

    public List<CDBMessage> getBroadcasts() {
        return broadcasts;
    }

    public void clear() {
        broadcasts.clear();
    }
}
