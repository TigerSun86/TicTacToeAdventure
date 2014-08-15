package com.TigerSun.tictactoeadventure;

import java.util.ArrayList;

import com.TigerSun.Game.GameState;
import com.TigerSun.Game.PM;
import com.TigerSun.tictactoeadventure.util.Position;

/**
 * FileName: TttState.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jun 6, 2014 2:12:37 PM
 */
public class TttState extends GameState {
    public static final int ATTR_COUNT = 9;
    
    private final long[] board;

    public TttState() {
        // For convenient. Actually only use board[1] and board[2].
        this.board = new long[PM.P_2 + 1];
    }
    
    public TttState(final long xb, final long ob) {
        // For convenient. Actually only use board[1] and board[2].
        this.board = new long[PM.P_2 + 1];
        this.board[PM.P_1]  = xb;
        this.board[PM.P_2]  = ob;
    }
    
    public TttState(TttState s) {
        // For convenient. Actually only use board[1] and board[2].
        this.board = new long[PM.P_2 + 1];
        this.board[PM.P_1] = s.board[PM.P_1];
        this.board[PM.P_2] = s.board[PM.P_2];
    }
    
    @Override
    public String toString(){
        return board[PM.P_1] +" "+board[PM.P_2];
    }
    
    public int get (int l, int r, int c) {
        final int index = getBitIndex(l, r, c);
        final long mask = 1L << index;
        if ((board[PM.P_1] & mask) != 0) {
            return PM.P_1;
        } else if ((board[PM.P_2] & mask) != 0) {
            return PM.P_2;
        } else {
            return PM.P_EMPTY;
        }
    }

    public void set (int l, int r, int c, int p) {
        final int index = getBitIndex(l, r, c);
        final long mask = 1L << index;
        board[p] |= mask;
    }

    public int endTest () {
        if (isFull()) {
            return PM.TIE;
        }
        for (long route : winRoutes) {
            if (Long.bitCount(board[PM.P_1] & route) == 4) {
                return PM.P_1;
            } else if (Long.bitCount(board[PM.P_2] & route) == 4) {
                return PM.P_2;
            }
        }
        return PM.NOT_END;
    }

    public ArrayList<Object> getEmptyPostions () {
        final ArrayList<Object> poss = new ArrayList<Object>();
        final long total = board[PM.P_1] | board[PM.P_2];
        for (int i = 0; i < 64; i++) {
            final long mask = 1L << i;
            if ((total & mask) == 0) { // Position is empty.
                poss.add(getPosition(i));
            }
        }
        return poss;
    }

    private static final double ATTACK_SCORE = 1.0;
    private static final double DEFENSE_SCORE = -1.0;

    public ArrayList<Double> getAttributs (final int p) {
        final int op = (p == PM.P_1) ? PM.P_2 : PM.P_1;
        final int[] pFea = new int[5]; // Do not use pFea[0]
        final int[] oFea = new int[5]; // Do not use oFea[0]
        for (long r : winRoutes) {
            final long pr = board[p] & r;
            final long or = board[op] & r;
            if (pr != 0 && or == 0) {
                final int pc = Long.bitCount(pr);
                pFea[pc]++;
            } else if (pr == 0 && or != 0) {
                final int oc = Long.bitCount(or);
                oFea[oc]++;
            }
        }
        final ArrayList<Double> attrs = new ArrayList<Double>();
        for (int i = 1; i < pFea.length; i++) {
            attrs.add((double) pFea[i]); // Add my features.
        }
        for (int i = 1; i < oFea.length; i++) {
            attrs.add((double) oFea[i]); // Add opponent's features.
        }
        // Attack turn. Here just get next player from the board, because the
        // next player doesn't stored in GameState, and I don't want to change
        // the structure.
        final int p1c = Long.bitCount(board[PM.P_1]);
        final int p2c = Long.bitCount(board[PM.P_2]);
        final int nextP = (p1c == p2c) ? PM.P_1 : PM.P_2;
        if (nextP == p) {
            attrs.add(ATTACK_SCORE);
        } else {
            attrs.add(DEFENSE_SCORE);
        }
        
        // Make all attributes to 0 - 1.
        attrs.set(0, attrs.get(0)/24);
        attrs.set(1, attrs.get(1)/10);
        attrs.set(2, attrs.get(2)/2);
        attrs.set(3, attrs.get(3)/1);
        attrs.set(4, attrs.get(4)/23);
        attrs.set(5, attrs.get(5)/9);
        attrs.set(6, attrs.get(6)/2);
        attrs.set(7, attrs.get(7)/1);
        return attrs;
    }

    private boolean isFull () {
        final int count =
                Long.bitCount(board[PM.P_1]) + Long.bitCount(board[PM.P_2]);
        return count == 64;
    }

    private static int getBitIndex (int l, int r, int c) {
        return l * 16 + r * 4 + c;
    }

    private static Position getPosition (int index) {
        final int l = index / 16;
        index %= 16;
        final int r = index / 4;
        index %= 4;
        final int c = index;
        return new Position(l, r, c);
    }

    private static long[] winRoutes = {
            // Straight
            // level changes, other remain
            0x0001000100010001L,
            0x0002000200020002L,
            0x0004000400040004L,
            0x0008000800080008L,
            0x0010001000100010L,
            0x0020002000200020L,
            0x0040004000400040L,
            0x0080008000800080L,
            0x0100010001000100L,
            0x0200020002000200L,
            0x0400040004000400L,
            0x0800080008000800L,
            0x1000100010001000L,
            0x2000200020002000L,
            0x4000400040004000L,
            0x8000800080008000L,
            // row changes, other remain
            0x0000000000001111L,
            0x0000000000002222L,
            0x0000000000004444L,
            0x0000000000008888L,
            0x0000000011110000L,
            0x0000000022220000L,
            0x0000000044440000L,
            0x0000000088880000L,
            0x0000111100000000L,
            0x0000222200000000L,
            0x0000444400000000L,
            0x0000888800000000L,
            0x1111000000000000L,
            0x2222000000000000L,
            0x4444000000000000L,
            0x8888000000000000L,
            // column changes, other remain
            0x000000000000000FL,
            0x00000000000000F0L,
            0x0000000000000F00L,
            0x000000000000F000L,
            0x00000000000F0000L,
            0x0000000000F00000L,
            0x000000000F000000L,
            0x00000000F0000000L,
            0x0000000F00000000L,
            0x000000F000000000L,
            0x00000F0000000000L,
            0x0000F00000000000L,
            0x000F000000000000L,
            0x00F0000000000000L,
            0x0F00000000000000L,
            0xF000000000000000L,
            // Diagonal
            // level remains, others change
            0x0000000000008421L, 0x0000000084210000L,
            0x0000842100000000L,
            0x8421000000000000L,
            0x0000000000001248L,
            0x0000000012480000L,
            0x0000124800000000L,
            0x1248000000000000L,
            // row remains, others change
            0x0008000400020001L, 0x0080004000200010L, 0x0800040002000100L,
            0x8000400020001000L, 0x0001000200040008L,
            0x0010002000400080L,
            0x0100020004000800L,
            0x1000200040008000L,
            // column remains, others change
            0x1000010000100001L, 0x2000020000200002L, 0x4000040000400004L,
            0x8000080000800008L, 0x0001001001001000L, 0x0002002002002000L,
            0x0004004004004000L, 0x0008008008008000L,
            // All change
            0x8000040000200001L, 0x0001002004008000L, 0x0008004002001000L,
            0x1000020000400008L, };
}
