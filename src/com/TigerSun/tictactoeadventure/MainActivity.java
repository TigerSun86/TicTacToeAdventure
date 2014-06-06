package com.TigerSun.tictactoeadventure;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
    private Button startBT;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startBT =(Button) findViewById(R.id.startBT);
        startBT.setOnClickListener(startBTListener);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public OnClickListener startBTListener = new OnClickListener(){
        @Override
        public void onClick(View v) {
            final Intent intent = new Intent(getApplication(), GameBoardDisplay.class);
            intent.putExtra("PX", 0);
            intent.putExtra("PO", 0);
            startActivity(intent);
        }
    };
}
