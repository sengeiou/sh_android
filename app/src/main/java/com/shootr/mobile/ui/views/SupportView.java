package com.shootr.mobile.ui.views;

import com.shootr.mobile.domain.model.stream.Stream;

public interface SupportView {

    void showError();

    void goToStream(Stream blog);

    void handleReport();

    void showAlertDialog();
}
