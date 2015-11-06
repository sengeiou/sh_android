package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.views.base.LoadDataView;
import java.util.List;

public interface FindParticipantsView extends LoadDataView {

    void renderParticipants(List<UserModel> participants);

    void setCurrentQuery(String query);

    void hideKeyboard();

    void showContent();

    void hideContent();
}
