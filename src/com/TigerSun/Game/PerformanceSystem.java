package com.TigerSun.Game;

import android.util.Log;

import com.TigerSun.MoveMaker.MoveMaker;

public class PerformanceSystem {
    private static final String MODEL = "PerformanceSystem";
    public GameProblem prob;
    public GameRecorder gr;

    public PerformanceSystem(GameProblem prob, GameRecorder gr) {
        this.prob = prob;
        this.gr = gr;
    }

    public boolean next () {
        if (getCurPlayerType() == PM.T_HUMAN) {
            return true;
        } else {
            final Record lastRecord = gr.getLastRecord();
            final int player = lastRecord.nextPlayer;
            final MoveMaker moveMaker = prob.pm.getMoveMaker(player);
            final Object action = moveMaker.makeMove(prob, lastRecord);
            final Record newRecord = prob.executeAction(lastRecord, action);
            gr.addRecord(newRecord);
            Log.d(MODEL, "Player: " + player + " made action: " + action);
            return false;
        }
    }

    public GameState getLastState () {
        return gr.getLastRecord().state;
    }

    public int getCurPlayerType () {
        final Record lastRecord = gr.getLastRecord();
        final int player = lastRecord.nextPlayer;
        return prob.pm.getType(player);
    }

    public void makeHumMove (Object action) {
        final Record lastRecord = gr.getLastRecord();
        final Record newRecord = prob.executeAction(lastRecord, action);
        gr.addRecord(newRecord);
    }

    public boolean isLegalMove (Object action) {
        final Record lastRecord = gr.getLastRecord();
        return prob.isLegalMove(lastRecord, action);
    }

    public boolean isEnd () {
        return gr.getLastRecord().winner != PM.NOT_END;
    }

    public int winner () {
        return gr.getLastRecord().winner;
    }
}
