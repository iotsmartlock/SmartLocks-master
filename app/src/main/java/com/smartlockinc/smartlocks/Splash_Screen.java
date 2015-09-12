package com.smartlockinc.smartlocks;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;

public class Splash_Screen  extends Activity implements Animation.AnimationListener {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    Animation animFadein;
    TextView txtview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fadein);
        animFadein.setAnimationListener(this);
        txtview = (TextView) findViewById(R.id.smartlock);
        txtview.startAnimation(animFadein);

        new Handler().postDelayed(new Runnable() {



            @Override
            public void run() {
                Intent i = new Intent(Splash_Screen.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onAnimationEnd(Animation animation) {


    }
    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub

    }


}