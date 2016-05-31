package com.lids.barscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
    private final int SPLASH_LENGTH = 4000;
    private static boolean splashLoaded = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check is to make sure splash screen only loads once during application creation and does not
        // repeat in case user presses back button or presses home button
        if (!splashLoaded) {
            setContentView(R.layout.splash_screen);

            // Fade in and out animation for "loading" text
            final Animation fade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_out);
            final TextView textView = (TextView) findViewById(R.id.splash_load);
            textView.bringToFront();
            textView.startAnimation(fade);

            // Following code animates pages in the book through frame by frame animation
            final ImageView book_anim = (ImageView) findViewById(R.id.book_animation);
            book_anim.setImageResource(R.drawable.book_1);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    book_anim.setImageResource(R.drawable.book_2);
                }
            }, 300);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    book_anim.setImageResource(R.drawable.book_3);
                }
            }, 600);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    book_anim.setImageResource(R.drawable.book_4);
                }

            }, 900);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    book_anim.setImageResource(R.drawable.book_5);
                }
            }, 1200);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    book_anim.setImageResource(R.drawable.book_6);
                }
            }, 1500);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    book_anim.setImageResource(R.drawable.book_7);
                }
            }, 1800);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    book_anim.setImageResource(R.drawable.book_8);
                }
            }, 2100);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    book_anim.setImageResource(R.drawable.book_1);
                }

            }, 2400);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    book_anim.setImageResource(R.drawable.book_2);
                }

            }, 2700);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    book_anim.setImageResource(R.drawable.book_3);
                }

            }, 3000);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    book_anim.setImageResource(R.drawable.book_4);
                }
            }, 3300);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    book_anim.setImageResource(R.drawable.book_5);
                }
            }, 3600);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    book_anim.setImageResource(R.drawable.book_6);
                }
            }, 3900);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    book_anim.setImageResource(R.drawable.book_7);
                }
            }, 4200);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    book_anim.setImageResource(R.drawable.book_8);
                }
            }, 4500);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    book_anim.setImageResource(R.drawable.book_1);
                }
            }, 4800);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    book_anim.setImageResource(R.drawable.book_2);
                }
            }, 5100);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    book_anim.setImageResource(R.drawable.book_3);
                }
            }, 5400);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    book_anim.setImageResource(R.drawable.book_4);
                }

            }, 5700);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    book_anim.setImageResource(R.drawable.book_5);
                    // Start activity
                    startActivity(new Intent(SplashScreen.this, LoginScreen.class));
                }
            }, 6000);

            splashLoaded = true;

        // If splash screen has already been shown, directly go to main activity
        } else {
            Intent goToMainActivity = new Intent(SplashScreen.this, LoginScreen.class);
            goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(goToMainActivity);
            finish();
        }
    }
}
