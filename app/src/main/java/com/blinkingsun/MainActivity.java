package com.blinkingsun;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.library.views.SunRiseView;

/**
 * Created by StanlyMoses on 5/11/16.
 */

public class MainActivity extends AppCompatActivity {

    private Button play, stop;
    private SunRiseView sunRiseView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sunRiseView = (SunRiseView) findViewById(R.id.sunView);

        play = (Button) findViewById(R.id.blink);
        stop = (Button) findViewById(R.id.reset);

        play.setOnClickListener(view -> sunRiseView.startBlinking());
        stop.setOnClickListener(view -> sunRiseView.stopBlinking());
    }
}