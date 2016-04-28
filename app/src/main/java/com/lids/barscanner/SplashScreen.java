package com.lids.barscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {
    private final int SPLASH_LENGTH = 1000;
    private static boolean splashLoaded = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!splashLoaded) {
            setContentView(R.layout.splash_screen);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(SplashScreen.this, LoginScreen.class));
                    finish();
                }
            }, SPLASH_LENGTH);

            splashLoaded = true;
        } else {
            Intent goToMainActivity = new Intent(SplashScreen.this, LoginScreen.class);
            goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(goToMainActivity);
            finish();
        }
    }
}
