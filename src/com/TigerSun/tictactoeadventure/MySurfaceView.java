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
    public static final int MSG_BT = 0;
    public static final int MSG_TV = 1;
    public Handler handler = null;

    private static final String MODEL = "MySurfaceView";

    private static final int PAINT_BOARD = 0;
    private static final int PAINT_P1 = 1; // The same with PM.P_1.
    private static final int PAINT_P2 = 2; // The same with PM.P_2.
    private static final int PAINT_P1_SEL = 3;
    private static final int PAINT_P2_SEL = 4;
    private static final int PAINT_P1_LINE = 5;
    private static final int PAINT_P2_LINE = 6;
    private static final int PAINT_P1_P = 7;
    private static final int PAINT_P2_P = 8;
    private static final int PAINT_P1_LAST = 9;
    private static final int PAINT_P2_LAST = 10;
    private static final int PAINT_TIPS = 11;

    private static final Paint[] PAINTS;
    static {
        final int ORANGE = 0xffffa500;
        final int HOTPINK = 0xffff69b4;
        PAINTS = new Paint[PAINT_TIPS + 1];
        // For board.
        PAINTS[PAINT_BOARD] = new Paint();
        PAINTS[PAINT_BOARD].setColor(Color.WHITE);
        PAINTS[PAINT_BOARD].setStrokeWidth(2);
        PAINTS[PAINT_BOARD].setAntiAlias(true);
        // For players.
        PAINTS[PAINT_P1] = new Paint();
        PAINTS[PAINT_P1].setColor(Color.RED);
        PAINTS[PAINT_P1].setAntiAlias(true);
        PAINTS[PAINT_P2] = new Paint();
        PAINTS[PAINT_P2].setColor(ORANGE);
        PAINTS[PAINT_P2].setAntiAlias(true);
        // For selected.
        PAINTS[PAINT_P1_SEL] = new Paint(); // for selected.
        PAINTS[PAINT_P1_SEL].setColor(Color.RED);
        PAINTS[PAINT_P1_SEL].setStyle(Paint.Style.STROKE);
        PAINTS[PAINT_P1_SEL].setStrokeWidth(5);
        PAINTS[PAINT_P1_SEL].setAntiAlias(true);
        PAINTS[PAINT_P2_SEL] = new Paint(); // for selected.
        PAINTS[PAINT_P2_SEL].setColor(ORANGE);
        PAINTS[PAINT_P2_SEL].setStyle(Paint.Style.STROKE);
        PAINTS[PAINT_P2_SEL].setStrokeWidth(5);
        PAINTS[PAINT_P2_SEL].setAntiAlias(true);
        // For imagine line.
        PAINTS[PAINT_P1_LINE] = new Paint(); // for selected.
        PAINTS[PAINT_P1_LINE].setColor(HOTPINK);
        PAINTS[PAINT_P1_LINE].setStrokeWidth(2);
        PAINTS[PAINT_P1_LINE].setAntiAlias(true);
        PAINTS[PAINT_P2_LINE] = new Paint(); // for selected.
        PAINTS[PAINT_P2_LINE].setColor(Color.YELLOW);
        PAINTS[PAINT_P2_LINE].setStrokeWidth(2);
        PAINTS[PAINT_P2_LINE].setAntiAlias(true);
        // For imagine points.
        PAINTS[PAINT_P1_P] = new Paint(); // for selected.
        PAINTS[PAINT_P1_P].setColor(HOTPINK);
        PAINTS[PAINT_P1_P].setAntiAlias(true);
        PAINTS[PAINT_P2_P] = new Paint(); // for selected.
        PAINTS[PAINT_P2_P].setColor(Color.YELLOW);
        PAINTS[PAINT_P2_P].setAntiAlias(true);
        // For last moves.
        PAINTS[PAINT_P1_LAST] = new Paint(); // for selected.
        PAINTS[PAINT_P1_LAST].setColor(HOTPINK);
        PAINTS[PAINT_P1_LAST].setAntiAlias(true);
        PAINTS[PAINT_P2_LAST] = new Paint(); // for selected.
        PAINTS[PAINT_P2_LAST].setColor(Color.YELLOW);
        PAINTS[PAINT_P2_LAST].setAntiAlias(true);
        // For tips.
        PAINTS[PAINT_TIPS] = new Paint();
        PAINTS[PAINT_TIPS].setColor(Color.GREEN);
        PAINTS[PAINT_TIPS].setStyle(Paint.Style.STROKE);
        PAINTS[PAINT_TIPS].setStrokeWidth(5);
        PAINTS[PAINT_TIPS].setAntiAlias(true);
    }

    private static final Record INIT_GAME = new Record(new TttState(),
            Position.INVALID, PM.P_INVALID, PM.P_1, PM.NOT_END);

    private Thread th;
    private SurfaceHolder sfh;
    private Canvas canvas;
    private boolean isRunning;
    private BoardLines boardLines;
    private PerformanceSystem perform;
    private AlphaBetaPlayer learntAi = null;

    private Position recommendedPos = null;
    private Position selectedPos = null;
    private Position imagineLinePos = null;

    private int p1Type;
    private int p1Depth;
    private int p2Type;
    private int p2Depth;

    private boolean waitInput = false;

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
            // Save current machine learning AI to file. Only keep one AI.
            RecordWriter.writeAI(learntAi.analyser.h, false);
            // Save this game record.
            //RecordWriter.writeRecord(perform.gr);
        }
    }

    private void draw () {
        try {
            canvas = sfh.lockCanvas();
            if (canvas != null) {
                drawBoard();
                drawPieces();
                if (perform != null) {
                    drawSelectedAndRecommended();
                    drawLastMove();

                    if (perform.isEnd()) {
                        drawWin();
                    } else {
                        drawImagineLine();
                    }
                }
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
                    PAINTS[PAINT_BOARD]);
        }
    }

    private void drawOnePiece (int l, int r, int c, final Paint paint) {
        final PointF p = boardLines.getPointOfPiece(l, r, c);
        canvas.drawCircle(p.x, p.y, boardLines.getRadius(), paint);
    }

    private void drawSelectedAndRecommended () {
        if (selectedPos != null) {
            final int paintIndex =
                    (perform.gr.getLastRecord().nextPlayer == PM.P_1 ? PAINT_P1_SEL
                            : PAINT_P2_SEL);

            drawOnePiece(selectedPos.level, selectedPos.row,
                    selectedPos.column, PAINTS[paintIndex]);
        }
        if (recommendedPos != null) {
            drawOnePiece(recommendedPos.level, recommendedPos.row,
                    recommendedPos.column, PAINTS[PAINT_TIPS]);
        }
    }

    private void drawPieces () {
        final TttState tttState;
        if (perform != null) {
            tttState = (TttState) perform.getLastState();
        } else { // For the draw before game initialized.
            tttState = (TttState) INIT_GAME.state;
        }

        for (int l = 0; l < 4; l++) {
            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < 4; c++) {
                    final int player = tttState.get(l, r, c);
                    if (player != PM.P_EMPTY) {
                        drawOnePiece(l, r, c, PAINTS[player]);
                    }
                }
            }
        }
    }

    private void drawImagines (final int player,
            final ArrayList<ArrayList<Position>> routes) {
        final Paint linePaint;
        final Paint pointPaint;
        if (player == PM.P_1) {
            linePaint = PAINTS[PAINT_P1_LINE];
            pointPaint = PAINTS[PAINT_P1_P];
        } else {
            linePaint = PAINTS[PAINT_P2_LINE];
            pointPaint = PAINTS[PAINT_P2_P];
        }

        for (ArrayList<Position> r : routes) {
            for (Position pt : r) {
                final PointF fp =
                        boardLines.getPointOfPiece(pt.level, pt.row, pt.column);
                canvas.drawCircle(fp.x, fp.y, boardLines.getRadius() / 2,
                        pointPaint);
            }
            final Position p1 = r.get(0);
            final Position p2 = r.get(3);
            final PointF fp1 =
                    boardLines.getPointOfPiece(p1.level, p1.row, p1.column);
            final PointF fp2 =
                    boardLines.getPointOfPiece(p2.level, p2.row, p2.column);
            canvas.drawLine(fp1.x, fp1.y, fp2.x, fp2.y, linePaint);
        }
    }

    private void drawWin () {
        final int player = perform.winner();
        if (player == PM.TIE) {
            return;
        }

        final TttState tttState = (TttState) perform.getLastState();
        final ArrayList<ArrayList<Position>> routes = tttState.getWinningPoss();
        drawImagines(player, routes);
    }

    private void drawImagineLine () {
        if (imagineLinePos == null) {
            return;
        }

        final TttState tttState = (TttState) perform.getLastState();
        int player =
                tttState.get(imagineLinePos.level, imagineLinePos.row,
                        imagineLinePos.column);
        if (player == PM.P_EMPTY) {
            player = perform.gr.getLastRecord().nextPlayer;
        }

        final ArrayList<ArrayList<Position>> routes =
                TttState.getRelatedPostions(imagineLinePos);
        drawImagines(player, routes);
    }

    private void drawLastMove () {
        final Position lastMove = (Position) perform.gr.getLastRecord().action;
        if (!lastMove.equals(Position.INVALID)) {
            final int paintIndex =
                    (perform.gr.getLastRecord().player == PM.P_1 ? PAINT_P1_LAST
                            : PAINT_P2_LAST);
            final Paint paint = PAINTS[paintIndex];
            final PointF fp =
                    boardLines.getPointOfPiece(lastMove.level, lastMove.row,
                            lastMove.column);
            canvas.drawCircle(fp.x, fp.y, boardLines.getRadius(), paint);
        }
    }

    private void showWin () {
        final String s =
                "Player "
                        + perform.gr.getLastRecord().player
                        + " moved in "
                        + ((Position) perform.gr.getLastRecord().action)
                                .toString();
        setTestView(s);
    }

    @Override
    public void run () {
        draw();
        initializeGame();
        while (isRunning && !perform.isEnd()) {
            waitInput = perform.next(); // Make a move.

            if (waitInput) { // Give tips when human's turn.
                getTips();
                draw();
            }

            while (waitInput) { // For human move.
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            draw();

            showWin();
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
                // Click same position twice will cancel imagine lines.
                if (imagineLinePos != null && imagineLinePos.equals(pos)) {
                    imagineLinePos = null;
                } else {
                    imagineLinePos = pos; // draw imagine line for pos.
                }
                draw();
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

        final String s = "The learnt AI suggests " + action.toString();
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
            imagineLinePos = null; // Clear imagine lines after making move.

            setOKBTEnabled(false);
        }
    };

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
