package com.TigerSun.tictactoeadventure;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.TigerSun.Game.GameRecorder;
import com.TigerSun.Game.PM;
import com.TigerSun.Game.PerformanceSystem;
import com.TigerSun.Game.Record;
import com.TigerSun.MoveMaker.MoveMaker;
import com.TigerSun.tictactoeadventure.util.Line;
import com.TigerSun.tictactoeadventure.util.Position;

/**
 * FileName: MySurfaceView.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date May 23, 2014 8:09:27 PM
 */
public class MySurfaceView extends SurfaceView implements Callback, Runnable {
    private static final String MODEL = "MySurfaceView";

    private static final Paint linePaint;
    static {
        linePaint = new Paint();
        linePaint.setColor(Color.YELLOW);
        linePaint.setStrokeWidth(2);
        linePaint.setAntiAlias(true);
    }
    private static final Paint[] playerPaint;
    static {
        playerPaint = new Paint[PM.P_2 + 1];
        //playerPaint[0] = null; // Empty.
        playerPaint[0] = new Paint();
        playerPaint[0].setColor(Color.WHITE);
        playerPaint[1] = new Paint();
        playerPaint[1].setColor(Color.RED);
        playerPaint[1].setAntiAlias(true);
        playerPaint[2] = new Paint();
        playerPaint[2].setColor(Color.BLUE);
        playerPaint[2].setAntiAlias(true);
    }
    private static final Record INIT_GAME = new Record(new TttState(), null,
            PM.P_INVALID, PM.P_1, PM.NOT_END);

    private Thread th;
    private SurfaceHolder sfh;
    private Canvas canvas;
    private boolean isRunning;
    private BoardLines boardLines;
    private PerformanceSystem perform;

    private boolean waitInput = false;

    private Activity activity;

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setKeepScreenOn(true);
        this.setLongClickable(true);

        sfh = this.getHolder();
        sfh.addCallback(this);

        Log.e(MODEL, "MySurfaceView Constructor");
    }

    public void setPlayer (int p1, int p2) {
        final MoveMaker player1;
        if (p1 == PM.T_HUMAN) {
            player1 = null;
        } else {
            player1 = null;
        }
        final MoveMaker player2;
        if (p2 == PM.T_HUMAN) {
            player2 = null;
        } else {
            player2 = null;
        }
        final PM pm = new PM();
        pm.setPlayer(PM.P_1, player1, p1);
        pm.setPlayer(PM.P_2, player2, p2);
        final TttProblem prob = new TttProblem(pm);
        final GameRecorder initGr = new GameRecorder(INIT_GAME);
        perform = new PerformanceSystem(prob, initGr);
    }

    public void setAct (Activity act) {
        this.activity = act;
    }

    @Override
    public void surfaceCreated (SurfaceHolder holder) {
        boardLines = new BoardLines(this.getWidth(), this.getHeight());
        isRunning = true;

        th = new Thread(this, "surface view thread");
        th.start();
        Log.d(MODEL, "surfaceCreated");
    }

    @Override
    public void surfaceChanged (SurfaceHolder holder, int format, int width,
            int height) {
        Log.d(MODEL, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed (SurfaceHolder holder) {
        isRunning = false;
        Log.d(MODEL, "surfaceDestroyed");
    }

    public void draw (PerformanceSystem perform) {
        try {
            canvas = sfh.lockCanvas();
            if (canvas != null) {
                for (Line line : boardLines.getLineSet()) {
                    canvas.drawLine(line.startX, line.startY, line.stopX,
                            line.stopY, linePaint);
                }
                final TttState tttState = (TttState) perform.getLastState();

                for (int l = 0; l < 4; l++) {
                    for (int r = 0; r < 4; r++) {
                        for (int c = 0; c < 4; c++) {
                            final int player = tttState.get(l, r, c);
                            if (player != PM.P_EMPTY) {
                                final PointF p =
                                        boardLines.getPointOfPiece(l, r, c);
                                canvas.drawCircle(p.x, p.y,
                                        boardLines.getRadius(),
                                        playerPaint[player]);
                            }
                        }
                    }
                }
                
                if (perform.isEnd()) {
                    showWin(perform.winner());
                    Log.d(MODEL, "Player " + perform.winner() + " won");
                }
            }
        } catch (Exception e) {
            Log.e(MODEL, "draw is Error!");
        } finally {
            if (canvas != null) sfh.unlockCanvasAndPost(canvas);
        }
    }

    public void showWin (int p) {
        Paint paint = new Paint(); 
        paint.setColor(Color.WHITE); 
        paint.setTextSize(20); 
        canvas.drawText("Winner is player " + p, 100, 25, paint);
    }

    @Override
    public void run () {
        draw(perform);
        while (isRunning && !perform.isEnd()) {
            waitInput = perform.next();
            while (waitInput) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            draw(perform);
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent (final MotionEvent event) {
        if (perform.getCurPlayerType() != PM.T_HUMAN) {
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            float x = event.getX();
            float y = event.getY();
            final Position pos = boardLines.getPosOfPiece(new PointF(x, y));
            if (pos != null) {
                // Check whether this position has been occupied.
                if (perform.isLegalMove(pos)) {
                    // If empty, make a move in this position.
                    perform.makeHumMove(pos);
                    waitInput = false;
                    Log.d(MODEL, "Humman move " + pos);
                }
            }
        }
        return true;
    }
}
