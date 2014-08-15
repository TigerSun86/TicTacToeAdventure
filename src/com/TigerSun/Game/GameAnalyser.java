package com.TigerSun.Game;

import java.util.ArrayList;

public abstract class GameAnalyser {
    public Hypothesis h;

    public GameAnalyser(final Hypothesis h2) {
        this.h = h2;
    }

    /** @return Set of actions. */
    public abstract ArrayList<Object> getActions (final GameProblem gp,
            final Record record);

    public abstract ArrayList<Double> getAttributes (final GameState state,
            final int player);

    private static final double END_SCORE_WIN = 100.0;
    private static final double END_SCORE_TIE = 0;
    private static final double END_SCORE_LOSE = -100.0;

    public double endScore (final int player, final int winner) {
        if (winner == player) {
            return END_SCORE_WIN;
        } else if (winner == PM.TIE) {
            return END_SCORE_TIE;
        } else {
            return END_SCORE_LOSE;
        }
    }
    
    public void setHypothesis (final Hypothesis h2) {
        this.h = h2;
    }

    public double getUtility (final Record record,
            final int player) {
        if (record.winner != PM.NOT_END) {
            return endScore(player, record.winner);
        } else {
            final ArrayList<Double> attrs = getAttributes(record.state, player);
            return h.predict(attrs);
        }
    }
    @Override
    public String toString(){
        return h.toString();
    }
}
