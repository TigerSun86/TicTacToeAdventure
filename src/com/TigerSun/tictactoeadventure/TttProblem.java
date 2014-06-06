package com.TigerSun.tictactoeadventure;

import com.TigerSun.Game.GameProblem;
import com.TigerSun.Game.GameState;
import com.TigerSun.Game.PM;
import com.TigerSun.Game.Record;
import com.TigerSun.tictactoeadventure.util.Position;

public class TttProblem extends GameProblem {
    public static final int MAX_LEVEL = 4;
    public static final int MAX_ROW = 4;
    public static final int MAX_COLUMN = 4;
    public static final int WINCOUNT = 4;

    public TttProblem(final PM pm2) {
        super(pm2);
    }

    @Override
    public Record executeAction (final Record preRecord, final Object action) {
        final int player = preRecord.nextPlayer;
        final int nextPlayer;
        if (player == PM.P_1) {
            nextPlayer = PM.P_2;
        } else {
            nextPlayer = PM.P_1;
        }

        final TttState newState = new TttState ((TttState) preRecord.state);

        final Position pos = (Position) action;
        newState.set(pos.level, pos.row, pos.column, player);
        final int winner = endTest(newState);

        return new Record(newState, pos, player, nextPlayer, winner);
    }
    
    @Override
    public int endTest (final GameState state) {
        final TttState s = (TttState) state;
        return s.endTest();
    }

    @Override
    public boolean isLegalMove (Record preRecord, Object action) {
        final TttState s = (TttState) preRecord.state;
        final Position pos = (Position) action;
        final int l = pos.level;
        final int r = pos.row;
        final int c = pos.column;
        boolean isLegal = true;
        // check whether the move is out of bound
        if (l >= MAX_LEVEL || l < 0) {
            isLegal = false;
        } else if (r >= MAX_ROW || r < 0) {
            isLegal = false;
        } else if (c >= MAX_COLUMN || c < 0) {
            isLegal = false;
        }

        if (isLegal) {
            // check whether the move is in the spot has been occupied
            if (s.get(l, r, c) != PM.P_EMPTY) {
                isLegal = false;
            }
        }
        return isLegal;
    }
}
