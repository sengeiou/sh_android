package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.views.base.LoadDataView;

import java.util.List;

public interface AllParticipantsView extends LoadDataView {

    void renderAllParticipants(List<UserModel> transform);

    void showAllParticipantsList();

    void goToSearchParticipants();

    void renderParticipantsBelow(List<UserModel> userModels);

    void hideProgressView();

    void showProgressView();
}
