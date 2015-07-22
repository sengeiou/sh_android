package com.shootr.android.ui.widgets;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public abstract class StreamTitleSpan extends ClickableSpan implements ClickableTextView.PressableSpan {

    private String eventId;
    private String eventTitle;

    public StreamTitleSpan(String eventId, String eventTitle) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
    }

    private boolean isPressed = false;

    @Override public void setPressed(boolean isPressed) {
        this.isPressed = isPressed;
    }

    @Override public void onClick(View widget) {
        onStreamClick(eventId, eventTitle);
    }

    public abstract void onStreamClick(String eventId, String eventTitle);

    @Override public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(isPressed);
    }
}