package com.TigerSun.tictactoeadventure.util;

public class Position {
    public static final Position INVALID = new Position(-1, -1, -1);
    
    public int level;
    public int row;
    public int column;

    public Position(int level, int row, int column) {
        this.level = level;
        this.row = row;
        this.column = column;
    }

    @Override
    public boolean equals (Object o) {
        if (!(o instanceof Position)) {
            return false;
        } else {
            final Position p = (Position) o;
            if (this.level == p.level && this.row == p.row
                    && this.column == p.column) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public String toString () {
        return level + " " + row + " " + column;
    }
}
