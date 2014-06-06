package com.TigerSun.MoveMaker;

import com.TigerSun.Game.Record;

public class MiniMaxNode implements Comparable<MiniMaxNode> {
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
        return Double.compare(this.utility, anotherNode.utility);
    }
}
