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
import com.shootr.mobile.ui.activities.StreamTimelineActivity;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.model.BaseMessagePollModel;
import com.shootr.mobile.ui.model.EntityContainable;
import com.shootr.mobile.ui.model.MentionModel;
import com.shootr.mobile.ui.model.StreamIndexModel;
import com.shootr.mobile.ui.model.UrlModel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseMessageTextView extends TextView {

  public static final String[] ALLOWED_SCHEMAS = { "http://", "https://", "rtsp://" };
  public static final String DEFAULT_SCHEMA = "http://";
  private static final String USERNAME_REGEX = "@[-_A-Za-z0-9]{3,25}";

  private EntityContainable baseMessageModel;
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
  public BaseMessageTextView(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
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

  public void setBaseMessageModel(EntityContainable baseMessageModel) {
    this.baseMessageModel = baseMessageModel;
  }

  public void addLinks() {
    if (baseMessageModel != null) {
      CharSequence text = getText();
      SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
      spanUrls(stringBuilder);
      spanPollQuestions(stringBuilder);
      spanMentions(stringBuilder);
      spanStreams(stringBuilder);
      setText(stringBuilder, BufferType.SPANNABLE);
    }
  }

  private void spanStreams(SpannableStringBuilder stringBuilder) {
    if (baseMessageModel.getEntitiesModel() != null) {
      for (final StreamIndexModel streamIndexModel : baseMessageModel.getEntitiesModel()
          .getStreams()) {

        try {
          if (streamIndexModel.getStreamTitle() != null) {
            StreamTitleSpan streamTitleSpan = new StreamTitleSpan(streamIndexModel.getIdStream(),
                streamIndexModel.getStreamTitle(), "") {
              @Override
              public void onStreamClick(String streamId, String streamTitle, String idAuthor) {
                openStream(streamId, streamTitle);
              }
            };

            Pattern termsPattern =
                Pattern.compile(Pattern.quote(streamIndexModel.getStreamTitle()));
            Matcher termsMatcher = termsPattern.matcher(stringBuilder.toString());
            if (termsMatcher.find()) {
              int termsStart = termsMatcher.start();
              int termsEnd = termsMatcher.end();
              stringBuilder.replace(termsStart, termsEnd, streamIndexModel.getStreamTitle());
              stringBuilder.setSpan(streamTitleSpan, termsStart, termsEnd,
                  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
          }
        } catch (IndexOutOfBoundsException error) {
          /* no-op */
        }
      }
    }
  }

  private void spanMentions(SpannableStringBuilder stringBuilder) {
    int lastUrlindex = 0;
    if (baseMessageModel.getEntitiesModel() != null
        && baseMessageModel.getEntitiesModel().getMentions() != null) {
      for (MentionModel mentionModel : baseMessageModel.getEntitiesModel().getMentions()) {
        try {
          int start = mentionModel.getIndices().get(0) + lastUrlindex;
          int end = mentionModel.getIndices().get(1) + lastUrlindex;

          String textToReplace = stringBuilder.toString().substring(start, end);

          BaseMessageUsernameSpan usernameClickSpan =
              new BaseMessageUsernameSpan(mentionModel.getIdUser()) {
                @Override public void onUsernameClick(String idUser) {
                  goToUserProfile(idUser);
                }
              };

          stringBuilder.setSpan(usernameClickSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

          stringBuilder.replace(start, end, mentionModel.getUsername());

          lastUrlindex += mentionModel.getUsername().length() - textToReplace.length();
        } catch (IndexOutOfBoundsException error) {
          /* no-op */
        }
      }
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

          stringBuilder.setSpan(new TouchableUrlSpan(urlModel.getUrl(), onUrlClickListener), start,
              end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

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

  private void goToUserProfile(String idUser) {
    Context context = getContext();
    Intent intentForUser = ProfileActivity.getIntent(context, idUser);
    context.startActivity(intentForUser);
  }

  private void openPoll(String idPoll) {
    Context context = getContext();
    Intent pollVoteIntent = PollVoteActivity.newIntentWithIdPoll(context, idPoll, null);
    context.startActivity(pollVoteIntent);
  }

  private void openStream(String idStream, String streamTitle) {
    Context context = getContext();
    Intent streamIntent = StreamTimelineActivity.newIntent(context, idStream, streamTitle);
    context.startActivity(streamIntent);
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

  private BaseMessagePressableSpan getTouchedSpan(MotionEvent event, TextView widget,
      Spannable buffer) {
    int x = (int) event.getX();
    int y = (int) event.getY();

    x -= widget.getTotalPaddingLeft();
    y -= widget.getTotalPaddingTop();

    x += widget.getScrollX();
    y += widget.getScrollY();

    Layout layout = widget.getLayout();
    int line = layout.getLineForVertical(y);
    int off = layout.getOffsetForHorizontal(line, x);

    BaseMessagePressableSpan[] pressedSpans =
        buffer.getSpans(off, off, BaseMessagePressableSpan.class);

    BaseMessagePressableSpan pressedSpan = null;

    if (pressedSpans.length != 0) {
      pressedSpan = pressedSpans[0];
    }
    return pressedSpan;
  }

  private class TouchableUrlSpan extends URLSpan implements BaseMessagePressableSpan {

    private OnUrlClickListener onUrlClickListener;
    private boolean isPressed = false;

    TouchableUrlSpan(String url) {
      super(url);
    }

    TouchableUrlSpan(String url, OnUrlClickListener onUrlClickListener) {
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
