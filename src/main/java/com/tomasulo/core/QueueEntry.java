package com.tomasulo.core;

/**
 * Lightweight view model for the GUI Queue table.
 */
public class QueueEntry {

    public final int index;
    public int iteration = -1; // -1 means not applicable
    public final String instructionText;

    // timing fields (-1 = not set)
    public int issue = -1;
    public int execStart = -1;
    public int execEnd = -1;
    public int write = -1;

    public QueueEntry(int index, String instructionText) {
        this.index = index;
        this.instructionText = instructionText;
    }

    public String getInstructionText() { return instructionText; }

    public String getIssueString() { return issue < 0 ? "" : String.valueOf(issue); }
    public String getExecuteString() {
        if (execStart < 0) return "";
        if (execEnd < 0) return String.valueOf(execStart) + "..";
        return execStart + ".." + execEnd;
    }
    public String getWriteString() { return write < 0 ? "" : String.valueOf(write); }
    public String getIterationString() { return iteration < 0 ? "" : String.valueOf(iteration); }
}
