package com.TigerSun.Game;

import java.util.ArrayList;
import java.util.Arrays;

import com.TigerSun.tictactoeadventure.RecordWriter;

/**
 * FileName: Learner.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Aug 10, 2014 2:36:49 PM
 */
public class Learner {
    public static void learn (final PM pm, final GameAnalyser analyser,
            final int times) {
        final ArrayList<GameRecorder> grl = RecordWriter.readRecord();
        for (int j = 0; j < times; j++) {
            for (int i = 0; i < grl.size(); i++) {
                final GameRecorder gr = grl.get(i);
                learnOneGame(pm, gr, analyser);
            }
        }
    }

    public static void learnOneGame (final PM pm, final GameRecorder recorder,
            final GameAnalyser analyser) {
        for (int player : pm.getPlayerSet()) {
            // Find the last move made by this player. Learning moves by reverse
            // order can be more precise.
            int index = recorder.previousIndexOf(player, recorder.size() - 1);
            while (index != -1) {
                // Get attributes.
                final Record r = recorder.getRecord(index);
                final ArrayList<Double> attrs =
                        analyser.getAttributes(r.state, player);

                // Get Training value.
                // Find the successor of current record.
                final Record nextR;
                final int index2 = recorder.nextIndexOf(player, index + 1);
                if (index2 == -1) {
                    // There is no further move made by the player, use the
                    // hypothesis value of the last record to represent training
                    // value.
                    nextR = recorder.getLastRecord();
                } else {
                    // Use hypothesis value of successor record to represent
                    // training value.
                    nextR = recorder.getRecord(index2);
                }

                final double vTrain = analyser.getUtility(nextR, player);

                // Update hypothesis immediately.
                analyser.h.updateH(attrs, vTrain);

                // For next loop.
                index = recorder.previousIndexOf(player, index - 1);
            }
        }
    }
}
