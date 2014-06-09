package com.TigerSun.tictactoeadventure;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;

public class MainActivity extends Activity {
    private Button startBT;
    private RadioGroup p1RG;
    private RadioGroup p2RG;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        p1RG = (RadioGroup) findViewById(R.id.xDifficultyRB);
        p2RG = (RadioGroup) findViewById(R.id.oDifficultyRB);

        startBT = (Button) findViewById(R.id.startBT);
        startBT.setOnClickListener(startBTListener);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public OnClickListener startBTListener = new OnClickListener() {
        @Override
        public void onClick (View v) {
            final Intent intent =
                    new Intent(getApplication(), GameBoardDisplay.class);
            int p1;
            if (p1RG.getCheckedRadioButtonId() == R.id.xEasy) {
                p1 = 1;
            } else if (p1RG.getCheckedRadioButtonId() == R.id.xNormal) {
                p1 = 2;
            } else if (p1RG.getCheckedRadioButtonId() == R.id.xNightmare) {
                p1 = 3;
            } else {
                p1 = 0;
            }
            int p2;
            if (p2RG.getCheckedRadioButtonId() == R.id.oEasy) {
                p2 = 1;
            } else if (p2RG.getCheckedRadioButtonId() == R.id.oNormal) {
                p2 = 2;
            } else if (p2RG.getCheckedRadioButtonId() == R.id.oNightmare) {
                p2 = 3;
            } else {
                p2 = 0;
            }

            intent.putExtra("PX", p1);
            intent.putExtra("PO", p2);
            startActivity(intent);
        }
    };
}
