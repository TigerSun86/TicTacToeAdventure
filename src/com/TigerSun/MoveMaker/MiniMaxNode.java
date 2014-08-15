package com.TigerSun.MoveMaker;

import java.util.Random;

import com.TigerSun.Game.Record;

public class MiniMaxNode implements Comparable<MiniMaxNode> {
    private static final Random RAN = new Random();
    // private static final Random RAN = null;
    
    public Record record;
    public int depth;
    public double utility;

    public MiniMaxNode(final Record record2, final int depth2,
            final double utility2) {
        this.record = record2;
        this.depth = depth2;
        this.utility = utility2;
    }

    @Override
    public int compareTo (final MiniMaxNode anotherNode) {
        int ret = Double.compare(this.utility, anotherNode.utility);
        if (ret == 0 && RAN != null) {
            // If two move have same score, randomly choose one.
            ret = RAN.nextBoolean() == true ? 1 : -1;
        }
        return ret;
    }
}
