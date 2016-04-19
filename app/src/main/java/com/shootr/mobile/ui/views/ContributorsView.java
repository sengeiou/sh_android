package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.views.base.LoadDataView;
import java.util.List;

public interface ContributorsView extends LoadDataView {

    void renderAllContributors(List<UserModel> contributors);

    void showAllContributors();

    void goToSearchContributors();

    void showContributorsLimitSnackbar();

    void hideAddContributorsButton();

    void hideAddContributorsText();

    void removeContributorFromList(UserModel userModel);
}
