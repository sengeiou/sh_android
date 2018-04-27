package com.shootr.mobile.ui;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import com.shootr.mobile.R;
import com.shootr.mobile.ShootrApplication;
import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.bus.FloatingPlayerState;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.activities.StreamTimelineActivity;
import com.shootr.mobile.ui.widgets.CustomYouTubeView;
import com.shootr.mobile.ui.widgets.PlayBackQuality;
import com.shootr.mobile.ui.widgets.YTParams;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class FloatingVideoService extends Service implements FloatingPlayerState.Receiver {

  private static final String API = "AIzaSyAamKWr6yMmLmhSsLvWA1cKOBYXPytC6_I";

  private static final String VIDEO_ID = "VIDEO_ID";
  private static final String CURRENT_SECOND = "CURRENT_SECOND";
  private static final String STREAM_ID = "STREAM_ID";
  private static boolean isRunning = false;

  @Inject @Main Bus bus;
  @Inject SessionRepository sessionRepository;

  private WindowManager windowManager;
  private View floatingView;
  private View removeView;

  private static Intent serviceIntent;
  private CustomYouTubeView youTubePlayerView;

  private boolean isVideoCued;
  private String currentStream;

  public FloatingVideoService() {
  }

  @Override public void onCreate() {
    super.onCreate();
    ShootrApplication.get(getApplicationContext()).inject(this);
    bus.register(this);
  }

  public static void startService(Context context) {
    if (!isRunning) {
      isRunning = true;
      serviceIntent = new Intent(context, FloatingVideoService.class);
      context.startService(serviceIntent);
      Log.d("VIDEO", "inticio servicio de video");
    } else {
      Log.d("VIDEO", "intento de abrir servicio de video que está running");
    }
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }
  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    boolean serviceStartedExplicitly = intent != null;
    if (serviceStartedExplicitly) {
      setup();
    }
    return START_STICKY;
  }

  private void setup() {

    windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

    floatingView = LayoutInflater.from(this).inflate(R.layout.floating_video, null);
    floatingView.setVisibility(View.GONE);

    final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_PHONE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT);

    params.gravity = Gravity.TOP | Gravity.LEFT;
    params.x = 0;
    params.y = 100;

    initWebView(floatingView);
    removeView = floatingView.findViewById(R.id.close);
    implementTouchListenerToFloatingWidgetView(params);
    windowManager.addView(floatingView, params);

  }

  private void initWebView(View view) {

    removeView = view.findViewById(R.id.close);
    removeView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        stopSelf();
      }
    });

    youTubePlayerView = (CustomYouTubeView) view.findViewById(R.id.player);

    YTParams params = new YTParams();
    params.setControls(0);
    params.setPlaybackQuality(PlayBackQuality.small);

    youTubePlayerView.initialize(params, new CustomYouTubeView.YouTubeListener() {
      @Override public void onReady() {
        /* no-op */
      }

      @Override public void onStateChange(String state) {
        if (state.equals(CustomYouTubeView.ENDED)) {
          isRunning = false;
          stopSelf();
        }
      }

      @Override public void onPlaybackQualityChange(String arg) {

      }

      @Override public void onPlaybackRateChange(String arg) {

      }

      @Override public void onError(String arg) {

      }

      @Override public void onApiChange(String arg) {

      }

      @Override public void onCurrentSecond(double second) {

      }

      @Override public void onDuration(double duration) {

      }

      @Override public void logs(String log) {
        Log.d("youtube", log);
      }
    });
  }

  @Override public void onDestroy() {
    super.onDestroy();
    isRunning = false;
    bus.unregister(this);
    if (floatingView != null) {
      windowManager.removeView(floatingView);
    }
  }

  @Override public void onTaskRemoved(Intent rootIntent) {
    super.onTaskRemoved(rootIntent);
    isRunning = false;
    stopSelf();
  }

  private void implementTouchListenerToFloatingWidgetView(final WindowManager.LayoutParams params) {

    floatingView.findViewById(R.id.mask).setOnTouchListener(new View.OnTouchListener() {
      private int initialX;
      private int initialY;
      private float initialTouchX;
      private float initialTouchY;


      @Override
      public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:

            //remember the initial position.
            initialX = params.x;
            initialY = params.y;

            //get the touch location
            initialTouchX = event.getRawX();
            initialTouchY = event.getRawY();
            return true;
          case MotionEvent.ACTION_UP:
            int Xdiff = (int) (event.getRawX() - initialTouchX);
            int Ydiff = (int) (event.getRawY() - initialTouchY);


            //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
            //So that is click event.
            if (Xdiff < 10 && Ydiff < 10) {
              Intent intent = StreamTimelineActivity.newIntent(getBaseContext(), currentStream);
              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              getBaseContext().startActivity(intent);
              floatingView.setVisibility(View.GONE);
              youTubePlayerView.pause();
            }
            return true;
          case MotionEvent.ACTION_MOVE:
            //Calculate the X and Y coordinates of the view.
            params.x = initialX + (int) (event.getRawX() - initialTouchX);
            params.y = initialY + (int) (event.getRawY() - initialTouchY);


            //Update the layout with new X & Y coordinate
            windowManager.updateViewLayout(floatingView, params);
            return true;
          default:
            break;
        }
        return false;
      }
    });
  }

  @Subscribe @Override public void onPlayerStateChanged(FloatingPlayerState.Event event) {
    switch (event.getState()) {
      case FloatingPlayerState.PLAY_PRESSED:
        cueVideoOnPlayIfPossible(event);
        break;
      case FloatingPlayerState.ACTIVITY_PAUSED:
        showFloatingView(event);
        break;
      case FloatingPlayerState.IN_APP_VIDEO:
        stopSelf();
        break;
        default:
          break;
    }
  }

  private void showFloatingView(FloatingPlayerState.Event event) {
    double seconds = TimeUnit.MILLISECONDS.toSeconds((long) event.getCurrentSecond());
    currentStream = event.getStreamId();
    if (isVideoCued) {
      youTubePlayerView.seekToMillis(seconds);
      youTubePlayerView.play();
    } else {
      youTubePlayerView.onCueVideo(event.getVideoId(), seconds);
    }

    floatingView.setVisibility(View.VISIBLE);
  }

  private void cueVideoOnPlayIfPossible(FloatingPlayerState.Event event) {
    floatingView.setVisibility(View.GONE);
    isVideoCued = false;
    if (isWifiConnection()) {
      youTubePlayerView.onCueVideo(event.getVideoId(), 0);
      isVideoCued = true;
    }
  }

  private boolean isWifiConnection() {
    return true;
  }
}