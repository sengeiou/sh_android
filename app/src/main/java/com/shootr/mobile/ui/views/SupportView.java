package com.shootr.mobile.ui.views;

import com.shootr.mobile.domain.Stream;

public interface SupportView {

    void showError();

    void goToStream(Stream blog);

    void showSupportLanguageDialog();
}
