package com.shootr.mobile.ui.widgets;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public abstract class PollQuestionSpan extends ClickableSpan implements ClickableTextView.PressableSpan {

  private String pollQuestion;
  private String idStream;

  public PollQuestionSpan(String idStream, String pollQuestion) {
    this.pollQuestion = pollQuestion;
    this.idStream = idStream;
  }

  private boolean isPressed = false;

  @Override public void setPressed(boolean isPressed) {
    this.isPressed = isPressed;
  }

  @Override public void onClick(View widget) {
    onPollQuestionClick(idStream);
  }

  public abstract void onPollQuestionClick(String pollQuestion);

  @Override public void updateDrawState(TextPaint ds) {
    super.updateDrawState(ds);
    ds.setUnderlineText(isPressed);
  }
}
