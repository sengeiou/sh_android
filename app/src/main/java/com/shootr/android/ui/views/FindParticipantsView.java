package com.shootr.android.ui.views;

import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.views.base.LoadDataView;
import java.util.List;

public interface FindParticipantsView extends LoadDataView {

    void renderParticipants(List<UserModel> participants);

    void setCurrentQuery(String query);

    void hideKeyboard();

    void showContent();

    void hideContent();
}
