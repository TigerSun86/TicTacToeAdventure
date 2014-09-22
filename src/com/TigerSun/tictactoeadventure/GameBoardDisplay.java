package com.TigerSun.tictactoeadventure;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;

public class GameBoardDisplay extends Activity {
    private MySurfaceView sfv;
    private Button okBT;

    private Handler handler;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_board_display);

        sfv = (MySurfaceView) this.findViewById(R.id.surface_view);

        final Button okBT = (Button) findViewById(R.id.okButton);
        okBT.setOnClickListener(sfv.okBTListener);
        okBT.setEnabled(false); // At the beginning unclickable;

        final TextView tv = (TextView) findViewById(R.id.TipsTV);

        handler = new Handler() {
            public void handleMessage (Message msg) {
                if (msg.what == MySurfaceView.MSG_BT) {
                    okBT.setEnabled((Boolean) msg.obj);
                } else if (msg.what == MySurfaceView.MSG_TV) {
                    tv.setText((String) msg.obj);
                }

                super.handleMessage(msg);
            }
        };
        sfv.handler = this.handler;

        Intent intent = this.getIntent();
        int p1 = intent.getIntExtra("PX", 0);
        int p2 = intent.getIntExtra("PO", 0);
        int d1 = intent.getIntExtra("XDEPTH", 0);
        int d2 = intent.getIntExtra("ODEPTH", 0);
        sfv.setPlayer(p1, p2, d1, d2);

    }

    public Handler getHandler () {
        return this.handler;
    }
}
