package com.shootr.android.ui.views;

public interface EditInfoView {

    void setSendButonEnabled(boolean enabled);

    void setTitle(String title);

    void setStatusText(String place);

    void setFocusOnStatus();

    void closeScreenWithResult(String stautsText);

    String getStatusText();

    void setMenuShoot();

    void setMenuDone();
}
