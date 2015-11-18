package com.shootr.mobile.util;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

public class WritePermissionManager {

    public static final int WRITE_PERMISSION_REQUEST = 1;
    private Activity activity;

    public void init(Activity activity) {
        this.activity = activity;
    }

    public boolean hasWritePermission() {
        return ContextCompat.checkSelfPermission(activity,
          Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestWritePermissionToUser() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
          Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(activity).setMessage(com.shootr.mobile.R.string.download_photo_permission_explaination)
              .setPositiveButton(com.shootr.mobile.R.string.ok, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                      requestWritePermission();
                  }
              })
              .setNegativeButton(com.shootr.mobile.R.string.cancel, null)
              .show();
        } else {
            requestWritePermission();
        }
    }

    protected void requestWritePermission() {
        ActivityCompat.requestPermissions(activity,
          new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
          WRITE_PERMISSION_REQUEST);
    }

    public int getWritePermissionRequest() {
        return WRITE_PERMISSION_REQUEST;
    }

}
