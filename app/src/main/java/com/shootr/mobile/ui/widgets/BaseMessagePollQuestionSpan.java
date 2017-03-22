package com.shootr.mobile.ui.widgets;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public abstract class BaseMessagePollQuestionSpan extends ClickableSpan
    implements BaseMessagePressableSpan {
  protected String idPoll;
  private boolean isPressed = false;

  public BaseMessagePollQuestionSpan(String idPoll) {
    this.idPoll = idPoll;
  }

  @Override public void setPressed(boolean isPressed) {
    this.isPressed = isPressed;
  }

  @Override public void onClick(View widget) {
    onPollQuestionClick(idPoll);
  }

  public abstract void onPollQuestionClick(String pollQuestion);

  @Override public void updateDrawState(TextPaint ds) {
    super.updateDrawState(ds);
    ds.setUnderlineText(isPressed);
  }
}
