package com.shootr.mobile.ui.widgets;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public abstract class StreamTitleSpan extends ClickableSpan implements ClickableTextView.PressableSpan {

    private String idAuthor;
    private String streamId;
    private String streamShortTitle;

    public StreamTitleSpan(String streamId, String streamShortTitle, String idAuthor) {
        this.streamId = streamId;
        this.streamShortTitle = streamShortTitle;
        this.idAuthor = idAuthor;
    }

    private boolean isPressed = false;

    @Override public void setPressed(boolean isPressed) {
        this.isPressed = isPressed;
    }

    @Override public void onClick(View widget) {
        onStreamClick(streamId, streamShortTitle, idAuthor);
    }

    public abstract void onStreamClick(String streamId, String streamShortTitle, String idAuthor);

    @Override public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(isPressed);
    }
}