package com.shootr.mobile.ui.views.nullview;

import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.views.PeopleView;

import java.util.List;

public class NullPeopleView implements PeopleView {

    @Override public void renderUserList(List<UserModel> people) {
        /* no-op */
    }

    @Override public void showPeopleList() {
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
