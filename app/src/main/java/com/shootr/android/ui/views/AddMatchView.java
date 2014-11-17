package com.shootr.android.ui.views;

import com.shootr.android.ui.model.MatchModel;
import java.util.List;

public interface AddMatchView {

    void hideKeyboard();

    void renderResults(List<MatchModel> matches);

    void hideResults();

    void showLoading();

    void hideLoading();

    void showEmpty();

    void hideEmpty();

    void alertConnectionNotAvailable();

    void alertComunicationError();
}
