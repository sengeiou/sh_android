package com.shootr.mobile.ui.widgets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.LruCache;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
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

import static com.shootr.mobile.ShootrApplication.SCREEN_SIZE;

/**
 * Created by miniserver on 28/9/17.
 */

public class CustomBaseMessageTextView extends View {

  public static final String[] ALLOWED_SCHEMAS = { "http://", "https://", "rtsp://" };
  public static final String DEFAULT_SCHEMA = "http://";
  private static final String USERNAME_REGEX = "@[-_A-Za-z0-9]{3,25}";

  private EntityContainable baseMessageModel;
  private BaseMessagePressableSpan alreadyPressedSpan;
  private OnUrlClickListener onUrlClickListener;

  String text = "This is some text.";
  CharSequence currenText;
  static TextPaint textPaint;
  StaticLayout staticLayout;

  {

    int avatarImageSize = (int) dp(36);
    int avatarImageMargin = (int) dp(8);
    int niceContainerSize = (int) dp(49);
    int conatinerPadding = (int) dp(32);

    int textXOffset = avatarImageMargin + avatarImageSize + niceContainerSize + conatinerPadding;

    LayoutCache.INSTANCE.changeWidth(SCREEN_SIZE.x - textXOffset);
  }

  public CustomBaseMessageTextView(Context context) {
    super(context);
    initLabelView();
  }

  public CustomBaseMessageTextView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initLabelView();
  }

  public CustomBaseMessageTextView(Context context, @Nullable AttributeSet attrs,
      int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initLabelView();
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public CustomBaseMessageTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initLabelView();
  }

  public void initLabelView() {
    if (textPaint == null) {
      textPaint = new TextPaint();
      textPaint.setAntiAlias(true);
      textPaint.setTextSize(sp(14));
      textPaint.linkColor = Color.parseColor("#478ceb");
    }
  }

  public void setText(CharSequence text) {
    currenText = text;
    staticLayout = LayoutCache.INSTANCE.commentLayoutFor(text);
    requestLayout();
    invalidate();
  }

  public CharSequence getCurrenText() {
    return currenText;
  }

  public StaticLayout getStaticLayout() {
    return staticLayout;
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width = MeasureSpec.getSize(widthMeasureSpec);
    int height;
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int heightRequirement = MeasureSpec.getSize(heightMeasureSpec);
    if (heightMode == MeasureSpec.EXACTLY) {
      height = heightRequirement;
    } else {
      height = staticLayout.getHeight() + getPaddingTop() + getPaddingBottom();
      if (heightMode == MeasureSpec.AT_MOST) {
        height = Math.min(height, heightRequirement);
      }
    }
    setMeasuredDimension(width, height);
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.save();
    canvas.translate(getPaddingLeft(), getPaddingTop());
    staticLayout.draw(canvas);
    canvas.restore();
  }

  private float sp(float sp) {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
        getResources().getDisplayMetrics());
  }

  private float dp(float dp) {
    float scale = getResources().getDisplayMetrics().density;
    return (int) (dp * scale + 0.5f);
  }

  public void setOnUrlClickListener(OnUrlClickListener onUrlClickListener) {
    this.onUrlClickListener = onUrlClickListener;
  }

  public void setBaseMessageModel(EntityContainable baseMessageModel) {
    this.baseMessageModel = baseMessageModel;
  }

  public void addLinks() {
    if (baseMessageModel != null) {
      CharSequence text = getCurrenText();
      SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
      spanUrls(stringBuilder);
      spanPollQuestions(stringBuilder);
      spanMentions(stringBuilder);
      spanStreams(stringBuilder);
      setText(stringBuilder);
    }
  }

  public void addLinksWithText(CharSequence text) {
    if (baseMessageModel != null) {
      SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
      spanUrls(stringBuilder);
      spanPollQuestions(stringBuilder);
      spanMentions(stringBuilder);
      spanStreams(stringBuilder);
      setText(stringBuilder);
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
    if (baseMessageModel.getEntitiesModel() != null) {
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
      if (baseMessageModel.getEntitiesModel().getUrls() != null) {
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
    CustomBaseMessageTextView widget = this;
    Object text = widget.getCurrenText();
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

  private BaseMessagePressableSpan getTouchedSpan(MotionEvent event,
      CustomBaseMessageTextView widget, Spannable buffer) {
    int x = (int) event.getX();
    int y = (int) event.getY();

    x -= widget.getPaddingLeft();
    y -= widget.getPaddingTop();

    x += widget.getScrollX();
    y += widget.getScrollY();

    Layout layout = widget.getStaticLayout();
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

  private enum LayoutCache {
    INSTANCE;

    private int width;
    private final LruCache<CharSequence, StaticLayout> commentCache =
        new LruCache<CharSequence, StaticLayout>(100) {
          @Override protected StaticLayout create(CharSequence key) {
            return new StaticLayout(key, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1, 1,
                true);
          }
        };

    public void changeWidth(int newWidth) {
      if (width != newWidth) {
        width = newWidth;
        commentCache.evictAll();
      }
    }

    public StaticLayout commentLayoutFor(CharSequence text) {
      return commentCache.get(text);
    }
  }
}
