package com.TigerSun.MoveMaker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

import com.TigerSun.Game.GameAnalyser;
import com.TigerSun.Game.GameProblem;
import com.TigerSun.Game.PM;
import com.TigerSun.Game.Record;

public class AlphaBetaPlayer implements MoveMaker {
    private static final boolean ORDER_ASCENDING = true;
    private static final boolean ORDER_DESCENDING = false;
    private static final int CAPA = 10;
    private static final double RANDOM_FACTOR = 0.00001;

    private final GameAnalyser analyser;
    private final int depth;
    private int player;
    private boolean randomAmongBests = true;
    
    public AlphaBetaPlayer(final GameAnalyser analyser2, final int depth2) {
        this.analyser = analyser2;
        this.depth = depth2;
    }

    @Override
    public Object makeMove (final GameProblem gp, final Record record) {
        this.player = record.nextPlayer;
        // Get last game record.
        final MiniMaxNode rootNode = new MiniMaxNode(record, 0, 0);
        // Descending order.
        final PriorityQueue<MiniMaxNode> frontier =
                generateChildNodes(gp, rootNode, ORDER_DESCENDING);

        double max = Double.NEGATIVE_INFINITY;
        MiniMaxNode best = null;
        while (!frontier.isEmpty()) {
            // Check successors in order with utility descending.
            final MiniMaxNode child = frontier.remove();
            final double v = minValue(gp, child, max, Double.POSITIVE_INFINITY);
            if (Double.compare(max, v) < 0) {
                max = v;
                best = child;
            }
        }

        return best.record.action;
    }
    
    public void setRandom(final boolean randomAmongBests2){
        this.randomAmongBests = randomAmongBests2;
    }

    private double minValue (final GameProblem gp, final MiniMaxNode node,
            final double a, final double b) {
        if (terminalTest(gp, node)) {
            return node.utility;
        }
        // Ascending order.
        final PriorityQueue<MiniMaxNode> frontier =
                generateChildNodes(gp, node, ORDER_ASCENDING);

        double myB = b;
        while (!frontier.isEmpty()) {
            // Check successors in order with utility ascending.
            final MiniMaxNode child = frontier.remove();
            myB = Math.min(myB, maxValue(gp, child, a, myB));
            if (Double.compare(myB, a) <= 0) {
                break; // prune all siblings after this.
            }
        }
        return myB;
    }

    private double maxValue (final GameProblem gp, final MiniMaxNode node,
            final double a, final double b) {
        if (terminalTest(gp, node)) {
            return node.utility;
        }
        final PriorityQueue<MiniMaxNode> frontier =
                generateChildNodes(gp, node, ORDER_DESCENDING);

        double myA = a;
        while (!frontier.isEmpty()) {
            // Check successors in order with utility descending.
            final MiniMaxNode child = frontier.remove();
            myA = Math.max(myA, minValue(gp, child, myA, b));
            if (Double.compare(myA, b) >= 0) {
                break; // prune all siblings after this.
            }
        }
        return myA;
    }

    private boolean terminalTest (final GameProblem gp, final MiniMaxNode node) {
        if (node.depth >= depth) {
            return true;
        } else {
            return (node.record.winner != PM.NOT_END);
        }
    }

    private PriorityQueue<MiniMaxNode> generateChildNodes (
            final GameProblem gp, final MiniMaxNode node, final boolean order) {
        final PriorityQueue<MiniMaxNode> frontier;
        if (order == ORDER_ASCENDING) {
            frontier = new PriorityQueue<MiniMaxNode>();
        } else {
            frontier =
                    new PriorityQueue<MiniMaxNode>(CAPA,
                            Collections.reverseOrder());
        }

        final ArrayList<Object> sucs = analyser.getActions(gp, node.record);
        for (Object action : sucs) {
            final Record r = gp.executeAction(node.record, action);
            double utility = analyser.getUtility(gp, r, player);
            if (randomAmongBests){
                utility += RANDOM_FACTOR * Math.random();
            }
            final MiniMaxNode child =
                    new MiniMaxNode(r, node.depth + 1, utility);
            frontier.add(child);
        }

        return frontier;
    }
}
