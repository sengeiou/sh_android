package com.shootr.mobile.ui.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import com.shootr.mobile.ui.activities.PollVoteActivity;
import com.shootr.mobile.ui.activities.ProfileActivity;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.model.BaseMessageModel;
import com.shootr.mobile.ui.model.BaseMessagePollModel;
import com.shootr.mobile.ui.model.UrlModel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseMessageTextView extends TextView {

  private static final String POLL_TYPE = "POLL";
  private static final String URL_TYPE = "URL";
  public static final String[] ALLOWED_SCHEMAS = { "http://", "https://", "rtsp://" };
  public static final String DEFAULT_SCHEMA = "http://";
  public static final int START = 0;
  private static final String USERNAME_REGEX = "@[-_A-Za-z0-9]{3,25}";

  private BaseMessageModel baseMessageModel;
  private BaseMessagePressableSpan alreadyPressedSpan;
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
      spanPollQuestions(stringBuilder);
      spanMentions(stringBuilder);
      setText(stringBuilder, BufferType.SPANNABLE);
    }
  }

  private void spanMentions(SpannableStringBuilder stringBuilder) {
    Pattern pattern = Pattern.compile(USERNAME_REGEX);
    Matcher matcher = pattern.matcher(stringBuilder);
    while (matcher.find()) {
      String username = stringBuilder.subSequence(matcher.start() + 1, matcher.end()).toString();
      BaseMessageUsernameSpan usernameClickSpan = new BaseMessageUsernameSpan(username) {
        @Override public void onUsernameClick(String username) {
          goToUserProfile(username);
        }
      };
      stringBuilder.setSpan(usernameClickSpan, matcher.start(), matcher.end(),
          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
  }

  private void spanUrls(SpannableStringBuilder stringBuilder) {
    int lastUrlindex = 0;
    if (baseMessageModel.getEntitiesModel() != null) {
      for (UrlModel urlModel : baseMessageModel.getEntitiesModel().getUrls()) {
        try {
          int start = urlModel.getIndices().get(0) + lastUrlindex;
          int end = urlModel.getIndices().get(1) + lastUrlindex;

          String textToReplace = stringBuilder.toString().substring(start, end);

          stringBuilder.setSpan(new TouchableUrlSpan(urlModel.getUrl(), onUrlClickListener),
              start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

          stringBuilder.replace(start, end, urlModel.getDisplayUrl());

          lastUrlindex += urlModel.getDisplayUrl().length() - textToReplace.length();
        } catch (IndexOutOfBoundsException error) {
          /* no-op */
        }
      }
    }
  }

  private void spanPollQuestions(SpannableStringBuilder stringBuilder) {
    if (baseMessageModel.getEntitiesModel() != null) {
      for (final BaseMessagePollModel baseMessagePollModel : baseMessageModel.getEntitiesModel()
          .getPolls()) {

        try {
          BaseMessagePollQuestionSpan pollQuestionSpan =
              new BaseMessagePollQuestionSpan(baseMessagePollModel.getIdPoll()) {
                @Override public void onPollQuestionClick(String pollQuestion) {
                  openPoll(baseMessagePollModel.getIdPoll());
                }
              };

          Pattern termsPattern =
              Pattern.compile(Pattern.quote(baseMessagePollModel.getPollQuestion()));
          Matcher termsMatcher = termsPattern.matcher(stringBuilder.toString());
          if (termsMatcher.find()) {
            int termsStart = termsMatcher.start();
            int termsEnd = termsMatcher.end();
            stringBuilder.replace(termsStart, termsEnd, baseMessagePollModel.getPollQuestion());
            stringBuilder.setSpan(pollQuestionSpan, termsStart, termsEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
          }
        } catch (IndexOutOfBoundsException error) {
          /* no-op */
        }
      }
    }
  }

  private void goToUserProfile(String username) {
    Context context = getContext();
    Intent intentForUser = ProfileActivity.getIntentWithUsername(context, username);
    context.startActivity(intentForUser);
  }

  private void openPoll(String idPoll) {
    Context context = getContext();
    Intent pollVoteIntent =
        PollVoteActivity.newIntentWithIdPoll(context, idPoll, null);
    context.startActivity(pollVoteIntent);
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
        BaseMessagePressableSpan touchedUrlSpan = getTouchedSpan(event, widget, buffer);

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

  private BaseMessagePressableSpan getTouchedSpan(MotionEvent event, TextView widget, Spannable buffer) {
    int x = (int) event.getX();
    int y = (int) event.getY();

    x -= widget.getTotalPaddingLeft();
    y -= widget.getTotalPaddingTop();

    x += widget.getScrollX();
    y += widget.getScrollY();

    Layout layout = widget.getLayout();
    int line = layout.getLineForVertical(y);
    int off = layout.getOffsetForHorizontal(line, x);

    BaseMessagePressableSpan[] pressedSpans = buffer.getSpans(off, off, BaseMessagePressableSpan.class);

    BaseMessagePressableSpan pressedSpan = null;

    if (pressedSpans.length != 0) {
      pressedSpan = pressedSpans[0];
    }
    return pressedSpan;
  }

  private class TouchableUrlSpan extends URLSpan implements BaseMessagePressableSpan {

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
}
