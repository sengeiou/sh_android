package com.shootr.mobile.ui.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.model.BaseMessageModel;
import com.shootr.mobile.ui.model.UrlModel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseMessageTextView extends TextView {

  private static final String[] ALLOWED_SCHEMAS = { "http://", "https://", "rtsp://" };
  private static final String DEFAULT_SCHEMA = "http://";
  public static final int START = 0;

  private BaseMessageModel baseMessageModel;
  private PressableSpan alreadyPressedSpan;
  private OnUrlClickListener onUrlClickListener;

  public BaseMessageTextView(Context context) {
    super(context);
  }

  public BaseMessageTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public BaseMessageTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public BaseMessageTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override public void setText(CharSequence text, BufferType type) {
    // Force spannable text
    if (text != null) {
      super.setText(text, BufferType.SPANNABLE);
    }
  }

  public void setOnUrlClickListener(OnUrlClickListener onUrlClickListener) {
    this.onUrlClickListener = onUrlClickListener;
  }

  public void setBaseMessageModel(BaseMessageModel baseMessageModel) {
    this.baseMessageModel = baseMessageModel;
  }

  public void addLinks() {
    if (baseMessageModel != null) {
      CharSequence text = getText();
      SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
      spanUrls(stringBuilder);
      setText(stringBuilder, BufferType.SPANNABLE);
    }
  }

  private void spanUrls(SpannableStringBuilder stringBuilder) {
    if (baseMessageModel.getEntitiesModel() != null) {
      for (UrlModel urlModel : baseMessageModel.getEntitiesModel().getUrls()) {
        int start = urlModel.getIndices().get(0);
        int end = urlModel.getIndices().get(1);
        stringBuilder.replace(start, end, urlModel.getDisplayUrl());
        Pattern termsPattern = Pattern.compile(urlModel.getDisplayUrl());
        Matcher termsMatcher = termsPattern.matcher(stringBuilder.toString());
        if (termsMatcher.find()) {
          int termsStart = termsMatcher.start();
          int termsEnd = termsMatcher.end();
          stringBuilder.setSpan(new TouchableUrlSpan(urlModel.getUrl(), onUrlClickListener),
              termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
      }
    }
  }

  @Override public boolean onTouchEvent(@NonNull MotionEvent event) {
    TextView widget = this;
    Object text = widget.getText();
    if (text instanceof Spannable) {
      Spannable buffer = (Spannable) text;

      int action = event.getAction();

      if (action == MotionEvent.ACTION_CANCEL) {
        if (alreadyPressedSpan != null) {
          alreadyPressedSpan.setPressed(false);
        }
        alreadyPressedSpan = null;
        Selection.removeSelection(buffer);
      }

      if (action == MotionEvent.ACTION_UP
          || action == MotionEvent.ACTION_DOWN
          || action == MotionEvent.ACTION_MOVE) {
        PressableSpan touchedUrlSpan = getTouchedSpan(event, widget, buffer);

        if (action == MotionEvent.ACTION_MOVE
            && alreadyPressedSpan != null
            && touchedUrlSpan != alreadyPressedSpan) {
          alreadyPressedSpan.setPressed(false);
          alreadyPressedSpan = null;
          Selection.removeSelection(buffer);
          return false;
        }

        if (touchedUrlSpan != null) {
          if (action == MotionEvent.ACTION_DOWN) {
            Selection.setSelection(buffer, buffer.getSpanStart(touchedUrlSpan),
                buffer.getSpanEnd(touchedUrlSpan));
            alreadyPressedSpan = touchedUrlSpan;
            alreadyPressedSpan.setPressed(true);
          } else if (action == MotionEvent.ACTION_UP) {
            if (alreadyPressedSpan != null && alreadyPressedSpan == touchedUrlSpan) {
              alreadyPressedSpan.onClick(widget);
              alreadyPressedSpan.setPressed(false);
            }
            alreadyPressedSpan = null;
            Selection.removeSelection(buffer);
          }
          return true;
        } else {
          Selection.removeSelection(buffer);
        }
      }
    }
    return false;
  }

  private PressableSpan getTouchedSpan(MotionEvent event, TextView widget, Spannable buffer) {
    int x = (int) event.getX();
    int y = (int) event.getY();

    x -= widget.getTotalPaddingLeft();
    y -= widget.getTotalPaddingTop();

    x += widget.getScrollX();
    y += widget.getScrollY();

    Layout layout = widget.getLayout();
    int line = layout.getLineForVertical(y);
    int off = layout.getOffsetForHorizontal(line, x);

    PressableSpan[] pressedSpans = buffer.getSpans(off, off, PressableSpan.class);

    PressableSpan pressedSpan = null;

    if (pressedSpans.length != 0) {
      pressedSpan = pressedSpans[0];
    }
    return pressedSpan;
  }

  private class TouchableUrlSpan extends URLSpan implements PressableSpan {

    private OnUrlClickListener onUrlClickListener;
    private boolean isPressed = false;

    public TouchableUrlSpan(String url) {
      super(url);
    }

    public TouchableUrlSpan(String url, OnUrlClickListener onUrlClickListener) {
      super(url);
      this.onUrlClickListener = onUrlClickListener;
    }

    @Override public void setPressed(boolean isPressed) {
      this.isPressed = isPressed;
      invalidate();
      if (onUrlClickListener != null && isPressed) {
        onUrlClickListener.onClick();
      }
    }

    @Override public void updateDrawState(TextPaint ds) {
      super.updateDrawState(ds);
      ds.setUnderlineText(isPressed);
    }

    @Override public String getURL() {
      String url = super.getURL();
      if (!url.toLowerCase().startsWith(ALLOWED_SCHEMAS[0]) && !url.toLowerCase()
          .startsWith(ALLOWED_SCHEMAS[1]) && !url.toLowerCase().startsWith(ALLOWED_SCHEMAS[2])) {
        url = DEFAULT_SCHEMA + url;
      }

      return url;
    }
  }

  public interface PressableSpan {

    void setPressed(boolean isPressed);

    void onClick(View widget);
  }


}
