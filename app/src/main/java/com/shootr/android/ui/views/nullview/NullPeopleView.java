package com.shootr.android.ui.views.nullview;

import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.views.PeopleView;
import java.util.List;

public class NullPeopleView implements PeopleView {

    @Override public void renderUserList(List<UserModel> people) {
        /* no-op */
    }

    @Override public void showEmpty() {
        /* no-op */
    }

    @Override public void hideEmpty() {
        /* no-op */
    }

    @Override public void showLoading() {
        /* no-op */
    }

    @Override public void hideLoading() {
        /* no-op */
    }

    @Override public void showError(String message) {
        /* no-op */
    }
}