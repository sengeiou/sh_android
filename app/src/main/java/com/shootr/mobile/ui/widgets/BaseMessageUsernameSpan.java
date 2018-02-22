package com.shootr.mobile.ui.widgets;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public abstract class BaseMessageUsernameSpan extends ClickableSpan
    implements BaseMessagePressableSpan {
  private String idUser;

  public BaseMessageUsernameSpan(String idUser) {
    this.idUser = idUser;
  }

  private boolean isPressed = false;

  @Override public void setPressed(boolean isPressed) {
    this.isPressed = isPressed;
  }

  @Override public void onClick(View widget) {
    onUsernameClick(idUser);
  }

  public abstract void onUsernameClick(String idUser);

  @Override public void updateDrawState(TextPaint ds) {
    super.updateDrawState(ds);
    ds.setUnderlineText(isPressed);
  }
}
