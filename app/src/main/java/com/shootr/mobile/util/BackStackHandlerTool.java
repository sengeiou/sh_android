package com.shootr.mobile.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import com.shootr.mobile.ui.activities.MainTabbedActivity;
import java.util.List;
import javax.inject.Inject;

public class BackStackHandlerTool implements BackStackHandler {

    @Inject public BackStackHandlerTool() {
    }

    @Override public void handleBackStack(Activity activity) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = manager.getRunningTasks(10);
        if (taskList.get(0).numActivities == 1 && taskList.get(0).topActivity.getClassName()
          .equals(activity.getClass().getName())) {
            Intent intent = new Intent(activity, MainTabbedActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } else {
            activity.finish();
        }
    }
}
