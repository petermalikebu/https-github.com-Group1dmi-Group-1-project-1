package com.example.dmitimetable;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView splashText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize splash text view
        splashText = findViewById(R.id.splashText);
        if (splashText == null) {
            throw new RuntimeException("Unable to find TextView with ID splashText");
        }

        // Set up animations
        setUpAnimations();

        // Redirect to LoginActivity after 2 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Ensure we finish the splash activity
        }, 2000);
    }

    private void setUpAnimations() {
        // Load animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in); // Add zoom-in for logo
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        // Combine text animations
        AnimationSet textAnimationSet = new AnimationSet(true);
        textAnimationSet.addAnimation(fadeIn);
        textAnimationSet.addAnimation(slideUp);
        textAnimationSet.setDuration(1000);

        // Set up logo animation
        ImageView imageView = findViewById(R.id.imageView);
        if (imageView != null) {
            imageView.startAnimation(zoomIn); // Start zoom-in animation for the logo
        }

        // Start text animation
        splashText.startAnimation(textAnimationSet);
    }
}
