package com.shootr.android.ui.views;

import com.shootr.android.ui.model.UserModel;
import java.util.List;

public interface PeopleView extends LoadDataView {

    void renderUserList(List<UserModel> people);
}