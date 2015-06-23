package com.shootr.android.ui.widgets;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public abstract class EventTitleSpan extends ClickableSpan implements ClickableTextView.PressableSpan {

    private String eventId;

    public EventTitleSpan(String eventId) {
        this.eventId = eventId;
    }

    private boolean isPressed = false;

    @Override public void setPressed(boolean isPressed) {
        this.isPressed = isPressed;
    }

    @Override public void onClick(View widget) {
        onEventClick(eventId);
    }

    public abstract void onEventClick(String eventId);

    @Override public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(isPressed);
    }
}