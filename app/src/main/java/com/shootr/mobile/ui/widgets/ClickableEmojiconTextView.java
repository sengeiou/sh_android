package com.shootr.mobile.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.DynamicDrawableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.rockerhieu.emojicon.EmojiconHandler;
import com.shootr.mobile.domain.utils.Patterns;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClickableEmojiconTextView extends TextView {

  private static final String[] ALLOWED_SCHEMAS = { "http://", "https://", "rtsp://" };
  private static final String DEFAULT_SCHEMA = ALLOWED_SCHEMAS[0];
  private static final String EMPTY_STRING = "";
  private static final String PREFIX = "www.";
  private static final int URL_LENGTH = 20;
  private static final int START = 0;
  private static final String ELLIPSIS = "...";
  private Pattern urlPattern;
  private PressableSpan alreadyPressedSpan;

  private int emojiconSize;
  private int emojiconAlignment;
  private int emojiconTextSize;
  private int textStart = 0;
  private int textLength = -1;
  private boolean useSystemDefault = false;

  public ClickableEmojiconTextView(Context context) {
    super(context);
    init(null);
  }

  public ClickableEmojiconTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs);
  }

  public ClickableEmojiconTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs);
  }

  private void init(AttributeSet attrs) {
    emojiconTextSize = (int) getTextSize();
    if (attrs == null) {
      emojiconSize = (int) getTextSize();
    } else {
      TypedArray a =
          getContext().obtainStyledAttributes(attrs, com.rockerhieu.emojicon.R.styleable.Emojicon);
      emojiconSize = (int) a.getDimension(com.rockerhieu.emojicon.R.styleable.Emojicon_emojiconSize,
          getTextSize());
      emojiconAlignment = a.getInt(com.rockerhieu.emojicon.R.styleable.Emojicon_emojiconAlignment,
          DynamicDrawableSpan.ALIGN_BASELINE);
      textStart = a.getInteger(com.rockerhieu.emojicon.R.styleable.Emojicon_emojiconTextStart, 0);
      textLength =
          a.getInteger(com.rockerhieu.emojicon.R.styleable.Emojicon_emojiconTextLength, -1);
      useSystemDefault =
          a.getBoolean(com.rockerhieu.emojicon.R.styleable.Emojicon_emojiconUseSystemDefault,
              false);
      a.recycle();
    }
    setText(getText());
  }

  @Override public void setText(CharSequence text, BufferType type) {
    if (!TextUtils.isEmpty(text)) {
      SpannableStringBuilder builder = new SpannableStringBuilder(text);
      EmojiconHandler.addEmojis(getContext(), builder, emojiconSize, emojiconAlignment,
          emojiconTextSize, textStart, textLength, useSystemDefault);
      text = builder;
    }
    super.setText(text, BufferType.SPANNABLE);
  }

  /**
   * Call this method after setting the text with links.
   * Matches any link in the text and sets UrlSpans on them.
   */
  public void addLinks() {
    if (urlPattern == null) {
      urlPattern = Patterns.WEB_URL;
    }
    CharSequence text = getText();
    Matcher matcher = urlPattern.matcher(text);
    SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
    while (matcher.find()) {
      int start = matcher.start();
      int end = matcher.end();
      String url = makeUrl(matcher.group());
      stringBuilder.setSpan(new TouchableUrlSpan(url), start, end,
          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    formatSpans(stringBuilder);
    setText(stringBuilder, BufferType.SPANNABLE);
  }

  private void formatSpans(SpannableStringBuilder stringBuilder) {
    TouchableUrlSpan[] spans =
        stringBuilder.getSpans(START, stringBuilder.length(), TouchableUrlSpan.class);

    for (TouchableUrlSpan span : spans) {
      String newUrl = span.getURL();
      newUrl = formatUrlInComment(newUrl);
      stringBuilder.replace(stringBuilder.getSpanStart(span), stringBuilder.getSpanEnd(span),
          newUrl);
    }
  }

  @NonNull private String formatUrlInComment(String newUrl) {
    for (String allowedSchema : ALLOWED_SCHEMAS) {
      newUrl = newUrl.replace(allowedSchema, EMPTY_STRING);
    }
    newUrl = newUrl.replace(PREFIX, EMPTY_STRING);
    if (newUrl.length() > URL_LENGTH) {
      newUrl = newUrl.substring(START, URL_LENGTH) + ELLIPSIS;
    }
    return newUrl;
  }

  /**
   * From {@link Linkify}'s makeUrl()
   */
  private String makeUrl(String url) {
    boolean hasPrefix = false;
    String newUrl = url;
    for (String ALLOWED_SCHEMA : ALLOWED_SCHEMAS) {
      if (url.regionMatches(true, 0, ALLOWED_SCHEMA, 0, ALLOWED_SCHEMA.length())) {
        hasPrefix = true;

        // Fix capitalization if necessary
        if (!url.regionMatches(false, 0, ALLOWED_SCHEMA, 0, ALLOWED_SCHEMA.length())) {
          newUrl = ALLOWED_SCHEMA + url.substring(ALLOWED_SCHEMA.length());
        }

        break;
      }
    }

    if (!hasPrefix) {
      newUrl = DEFAULT_SCHEMA + newUrl;
    }

    return newUrl;
  }

  /**
   * Triggers ClickableSpans' onClick stream when tapped over the span, and does nothing otherwise.
   * Mirror implementation from {@link LinkMovementMethod}
   */
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

  public interface PressableSpan {

    void setPressed(boolean isPressed);

    void onClick(View widget);
  }

  private class TouchableUrlSpan extends URLSpan implements PressableSpan {

    private boolean isPressed = false;

    public TouchableUrlSpan(String url) {
      super(url);
    }

    public TouchableUrlSpan(Parcel src) {
      super(src);
    }

    @Override public void setPressed(boolean isPressed) {
      this.isPressed = isPressed;
      invalidate();
    }

    @Override public void updateDrawState(TextPaint ds) {
      super.updateDrawState(ds);
      ds.setUnderlineText(isPressed);
    }
  }
}
