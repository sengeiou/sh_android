package com.shootr.mobile.ui.widgets;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public abstract class NicerSpan extends ClickableSpan implements ClickableTextView.PressableSpan {

  private String username;

  public NicerSpan(String username) {
    this.username = username;
  }

  private boolean isPressed = false;

  @Override public void setPressed(boolean isPressed) {
    this.isPressed = isPressed;
  }

  @Override public void onClick(View widget) {
    onUsernameClick(username);
  }

  public abstract void onUsernameClick(String username);

  @Override public void updateDrawState(TextPaint ds) {
    super.updateDrawState(ds);
    ds.setUnderlineText(isPressed);
  }
}
