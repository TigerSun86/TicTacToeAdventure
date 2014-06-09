package com.TigerSun.tictactoeadventure;

import java.util.ArrayList;

import android.graphics.PointF;

import com.TigerSun.tictactoeadventure.util.Line;
import com.TigerSun.tictactoeadventure.util.Position;

/**
 * FileName: BoardLines.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date May 23, 2014 8:40:40 PM
 */
public class BoardLines {
    private static final float GAP_HOR_TOP_AHEAD = 160f;
    private static final float GAP_HOR_TOP_BEHIND = 20f;
    private static final float GAP_HOR_BOTT_AHEAD = 20f;
    // private static final float GAP_HOR_BOTT_BEHIND = 80f;
    private static final float GAP_VER_AHEAD = 50f;
    private static final float GAP_VER_BEHIND = 50f;
    private static final float GAP_VER_INTERVAL = 50f;

    private static final int MAX_LEVELS = 4;
    private static final int MAX_ROWS = MAX_LEVELS;
    private static final int MAX_COLS = MAX_LEVELS;

    private float oneBoardHeight;
    private float gridWidth;
    private float gridHeight;

    private PointF[][][] intersections;
    private PointF[][][] pieces;
    private ArrayList<Line> lines;
    private float radius;

    public BoardLines (final int w, final int h) {
        initSizeData(w, h);
        initIntersections();
        initPieces();
        initLines();
    }

    public final ArrayList<Line> getLineSet () {
        return lines;
    }

    public final Position getPosOfPiece (final PointF pos) {
        int l = -1;
        int r = -1;
        // Go through all horizontal lines.
        for (int level = 0; level < MAX_LEVELS; level++) {
            for (int row = 0; row < MAX_ROWS + 1; row++) {
                final PointF interP = intersections[level][row][0];
                if (Float.compare(pos.y, interP.y) < 0) {
                    l = level;
                    // Row of position is 1 less than row of intersection.
                    r = row - 1;
                    break;
                }
            }
            if (l != -1) {
                break;
            }
        }
        if (l == -1 || r == -1) {
            return null; // Out of the board.
        }

        int c = -1;
        for (int col = 0; col < MAX_COLS + 1; col++) {
            final PointF interP = intersections[l][r][col];
            if (Float.compare(pos.x, interP.x) < 0) {
                // Column of position is 1 less than Column of intersection.
                c = col - 1;
                break;
            }
        }
        if (c == -1) {
            return null; // Out of the board.
        }

        return new Position(l, r, c);
    }

    public PointF getPointOfPiece (final int l, final int r, final int c) {
        return pieces[l][r][c];
    }

    public float getRadius () {
        return radius;
    }

    private void initSizeData (final int w, final int h) {
        /* Initialize screen size data. */
        final float oneBoardWidth = w - GAP_HOR_TOP_AHEAD - GAP_HOR_TOP_BEHIND;
        oneBoardHeight =
                (h - GAP_VER_AHEAD - GAP_VER_BEHIND - (GAP_VER_INTERVAL * 3))
                        / MAX_LEVELS;

        gridWidth = oneBoardWidth / MAX_COLS;
        gridHeight = oneBoardHeight / MAX_ROWS;
    }

    private void initLines () {
        lines = new ArrayList<Line>();
        // Horizontal lines.
        for (int level = 0; level < MAX_LEVELS; level++) {
            for (int row = 0; row < MAX_ROWS + 1; row++) {
                final PointF startP = intersections[level][row][0];
                final PointF stopP = intersections[level][row][MAX_COLS];
                final Line line = new Line(startP, stopP);
                lines.add(line);
            }
        }
        // Vertical lines.
        for (int level = 0; level < MAX_LEVELS; level++) {
            for (int col = 0; col < MAX_COLS + 1; col++) {
                final PointF startP = intersections[level][0][col];
                final PointF stopP = intersections[level][MAX_ROWS][col];
                final Line line = new Line(startP, stopP);
                lines.add(line);
            }
        }
    }

    private void initIntersections () {
        // 4 levels, every level have 5 intersections in row, 5 intersections in
        // column.
        intersections = new PointF[MAX_LEVELS][MAX_ROWS + 1][MAX_COLS + 1];

        // Level 0, row 0.
        intersections[0][0][0] = new PointF(GAP_HOR_TOP_AHEAD, GAP_VER_AHEAD);
        PointF prior = intersections[0][0][0];
        for (int i = 1; i < MAX_COLS + 1; i++) {
            intersections[0][0][i] = new PointF(prior.x + gridWidth, prior.y);
            prior = intersections[0][0][i];
        }

        float xShift =
                Math.abs(((float) GAP_HOR_TOP_AHEAD - GAP_HOR_BOTT_AHEAD) / 4);

        if (GAP_HOR_TOP_AHEAD > GAP_HOR_BOTT_AHEAD) {
            // Next row will be left shifted.
            xShift *= -1;
        }

        // Level 0, row 1,2,3,4.
        for (int row = 1; row < MAX_ROWS + 1; row++) {
            for (int col = 0; col < MAX_COLS + 1; col++) {
                float newX = intersections[0][0][col].x + (row * xShift);
                float newY = intersections[0][0][col].y + (row * gridHeight);
                intersections[0][row][col] = new PointF(newX, newY);
            }
        }
        // Level 1,2,3.
        float yShift = oneBoardHeight + GAP_VER_INTERVAL;
        for (int level = 1; level < MAX_LEVELS; level++) {
            for (int row = 0; row < MAX_ROWS + 1; row++) {
                for (int col = 0; col < MAX_COLS + 1; col++) {
                    float newX = intersections[0][row][col].x;
                    float newY = intersections[0][row][col].y + yShift;
                    intersections[level][row][col] = new PointF(newX, newY);
                }
            }
            yShift += oneBoardHeight + GAP_VER_INTERVAL;
        }
    }

    private void initPieces () {
        pieces = new PointF[MAX_LEVELS][MAX_ROWS][MAX_COLS];
        for (int l = 0; l < MAX_LEVELS; l++) {
            pieces[l] = new PointF[MAX_ROWS][MAX_COLS];
            for (int r = 0; r < MAX_ROWS; r++) {
                pieces[l][r] = new PointF[MAX_COLS];
                for (int c = 0; c < MAX_COLS; c++) {
                    float newX =
                            (intersections[l][r][c].x
                                    + intersections[l][r][c + 1].x
                                    + intersections[l][r + 1][c].x + intersections[l][r + 1][c + 1].x) / 4;
                    float newY =
                            (intersections[l][r][c].y + intersections[l][r + 1][c].y) / 2;
                    pieces[l][r][c] = new PointF(newX, newY);
                }
            }
        }
        // Set piece radius
        float w = (pieces[0][0][1].x - pieces[0][0][0].x) / 2;
        float h = (pieces[0][1][0].y - pieces[0][0][0].y) / 2;
        radius = (float) (Math.min(w, h) * 0.9);
    }
}
