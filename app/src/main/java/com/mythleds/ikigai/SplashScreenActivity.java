package com.mythleds.ikigai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.urushi.prizeleds.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {

    long delay = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);



        Timer runtimer = new Timer();
        TimerTask showTimer = new TimerTask() {
            @Override
            public void run() {
                finish();
                startActivity(new Intent(SplashScreenActivity.this,BluetoothScanActivity.class));
            }
        };

        runtimer.schedule(showTimer,delay);
    }
}