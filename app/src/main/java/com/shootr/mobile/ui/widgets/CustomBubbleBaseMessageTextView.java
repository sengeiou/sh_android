package com.shootr.mobile.ui.widgets;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import com.shootr.mobile.R;
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

public class CustomBubbleBaseMessageTextView extends View  {

  public static final String[] ALLOWED_SCHEMAS = { "http://", "https://", "rtsp://" };
  public static final String DEFAULT_SCHEMA = "http://";
  private final String DEFAULT_COLOR_TEXT = "#FF000000";
  private final String MY_MESSAGE_COLOR_TEXT = "#ffffff";

  private EntityContainable baseMessageModel;
  private BaseMessagePressableSpan alreadyPressedSpan;
  private OnUrlClickListener onUrlClickListener;
  private String textColor = "#FF000000";
  private AttributeSet attributeSet;
  private int maxWidth;

  String text = "";
  CharSequence currenText;
  TextPaint textPaint;
  StaticLayout staticLayout;
  private boolean isMine;

  public CustomBubbleBaseMessageTextView(Context context) {
    super(context);
    initLabelView(null);
    setupMaxWidht();
  }

  public CustomBubbleBaseMessageTextView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initLabelView(attrs);
    setupMaxWidht();
  }

  public CustomBubbleBaseMessageTextView(Context context, @Nullable AttributeSet attrs,
      int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initLabelView(attrs);
    setupMaxWidht();
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public CustomBubbleBaseMessageTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initLabelView(attrs);
    setupMaxWidht();
  }

  public void setMine(boolean mine) {
    isMine = mine;
  }

  public void initLabelView(AttributeSet attributeSet) {
    this.attributeSet = attributeSet;

    if (attributeSet != null) {
      TypedArray attributes =
          getContext().obtainStyledAttributes(attributeSet, R.styleable.CustomBubbleBaseMessageTextView);
      try {
        boolean isMine = attributes.getBoolean(R.styleable.CustomBubbleBaseMessageTextView_my_shot, false);
        if (isMine) {
          textColor = MY_MESSAGE_COLOR_TEXT;
        } else {
          textColor = DEFAULT_COLOR_TEXT;
        }
      } finally {
        attributes.recycle();
      }
    } else {
      textColor = DEFAULT_COLOR_TEXT;
    }

    if (textPaint == null) {
      textPaint = new TextPaint();
      textPaint.setAntiAlias(true);
      textPaint.setTextSize(sp(16));
      textPaint.setColor(Color.parseColor(textColor));
      Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
      textPaint.setTypeface(typeface);
      textPaint.linkColor = Color.parseColor("#bdbdbd");
    }
  }

  public void setText(CharSequence text, boolean myShot) {

    currenText = text;
    int textWidth = (int) textPaint.measureText(text.toString());
    if (textWidth > maxWidth) {
      textWidth = maxWidth;
    }


    staticLayout =
        new StaticLayout(text, textPaint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1, 1, true);
    requestLayout();
    invalidate();
    if (text.toString().isEmpty()) {
      setVisibility(GONE);
    } else {
      setVisibility(VISIBLE);
    }
  }

  public CharSequence getCurrenText() {
    return currenText;
  }

  public StaticLayout getStaticLayout() {
    return staticLayout;
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width;
    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int widthRequirement = MeasureSpec.getSize(widthMeasureSpec);
    if (widthMode == MeasureSpec.EXACTLY) {
      width = widthRequirement;
    } else {
      width = staticLayout.getWidth() + getPaddingLeft() + getPaddingRight();
      if (widthMode == MeasureSpec.AT_MOST) {
        if (width > widthRequirement) {
          width = widthRequirement;
          // too long for a single line so relayout as multiline
          staticLayout =
              new StaticLayout(currenText, textPaint, maxWidth, Layout.Alignment.ALIGN_NORMAL,
                  1.0f, 0, false);
        }
      }
    }

    // determine the height
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

    // Required call: set width and height
    setMeasuredDimension(width, height);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.save();
    canvas.translate(getPaddingLeft(), getPaddingTop());
    staticLayout.draw(canvas);
    canvas.restore();
  }

  private void setupMaxWidht() {
    int avatarImageSize = (int) dp(36);
    int avatarImageMargin = (int) dp(8);
    int conatinerPadding = (int) dp(100);

    int imageOffset =  avatarImageMargin + avatarImageSize + conatinerPadding;

    maxWidth = SCREEN_SIZE.x - imageOffset;

  }


  private float sp(float sp) {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
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

  public void addLinks(CharSequence comment, boolean isMine) {
    if (baseMessageModel != null) {
      currenText = comment;
      SpannableStringBuilder stringBuilder = new SpannableStringBuilder(comment);
      spanUrls(stringBuilder);
      spanPollQuestions(stringBuilder);
      spanMentions(stringBuilder);
      spanStreams(stringBuilder);
      setText(stringBuilder, isMine);
    }
  }

  public void addLinksWithText(CharSequence text, boolean isMine) {
    if (baseMessageModel != null) {
      SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
      spanUrls(stringBuilder);
      spanPollQuestions(stringBuilder);
      spanMentions(stringBuilder);
      spanStreams(stringBuilder);
      setText(stringBuilder, isMine);
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

          BaseMessageUsernameSpan usernameClickSpan = new BaseMessageUsernameSpan(mentionModel.getIdUser()) {
            @Override public void onUsernameClick(String idUser) {
              goToUserProfile(idUser);
            }
          };

          stringBuilder.setSpan(usernameClickSpan, start,
              end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

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
    Intent streamIntent = StreamTimelineActivity.newIntent(context, idStream, streamTitle, null);
    context.startActivity(streamIntent);
  }

  @Override public boolean onTouchEvent(@NonNull MotionEvent event) {
    CustomBubbleBaseMessageTextView widget = this;
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

  private BaseMessagePressableSpan getTouchedSpan(MotionEvent event, CustomBubbleBaseMessageTextView widget,
      Spannable buffer) {
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

}
