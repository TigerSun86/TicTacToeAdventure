package com.TigerSun.tictactoeadventure;

import java.util.ArrayList;

import com.TigerSun.Game.GameAnalyser;
import com.TigerSun.Game.GameProblem;
import com.TigerSun.Game.GameState;
import com.TigerSun.Game.Hypothesis;
import com.TigerSun.Game.Record;

public class TttAnalyser extends GameAnalyser {
    private static final int ATTRIBUTE_COUNT = 9;

    public TttAnalyser(final Hypothesis h2) {
        super(h2);
    }

    @Override
    public ArrayList<Double> getAttributes (final GameState state,
            final int player) {
        final TttState s = (TttState) state;
        return s.getAttributs(player);
    }

    @Override
    public ArrayList<Object> getActions (final GameProblem gp,
            final Record record) {
        final TttState state = (TttState) record.state;
        return state.getEmptyPostions();
    }

    public static int attributesCount () {
        return ATTRIBUTE_COUNT;
    }
}
