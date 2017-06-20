package com.shootr.mobile.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.shootr.mobile.R;

public class SetupNotificationsActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setup_notifications);
    Button button = (Button) findViewById(R.id.button);

    button.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        goToNotifications();
      }
    });

  }

  private void goToNotifications() {

    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Intent intent = new Intent();
      intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
      //for Android 5-7
      intent.putExtra("app_package", getPackageName());
      intent.putExtra("app_uid", getApplicationInfo().uid);
      // for Android O
      intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());

      startActivity(intent);
    } else {
      startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
    }

  }
}
