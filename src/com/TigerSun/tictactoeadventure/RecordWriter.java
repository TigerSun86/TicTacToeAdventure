package com.TigerSun.tictactoeadventure;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.os.Environment;
import android.util.Log;

import com.TigerSun.Game.GameRecorder;
import com.TigerSun.Game.Hypothesis;
import com.TigerSun.Game.LmsHypo;
import com.TigerSun.Game.Record;
import com.TigerSun.tictactoeadventure.util.Position;

/**
 * FileName: RecordWriter.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Aug 8, 2014 11:36:45 PM
 */
public class RecordWriter {
    private static final String MODEL = "RecordWriter";
    private static final String PATH = Environment
            .getExternalStorageDirectory().getPath() + "/Tiger";
    private static final String AIFN = "ai.txt";
    private static final String PRE = "rec";
    private static final String EXT = ".txt";

    public static void writeAI (Hypothesis h) {
        writeString(
                h.getClass().getSimpleName() + " " + h.toString()
                        + String.format("%n"), AIFN);

    }

    public static Hypothesis readAI () {
        final Hypothesis h;
        final String aiS = readString(AIFN);
        if (aiS != null){
            final String[] temp1 = aiS.split(String.format("%n"));
            // Use the last ai.
            final String[] s = temp1[temp1.length -1].split(" ");
            final String className = s[0];
            if (className.equals(LmsHypo.class.getSimpleName())) {
                final ArrayList<Double> weights = new ArrayList<Double>();
                for (int i = 1; i < s.length; i++) {
                    weights.add(Double.valueOf(s[i]));
                }
                h = new LmsHypo(weights);
            } else {
                h = null;
            }
        } else { // No ai file.
            h =  null;
        }
        return h;
    }

    public static void writeRecord (GameRecorder gr) {
        if (Environment.getExternalStorageState() != null) {
            Log.v(MODEL, "Has SDcard");

            final StringBuilder sb = new StringBuilder();
            for (Record r : gr.history) {
                sb.append(r.toString()); // One move.
                sb.append(String.format("%n"));
            }

            File path = new File(PATH);
            if (!path.exists()) {
                path.mkdirs(); // Make directory.
            }

            final String time =
                    new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)
                            .format(new java.util.Date());
            String fileName = "/" + PRE + time + EXT;
            File f = new File(path + "/" + fileName);

            int i = 1;
            while (f.exists()) { // To prevent 1 sec has 2 games.
                fileName = "/" + PRE + time + " (" + i + ")" + EXT;
                f = new File(path + "/" + fileName);
                i++;
            }

            writeString(sb.toString(), fileName);
        }
    }

    public static ArrayList<GameRecorder> readRecord () {
        final ArrayList<GameRecorder> grs = new ArrayList<GameRecorder>();

        final File mfile = new File(PATH);
        final File[] files = mfile.listFiles();
        for (File f : files) {
            final String fn = f.getName();
            if (fn.startsWith(PRE)) {
                final String[] s = readString(fn).split(String.format("%n"));
                final GameRecorder recorder = new GameRecorder();

                for (String line : s) {
                    final Record r = strToRec(line);
                    recorder.addRecord(r); // Add one move to recorder.
                }
                grs.add(recorder);
            }
        }
        return grs;
    }

    private static void writeString (String s, String fileName) {
        BufferedWriter bw = null;
        try {
            if (Environment.getExternalStorageState() != null) {
                final File path = new File(PATH);
                final File f = new File(path + "/" + fileName);
                if (!path.exists()) {
                    path.mkdirs(); // Make directory.
                }
                if (!f.exists()) {
                    f.createNewFile(); // Create file.
                }
                bw = new BufferedWriter(new FileWriter(f, true));
                bw.write(s); // Write one move to file.
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readString (String fileName) {
        String ret = null;
        BufferedReader br = null;
        try {
            final File f = new File(PATH + "/" + fileName);
            br = new BufferedReader(new FileReader(f));
            final StringBuilder sb = new StringBuilder();

            boolean isFirst = true;
            boolean endOfFile = false;
            while (!endOfFile) {
                final String temp = br.readLine();
                if (temp != null) {
                    if (isFirst) {
                        isFirst = false;
                    } else { // Only append "%n" between lines.
                        sb.append(String.format("%n"));
                    }
                    sb.append(temp);
                } else {
                    endOfFile = true;
                }
            }
            ret = sb.toString();
        } catch (FileNotFoundException e) {
            ret = null; // No such file just return null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    private static Record strToRec (final String s) {
        final String[] s2 = s.split(" ");
        assert s2.length == 8;
        final long b1 = Long.valueOf(s2[0]);
        final long b2 = Long.valueOf(s2[1]);
        final TttState state = new TttState(b1, b2);
        final int l = Integer.valueOf(s2[2]);
        final int r = Integer.valueOf(s2[3]);
        final int c = Integer.valueOf(s2[4]);
        final Position pos = new Position(l, r, c);
        final int player = Integer.valueOf(s2[5]);      // The player made this
        // action.
        final int nextPlayer = Integer.valueOf(s2[6]);  // The player about to
                                                       // make action.
        final int winner = Integer.valueOf(s2[7]);
        final Record rec = new Record(state, pos, player, nextPlayer, winner);
        return rec;
    }
}
