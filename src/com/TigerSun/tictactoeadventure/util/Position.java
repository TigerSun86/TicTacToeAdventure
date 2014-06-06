package com.TigerSun.tictactoeadventure.util;

public class Position {
    public int level;
    public int row;
    public int column;

    public Position(int level, int row, int column) {
        this.level = level;
        this.row = row;
        this.column = column;
    }

    @Override
    public String toString () {
        return String.format("level: %d, row: %d, column: %d ", level, row,
                column);
    }

}
