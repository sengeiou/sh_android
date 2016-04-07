package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.views.base.LoadDataView;

import java.util.List;

public interface FindFriendsView extends LoadDataView {

    void renderFriends(List<UserModel> participants);

    void setCurrentQuery(String query);

    void hideKeyboard();

    void showContent();

    void hideContent();

    void setHasMoreItemsToLoad(Boolean hasMoreItems);

    void addFriends(List<UserModel> friends);

    void hideProgress();

    void restoreState();
}
