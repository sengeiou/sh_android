package com.shootr.android.ui.views;

import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.views.base.LoadDataView;
import java.util.List;

public interface AllParticipantsView extends LoadDataView {

    void renderAllParticipants(List<UserModel> transform);

    void showAllParticipantsList();

    void goToSearchParticipants();

    void renderParticipantsBelow(List<UserModel> userModels);

    void hideProgressView();

    void showProgressView();
}
