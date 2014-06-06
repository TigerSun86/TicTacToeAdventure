package com.TigerSun.tictactoeadventure.util;

import android.content.Context;
import android.widget.Toast;

public class Dbg {
    public static void print (Context context, final boolean dbg, final String model,
            final String dbgInfo) {
        if (!dbg) {
            return;
        }
        Toast.makeText(context, model + ": " + dbgInfo, Toast.LENGTH_SHORT).show();
    }
}
