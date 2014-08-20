package com.TigerSun.tictactoeadventure;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

import com.TigerSun.Game.GameRecorder;
import com.TigerSun.Game.Hypothesis;
import com.TigerSun.Game.Learner;
import com.TigerSun.Game.LmsHypo;
import com.TigerSun.Game.NeuralNetwork;
import com.TigerSun.Game.PM;
import com.TigerSun.Game.PerformanceSystem;
import com.TigerSun.Game.Record;
import com.TigerSun.MoveMaker.AlphaBetaPlayer;
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
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(2);
        linePaint.setAntiAlias(true);
    }
    private static final int SEL_PAINT_INDEX = 0;
    private static final int TIPS_PAINT_INDEX = 3;
    private static final Paint[] playerPaint;
    static {
        playerPaint = new Paint[PM.P_2 + 1 + 1];
        playerPaint[0] = new Paint(); // for selected.
        playerPaint[0].setColor(Color.RED);
        playerPaint[0].setStyle(Paint.Style.STROKE);
        playerPaint[1] = new Paint();
        playerPaint[1].setColor(Color.RED);
        playerPaint[1].setAntiAlias(true);
        playerPaint[2] = new Paint();
        playerPaint[2].setColor(Color.YELLOW);
        playerPaint[2].setAntiAlias(true);
        playerPaint[3] = new Paint(); // for tips.
        playerPaint[3].setColor(Color.GREEN);
        playerPaint[3].setStyle(Paint.Style.STROKE);
    }

    private static final Record INIT_GAME = new Record(new TttState(),
            new Position(-1, -1, -1), PM.P_INVALID, PM.P_1, PM.NOT_END);

    private Thread th;
    private SurfaceHolder sfh;
    private Canvas canvas;
    private boolean isRunning;
    private BoardLines boardLines;
    private PerformanceSystem perform;
    private AlphaBetaPlayer learntAi = null;

    private Position recommendedPos = null;
    private Position selectedPos = null;

    private int p1Type;
    private int p1Depth;
    private int p2Type;
    private int p2Depth;

    private boolean waitInput = false;

    public Handler handler = null;

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setKeepScreenOn(false);
        this.setLongClickable(true);

        sfh = this.getHolder();
        sfh.addCallback(this);

        Log.e(MODEL, "MySurfaceView Constructor");
    }

    public void setPlayer (int p1, int p2, int d1, int d2) {
        if (p1 == PM.T_HUMAN) {
            p1Type = PM.T_HUMAN;
            p1Depth = 0;
        } else if (p1 == 4) { // custom
            p1Type = PM.T_ML;
            p1Depth = d1;
        } else {
            p1Type = PM.T_AI;
            p1Depth = p1;
        }

        if (p2 == PM.T_HUMAN) {
            p2Type = PM.T_HUMAN;
            p2Depth = 0;
        } else if (p2 == 4) { // custom
            p2Type = PM.T_ML;
            p2Depth = d2;
        } else {
            p2Type = PM.T_AI;
            p2Depth = p2;
        }
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

    private void initializeGame () {
        initMlAi(1);

        final MoveMaker player1 = initPlayer(p1Type, p1Depth);
        final MoveMaker player2 = initPlayer(p2Type, p2Depth);

        final PM pm = new PM();
        pm.setPlayer(PM.P_1, player1, p1Type);
        pm.setPlayer(PM.P_2, player2, p2Type);
        final TttProblem prob = new TttProblem(pm);
        final GameRecorder initGr = new GameRecorder(INIT_GAME);
        perform = new PerformanceSystem(prob, initGr);
    }

    private void initMlAi (final int depth) {
        Hypothesis h = RecordWriter.readAI();
        if (h == null) {
            h = new LmsHypo(TttState.ATTR_COUNT);
        }
        final TttAnalyser analyser = new TttAnalyser(h);
        learntAi = new AlphaBetaPlayer(analyser, depth);
    }

    private MoveMaker initPlayer (final int type, final int depth) {
        final MoveMaker player;
        if (type == PM.T_HUMAN) {
            player = null;
        } else if (type == PM.T_ML) { // machine learning ai.
            player = learntAi;
        } else {
            final TttAnalyser analyser = new TttAnalyser(LmsHypo.H_SAMPLE);
            player = new AlphaBetaPlayer(analyser, depth);
        }
        return player;
    }

    private void saveGame () {
        if (perform.isEnd()) {
            if (perform.winner() == PM.TIE) {
                setTestView("Game tie");
            } else {
                setTestView("Winner is player " + perform.winner());
            }
            Log.d(MODEL, "Player " + perform.winner() + " won");

            // Let machine learning AI learn this game.
            Learner.learnOneGame(perform.prob.pm, perform.gr, learntAi.analyser);
            // Save current machine learning AI to file.
            RecordWriter.writeAI(learntAi.analyser.h);
            // Save this game record.
            RecordWriter.writeRecord(perform.gr);
        }
    }

    public void draw () {
        try {
            canvas = sfh.lockCanvas();
            if (canvas != null) {
                drawBoard();
                drawPieces();
                drawSelectedAndRecommended();
            }
        } catch (Exception e) {
            Log.e(MODEL, "draw is Error!");
        } finally {
            if (canvas != null) sfh.unlockCanvasAndPost(canvas);
        }
    }

    private void drawBoard () {
        canvas.drawColor(Color.BLACK); // Clear screen.
        for (Line line : boardLines.getLineSet()) {
            canvas.drawLine(line.startX, line.startY, line.stopX, line.stopY,
                    linePaint);
        }
    }

    private void drawOnePiece (int l, int r, int c, final Paint paint) {
        final PointF p = boardLines.getPointOfPiece(l, r, c);
        canvas.drawCircle(p.x, p.y, boardLines.getRadius(), paint);
    }

    private void drawSelectedAndRecommended () {
        if (selectedPos != null) {
            drawOnePiece(selectedPos.level, selectedPos.row,
                    selectedPos.column, playerPaint[SEL_PAINT_INDEX]);
        }
        if (recommendedPos != null) {
            drawOnePiece(recommendedPos.level, recommendedPos.row,
                    recommendedPos.column, playerPaint[TIPS_PAINT_INDEX]);
        }
    }

    private void drawPieces () {
        final TttState tttState = (TttState) perform.getLastState();

        for (int l = 0; l < 4; l++) {
            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < 4; c++) {
                    final int player = tttState.get(l, r, c);
                    if (player != PM.P_EMPTY) {
                        drawOnePiece(l, r, c, playerPaint[player]);
                    }
                }
            }
        }
    }

    private void drawImagineLine (Position p) {
        final TttState tttState = (TttState) perform.getLastState();
        int player = tttState.get(p.level, p.row, p.column);
        if (player == PM.P_EMPTY) {
            player = perform.gr.getLastRecord().nextPlayer;
        }
        final Paint paint = playerPaint[player];

        final ArrayList<ArrayList<Position>> routes =
                TttState.getRelatedPostions(p);
        for (ArrayList<Position> r : routes) {
            for (Position pt : r) {
                final PointF fp =
                        boardLines.getPointOfPiece(pt.level, pt.row, pt.column);
                canvas.drawCircle(fp.x, fp.y, boardLines.getRadius() / 2, paint);
            }

            final Position p1 = r.get(0);
            final Position p2 = r.get(3);
            final PointF fp1 =
                    boardLines.getPointOfPiece(p1.level, p1.row, p1.column);
            final PointF fp2 =
                    boardLines.getPointOfPiece(p2.level, p2.row, p2.column);
            canvas.drawLine(fp1.x, fp1.y, fp2.x, fp2.y, paint);
        }
    }

    private void drawTouch (Position pos) {
        try {
            canvas = sfh.lockCanvas();
            if (canvas != null) {
                drawBoard();
                drawPieces();
                drawSelectedAndRecommended();
                drawImagineLine(pos);
            }
        } catch (Exception e) {
            Log.e(MODEL, "draw is Error!");
        } finally {
            if (canvas != null) sfh.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void run () {
        draw();
        initializeGame();
        while (isRunning && !perform.isEnd()) {
            waitInput = perform.next();
            if (waitInput) { // Give tips when human's turn.
                getTips();
                draw();
            }

            while (waitInput) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            draw();

            final String s =
                    "Player "
                            + perform.gr.getLastRecord().player
                            + " moved in "
                            + ((Position) perform.gr.getLastRecord().action)
                                    .toString();
            setTestView(s);
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        saveGame();
    }

    @Override
    public boolean onTouchEvent (final MotionEvent event) {
        if (perform.getCurPlayerType() != PM.T_HUMAN || perform.isEnd()) {
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
                    selectedPos = pos;
                    setOKBTEnabled(true);
                } else { // clicked to cancel selection.
                    selectedPos = null;
                    setOKBTEnabled(false);
                }
                drawTouch(pos);
            }
        }
        return true;
    }

    private void getTips () {
        final Record lastRecord = perform.gr.getLastRecord();
        final MoveMaker moveMaker = learntAi;
        final Position action =
                (Position) moveMaker.makeMove(perform.prob, lastRecord);
        recommendedPos = action;
        // Human's default selected position is the one recommended by
        // tips.
        selectedPos = action;

        final String s =
                "The machine learning AI suggests " + action.toString();
        setTestView(s);

        setOKBTEnabled(true);
    }

    public OnClickListener okBTListener = new OnClickListener() {
        @Override
        public void onClick (View v) {
            assert selectedPos != null;

            perform.makeHumMove(selectedPos);
            waitInput = false;
            Log.d(MODEL, "Humman move " + selectedPos);

            recommendedPos = null;
            selectedPos = null;

            setOKBTEnabled(false);
        }
    };

    public static final int MSG_BT = 0;
    public static final int MSG_TV = 1;

    private void setOKBTEnabled (boolean enabled) {
        Message message = Message.obtain();
        message.what = MSG_BT;
        message.obj = enabled;
        handler.sendMessage(message);
    }

    private void setTestView (String s) {
        Message message = Message.obtain();
        message.what = MSG_TV;
        message.obj = s;
        handler.sendMessage(message);
    }

    private void trainAI (int times) {
        final PM pm = new PM();
        pm.setPlayer(PM.P_1, null, PM.T_AI);
        pm.setPlayer(PM.P_2, null, PM.T_AI);
        Learner.learn(pm, learntAi.analyser, times);
        Log.d(MODEL, learntAi.analyser.h.toString());
    }

    private AlphaBetaPlayer trainAIOfNn (int depth) {
        final PM pm = new PM();
        pm.setPlayer(PM.P_1, null, PM.T_AI);
        pm.setPlayer(PM.P_2, null, PM.T_AI);
        final ArrayList<Integer> nHidden = new ArrayList<Integer>();
        nHidden.add(9);
        final TttAnalyser analyser =
                new TttAnalyser(new NeuralNetwork(9, nHidden, true, 1, false,
                        0.1, 0.1));

        // Learner2.learn(pm, analyser);

        Log.d(MODEL, analyser.h.toString());
        return new AlphaBetaPlayer(analyser, 1);
    }

    private int count = 0;
    private int mlplayer = PM.P_1;

    private void loopTest () {

        if (perform.isEnd()) {
            if (perform.winner() == PM.TIE) {
                // drawMsg("Game tie");
            } else {
                // drawMsg("Winner is player " + perform.winner());
            }
            Log.d(MODEL, "Player " + perform.winner() + " won");
            String winner;
            if (perform.winner() == PM.TIE) {
                winner = "T";
            } else if (perform.winner() == mlplayer) {
                winner = "M";
            } else {
                winner = "A";
            }
            // RecordWriter.stringWrite(winner);

            final AlphaBetaPlayer player1 =
                    ((AlphaBetaPlayer) perform.prob.pm.getMoveMaker(PM.P_1));
            final AlphaBetaPlayer player2 =
                    ((AlphaBetaPlayer) perform.prob.pm.getMoveMaker(PM.P_2));

            AlphaBetaPlayer mlp = (mlplayer == PM.P_1 ? player1 : player2);
            Learner.learnOneGame(perform.prob.pm, perform.gr, mlp.analyser);

            perform.prob.pm.setPlayer(PM.P_1, player2, PM.T_AI);
            perform.prob.pm.setPlayer(PM.P_2, player1, PM.T_AI);

            mlplayer = (mlplayer == PM.P_1 ? PM.P_2 : PM.P_1);

            // RecordWriter.stringWrite3(((NeuralNetwork)mlp.analyser.h).getLayer(1).toString());
            // RecordWriter.stringWrite3(mlp.analyser.h.toString());
            count++;
            if (count < 1000) {
                final GameRecorder initGr = new GameRecorder(INIT_GAME);
                perform.gr = initGr;
            }

        }

    }
}
