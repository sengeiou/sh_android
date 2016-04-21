package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.views.base.LoadDataView;
import java.util.List;

public interface FindContributorsView extends LoadDataView {

    void renderContributors(List<UserModel> contributors);

    void setCurrentQuery(String query);

    void hideKeyboard();

    void showContent();

    void hideContent();

    void showAddConfirmation(UserModel userModel);

    void finishActivity();
}
