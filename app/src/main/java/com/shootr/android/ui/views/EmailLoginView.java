package com.shootr.android.ui.views;

public interface EmailLoginView {

    void goToTimeline();

    String getUsernameOrEmail();

    String getPassword();

    void emailButtonIsDisabled();

    void emailButtonIsEnabled();

    void emailButtonLoginStateHasChanged();
}
