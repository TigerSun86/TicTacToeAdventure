package com.TigerSun.Game;

import java.util.HashMap;
import java.util.Set;

import com.TigerSun.MoveMaker.MoveMaker;

public class PM { /* Player Manager */ 
    public static final int P_1 = 1;
    public static final int P_2 = 2;
    public static final int P_EMPTY = 0;
    public static final int P_INVALID = -1;
    public static final int NOT_END = -2;
    public static final int TIE = -3;
    
    public static final int T_HUMAN = 0;
    public static final int T_AI = 1; 
    
    private final HashMap<Integer, MoveMaker> players;
    private final HashMap<Integer, Integer> playerType;
    
    public PM() {
        players = new HashMap<Integer, MoveMaker>();
        playerType = new HashMap<Integer, Integer>();
    }

    public void setPlayer (final int player, final MoveMaker moveMaker, final int type) {
        players.put(player, moveMaker);
        playerType.put(player, type);
    }

    public MoveMaker getMoveMaker (final int player) {
        return players.get(player);
    }
    
    public int getType (final int player) {
        return playerType.get(player);
    }
    
    public Set<Integer> getPlayerSet () {
        return players.keySet();
    }
}
