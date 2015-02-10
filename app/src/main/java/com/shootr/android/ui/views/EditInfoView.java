package com.shootr.android.ui.views;

public interface EditInfoView {

    void setSendButonEnabled(boolean enabled);

    void setStatusText(String status);

    void setFocusOnStatus();

    void closeScreenWithResult(String stautsText);

    String getStatusText();

    void setMenuShoot();
}
