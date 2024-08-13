package com.example.mealmate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.animation.ObjectAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 4000; // Duration of splash screen in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Delay starting the main activity to allow the animation to complete
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}