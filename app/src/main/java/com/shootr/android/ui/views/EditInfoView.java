package com.shootr.android.ui.views;

public interface EditInfoView {

    void setSendButonEnabled(boolean enabled);

    void setTitle(String title);

    void setPlaceText(String place);

    void setFocusOnPlace();

    void closeScreenWithResult(String stautsText);

    String getPlaceText();

    void setMenuShoot();

    void setMenuDone();
}
