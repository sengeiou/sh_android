package com.shootr.mobile.ui.widgets;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public abstract class StreamTitleBoldSpan extends ClickableSpan
    implements ClickableTextView.PressableSpan, BaseMessagePressableSpan {

  private String streamId;
  private String streamTitle;

  public StreamTitleBoldSpan(String streamId, String streamTitle) {
    this.streamId = streamId;
    this.streamTitle = streamTitle;
  }

  private boolean isPressed = false;

  @Override public void setPressed(boolean isPressed) {
    this.isPressed = isPressed;
  }

  @Override public void onClick(View widget) {
    onStreamClick(streamId, streamTitle);
  }

  public abstract void onStreamClick(String streamId, String streamTitle);

  @Override public void updateDrawState(TextPaint ds) {
    super.updateDrawState(ds);
    ds.setUnderlineText(isPressed);
    ds.setTypeface(Typeface.DEFAULT_BOLD);
  }
}