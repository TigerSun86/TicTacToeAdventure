package com.TigerSun.Game;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class GameAnalyser {
    private Hypothesis h;

    public GameAnalyser(final Hypothesis h2) {
        this.h = h2;
    }

    public abstract double endScore (final int player, final int winner);

    /** @return Set of actions. */
    public abstract HashSet<Object> getActions (final GameProblem gp,
            final Record record);

    public abstract ArrayList<Double> getAttributes (final GameState state,
            final int player);

    public void setHypothesis (final Hypothesis h2) {
        this.h = h2;
    }

    public double getUtility (final GameProblem gp, final Record record,
            final int player) {
        if (record.winner != PM.NOT_END) {
            return endScore(player, record.winner);
        } else {
            final ArrayList<Double> attrs = getAttributes(record.state, player);
            return h.getV(attrs);
        }
    }
}
