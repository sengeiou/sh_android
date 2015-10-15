package com.shootr.android.ui.views;

import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.views.base.LoadDataView;
import java.util.List;

public interface PeopleView extends LoadDataView {

    void renderUserList(List<UserModel> people);

    void showPeopleList();
}
