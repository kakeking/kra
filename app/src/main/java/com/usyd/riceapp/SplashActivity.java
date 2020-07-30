package com.usyd.riceapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


public class SplashActivity extends AppCompatActivity {
    private ImageView logo, logoText;
    private static int splashTimeOut=3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitleBar();
        setContentView(R.layout.activity_splash);


        logo = findViewById(R.id.logo_img);
        logoText = findViewById(R.id.logo_text);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, splashTimeOut);

        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.splashanim);
        logo.startAnimation(myanim);
        logoText.startAnimation(myanim);
    }

    public void hideTitleBar(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

}
