package com.shootr.android.ui.views;

import com.shootr.android.domain.Stream;

public interface SupportView {

    void showError(String errorMessage);

    void goToBlogStream(Stream blog);
}
