package com.shootr.mobile.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import com.shootr.mobile.R;

public class SplashScreenActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash_screen);

    new CountDownTimer(1500, 1000) {

      @Override public void onTick(long millisUntilFinished) {
        /* no-op */
      }

      @Override public void onFinish() {
        goToStreamList();
      }
    }.start();
  }

  private void goToStreamList() {
    Intent i = new Intent(this, MainTabbedActivity.class);
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(i);
    finish();
  }
}
