package com.TigerSun.Game;

public class Record {
    public GameState state; // The state after action.
    public Object action;
    public int player;      // The player made this action.
    public int nextPlayer;  // The player about to make action.
    public int winner;

    public Record(final GameState state2, final Object action2,
            final int player2, final int nextPlayer2, final int winner2) {
        this.state = state2;
        this.action = action2;
        this.player = player2;
        this.nextPlayer = nextPlayer2;
        this.winner = winner2;
    }
}
