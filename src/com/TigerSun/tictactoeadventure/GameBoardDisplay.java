package com.TigerSun.tictactoeadventure;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GameBoardDisplay extends Activity {
    private static final String MODEL = "GBD";
    private static final boolean DBG = true;
        
    private MySurfaceView sfv;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_board_display);

        final View controlsView =
                findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.surface_view);

        sfv = (MySurfaceView) this.findViewById(R.id.surface_view);
        Intent intent = this.getIntent();
        int px = intent.getIntExtra("PX", 0);
        int po = intent.getIntExtra("PO", 0);
        int p1 = 0;
        int p2 = 0;
        sfv.setPlayer(p1, p2);
        sfv.setAct(this);
    }
}
