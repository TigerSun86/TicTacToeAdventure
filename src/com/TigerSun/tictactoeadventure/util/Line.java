package com.TigerSun.tictactoeadventure.util;

import android.graphics.PointF;

public class Line {
    public final float startX;
    public final float startY;
    public final float stopX;
    public final float stopY;

    public Line(float startX, float startY, float stopX, float stopY) {
        this.startX = startX;
        this.startY = startY;
        this.stopX = stopX;
        this.stopY = stopY;
    }

    public Line(final PointF start, final PointF stop) {
        this.startX = start.x;
        this.startY = start.y;
        this.stopX = stop.x;
        this.stopY = stop.y;
    }
}
