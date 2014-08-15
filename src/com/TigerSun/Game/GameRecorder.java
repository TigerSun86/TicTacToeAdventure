package com.TigerSun.Game;

import java.util.ArrayList;

public class GameRecorder {
    public final ArrayList<Record> history;
    
    public GameRecorder() {
        history = new ArrayList<Record>();
    }
    
    public GameRecorder(final Record initRecord) {
        history = new ArrayList<Record>();
        history.add(initRecord);
    }

    public GameRecorder(final GameRecorder gr) {
        history = new ArrayList<Record>();
        history.addAll(gr.history);
    }

    public Record getRecord (final int index) {
        if (index >= 0 && index <= history.size() - 1) {
            return history.get(index);
        } else {
            return null;
        }
    }

    /**
     * Get last record.
     */
    public Record getLastRecord () {
        return getRecord(history.size() - 1);
    }

    /**
     * Get next index of record with the specified player, or -1 if there is no
     * such index.
     * Search from the index, inclusive.
     */
    public int nextIndexOf (final int player, final int index) {
        for (int i = index; i <= history.size() - 1; i++) {
            if (history.get(i).player == player) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Get previous index of record with the specified player, or -1 if there is no
     * such index.
     * Search from the index, inclusive.
     */
    public int previousIndexOf (final int player, final int index) {
        final int index2;
        if (index >= history.size()) {
            index2 = history.size()-1;
        } else {
            index2 = index;
        }
        
        for (int i = index2; i >= 0; i--) {
            if (history.get(i).player == player) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Add record at the index, and remove all existing records after it.
     */
    public void addRecord (final int index, final Record suc) {
        assert (index >= 0);

        if (index > history.size() - 1) {
            addRecord(suc);
        } else {
            // Remove all records after index, inclusive.
            removeRecord(index);
            addRecord(suc);
        }
    }

    /**
     * Add one record at the end.
     */
    public void addRecord (final Record suc) {
        history.add(suc);
    }

    /**
     * Remove all records after fromIndex, inclusive.
     */
    public void removeRecord (final int fromIndex) {
        assert (fromIndex >= 0);
        while (fromIndex <= history.size() - 1) {
            // Remove all records after fromIndex, inclusive.
            removeRecord();
        }
    }

    /**
     * Remove last record.
     */
    public void removeRecord () {
        history.remove(history.size() - 1);
    }

    public int size () {
        return history.size();
    }

    public boolean isEmpty () {
        return history.isEmpty();
    }
}
