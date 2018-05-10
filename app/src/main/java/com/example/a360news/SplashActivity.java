package com.example.a360news;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGHT = 3000; // 延迟六秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(SplashActivity.this,
                        MainActivity.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }

        }, SPLASH_DISPLAY_LENGHT);

    }
}