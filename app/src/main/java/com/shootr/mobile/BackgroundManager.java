package com.shootr.mobile;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class BackgroundManager implements Application.ActivityLifecycleCallbacks {

  private static final String LOG = "SOCKET";

  public static final long BACKGROUND_DELAY = 30000;

  private static BackgroundManager sInstance;

  public interface Listener {
    void onBecameForeground();

    void onBecameBackground();

    void onStartActivity();
  }

  private boolean inBackground = true;
  private final List<Listener> listeners = new ArrayList<Listener>();
  private final Handler backgroundDelayHandler = new Handler();
  private Runnable backgroundTransition;

  public static BackgroundManager get(Application application) {
    if (sInstance == null) {
      sInstance = new BackgroundManager(application);
    }
    return sInstance;
  }

  private BackgroundManager(Application application) {
    application.registerActivityLifecycleCallbacks(this);
  }

  public void registerListener(Listener listener) {
    listeners.add(listener);
  }

  public void unregisterListener(Listener listener) {
    listeners.remove(listener);
  }

  public boolean isInBackground() {
    return inBackground;
  }

  @Override public void onActivityResumed(Activity activity) {
    if (backgroundTransition != null) {
      backgroundDelayHandler.removeCallbacks(backgroundTransition);
      backgroundTransition = null;
    }

    if (inBackground) {
      inBackground = false;
      notifyOnBecameForeground();
      Log.i(LOG, "Vuelvo de background");
    }
  }

  private void notifyOnBecameForeground() {
    for (Listener listener : listeners) {
      try {
        listener.onBecameForeground();
      } catch (Exception e) {
        Log.e(LOG, "Listener threw exception!" + e);
      }
    }
  }

  @Override public void onActivityPaused(Activity activity) {
    if (!inBackground && backgroundTransition == null) {
      backgroundTransition = new Runnable() {
        @Override public void run() {
          inBackground = true;
          backgroundTransition = null;
          notifyOnBecameBackground();
          Log.i(LOG, "Voy a background!");
        }
      };
      backgroundDelayHandler.postDelayed(backgroundTransition, BACKGROUND_DELAY);
    }
  }

  private void notifyOnBecameBackground() {
    for (Listener listener : listeners) {
      try {
        listener.onBecameBackground();
      } catch (Exception e) {
        Log.e(LOG, "Listener threw exception!" + e);
      }
    }
  }

  @Override public void onActivityStopped(Activity activity) {
  }

  @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
  }

  @Override public void onActivityStarted(Activity activity) {
    for (Listener listener : listeners) {
      try {
        listener.onStartActivity();
      } catch (Exception e) {
        Log.e(LOG, "Listener threw exception!" + e);
      }
    }
  }

  @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
  }

  @Override public void onActivityDestroyed(Activity activity) {
  }
}
