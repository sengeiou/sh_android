package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.views.base.LoadDataView;

import java.util.List;

public interface PeopleView extends LoadDataView {

    void renderUserList(List<UserModel> people);

    void showPeopleList();
}
