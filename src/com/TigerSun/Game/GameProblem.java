package com.TigerSun.Game;

public abstract class GameProblem {
    public final PM pm;

    public GameProblem(final PM pm2) {
        this.pm = pm2;
    }

    public abstract int endTest (final GameState state);

    public abstract Record executeAction (final Record preRecord,
            final Object action);

    public abstract boolean isLegalMove (final Record preRecord,
            final Object action);
}
