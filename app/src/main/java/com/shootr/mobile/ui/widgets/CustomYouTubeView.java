package com.shootr.mobile.ui.widgets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.shootr.mobile.R;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CustomYouTubeView extends WebView {

  public static final String UNSTARTED = "UNSTARTED";
  public static final String ENDED = "ENDED";
  public static final String PLAYING = "PLAYING";
  public static final String PAUSED = "PAUSED";
  public static final String BUFFERING = "BUFFERING";
  public static final String CUED = "CUED";
  public static final String NONE = "NONE";

  private YoutubeBridge bridge = new YoutubeBridge();

  private YTParams params = new YTParams();

  private YouTubeListener youTubeListener;
  private String backgroundColor = "#000000";

  private Context context;

  private boolean isHandlerEnable = true;

  public CustomYouTubeView(Context context) {
    super(context);
    this.context = context;
  }

  public CustomYouTubeView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
  }

  @SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled", "AddJavascriptInterface" })
  public void initialize(YouTubeListener youTubeListener) {
    WebSettings set = this.getSettings();

    set.setJavaScriptEnabled(true);
    set.setCacheMode(WebSettings.LOAD_NO_CACHE);

    this.youTubeListener = youTubeListener;
    this.addJavascriptInterface(bridge, "Interface");

    this.setWebChromeClient(new WebChromeClient() {
      @Override public Bitmap getDefaultVideoPoster() {
        Bitmap result = super.getDefaultVideoPoster();

        if (result == null) {
          return Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);
        } else {
          return result;
        }
      }
    });

    this.loadDataWithBaseURL("https://www.youtube.com", getVideoHTML(), "text/html", "utf-8", null);
  }

  public void initialize(YTParams params, YouTubeListener youTubeListener) {
    if (params != null) {
      this.params = params;
    }
    initialize(youTubeListener);
  }

  public void setAutoPlayerHeight(Context context) {
    DisplayMetrics displayMetrics = new DisplayMetrics();
    ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    this.getLayoutParams().height = (int) (displayMetrics.widthPixels * 0.5625);
  }

  public void setAutoPlayerHeight() {
    setAutoPlayerHeight(context);
  }

  public void seekToMillis(double mil) {
    this.loadUrl("javascript:onSeekTo(" + mil + ")");
  }

  public void pause() {
    this.loadUrl("javascript:onVideoPause()");
  }

  public void play() {
    this.loadUrl("javascript:onVideoPlay()");
  }

  public void onLoadVideo(String videoId, float mil) {
    this.loadUrl("javascript:loadVideo('" + videoId + "', " + mil + ")");
  }

  public void onCueVideo(String videoId, double starSeconds) {
    this.loadUrl("javascript:cueVideo('" + videoId + "', " + starSeconds + ")");
  }

  private class YoutubeBridge {

    private final Handler mainThreadHandler;

    private YoutubeBridge() {
      mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    @JavascriptInterface public void onReady(final String arg) {
      mainThreadHandler.post(new Runnable() {
        @Override public void run() {
          if (youTubeListener != null) {
            youTubeListener.onReady();
          }
        }
      });
    }

    @JavascriptInterface public void onStateChange(String arg) {
      if (youTubeListener != null) {
        if ("UNSTARTED".equalsIgnoreCase(arg)) {
          youTubeListener.onStateChange(UNSTARTED);
        } else if ("ENDED".equalsIgnoreCase(arg)) {
          youTubeListener.onStateChange(ENDED);
        } else if ("PLAYING".equalsIgnoreCase(arg)) {
          youTubeListener.onStateChange(PLAYING);
        } else if ("PAUSED".equalsIgnoreCase(arg)) {
          youTubeListener.onStateChange(PAUSED);
        } else if ("BUFFERING".equalsIgnoreCase(arg)) {
          youTubeListener.onStateChange(BUFFERING);
        } else if ("CUED".equalsIgnoreCase(arg)) {
          youTubeListener.onStateChange(CUED);
        }
      }
    }

    @JavascriptInterface public void onPlaybackQualityChange(final String arg) {
      mainThreadHandler.post(new Runnable() {
        @Override public void run() {
          if (youTubeListener != null) {
            youTubeListener.onPlaybackQualityChange(arg);
          }
        }
      });
    }

    @JavascriptInterface public void onPlaybackRateChange(final String arg) {
      mainThreadHandler.post(new Runnable() {
        @Override public void run() {
          if (youTubeListener != null) {
            youTubeListener.onPlaybackRateChange(arg);
          }
        }
      });
    }

    @JavascriptInterface public void onError(final String arg) {
      mainThreadHandler.post(new Runnable() {
        @Override public void run() {
          if (youTubeListener != null) {
            youTubeListener.onError(arg);
          }
        }
      });
    }

    @JavascriptInterface public void onApiChange(final String arg) {
      mainThreadHandler.post(new Runnable() {
        @Override public void run() {
          if (youTubeListener != null) {
            youTubeListener.onApiChange(arg);
          }
        }
      });
    }

    @JavascriptInterface public void currentSeconds(final String seconds) {
      mainThreadHandler.post(new Runnable() {
        @Override public void run() {
          if (youTubeListener != null) {
            // currentTime callback
            float second = Float.parseFloat(seconds);
            if (isHandlerEnable) {
              Message msg = new Message();
              msg.obj = second;
              if (youTubeListener != null) {
                youTubeListener.onCurrentSecond(second);
              }
            } else {
              youTubeListener.onCurrentSecond(second);
            }
          }
        }
      });
    }

    @JavascriptInterface public void duration(final String seconds) {
      mainThreadHandler.post(new Runnable() {
        @Override public void run() {
          if (youTubeListener != null) {
            youTubeListener.onDuration(Double.parseDouble(seconds));
          }
        }
      });
    }

    @JavascriptInterface public void logs(final String arg) {
      mainThreadHandler.post(new Runnable() {
        @Override public void run() {
          if (youTubeListener != null) {
            youTubeListener.logs(arg);
          }
        }
      });
    }
  }

  public interface YouTubeListener {
    void onReady();

    void onStateChange(String state);

    void onPlaybackQualityChange(String arg);

    void onPlaybackRateChange(String arg);

    void onError(String arg);

    void onApiChange(String arg);

    void onCurrentSecond(double second);

    void onDuration(double duration);

    void logs(String log);
  }

  private String getVideoHTML() {
    try {
      InputStream in = getResources().openRawResource(R.raw.youtube_player);
      if (in != null) {
        InputStreamReader stream = new InputStreamReader(in, "utf-8");
        BufferedReader buffer = new BufferedReader(stream);
        String read;
        StringBuilder sb = new StringBuilder("");

        while ((read = buffer.readLine()) != null) {
          sb.append(read + "\n");
        }

        in.close();

        String html = sb.toString().replace("[BG_COLOR]", backgroundColor);
        PlayBackQuality playbackQuality = params.getPlaybackQuality();
        html = html.replace("[AUTO_PLAY]", String.valueOf(params.getAutoplay()))
            .replace("[AUTO_HIDE]", String.valueOf(params.getAutohide()))
            .replace("[REL]", String.valueOf(params.getRel()))
            .replace("[SHOW_INFO]", String.valueOf(params.getShowinfo()))
            .replace("[ENABLE_JS_API]", String.valueOf(params.getEnablejsapi()))
            .replace("[DISABLE_KB]", String.valueOf(params.getDisablekb()))
            .replace("[CC_LANG_PREF]", String.valueOf(params.getgetCcLangPref()))
            .replace("[CONTROLS]", String.valueOf(params.getControls()))
            .replace("[AUDIO_VOLUME]", String.valueOf(params.getVolume()))
            .replace("[PLAYBACK_QUALITY]",
                playbackQuality == null ? String.valueOf("default") : playbackQuality.name());
        return html;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }
}
