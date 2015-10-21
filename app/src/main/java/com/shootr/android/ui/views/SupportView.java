package com.shootr.android.ui.views;

import com.shootr.android.domain.Stream;

public interface SupportView {

    void showError();

    void goToStream(Stream blog);
}
