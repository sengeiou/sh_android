package com.shootr.android.ui.views;

import com.shootr.android.ui.model.UserModel;
import java.util.List;

public interface SuggestedPeopleView {

    void renderSuggestedPeopleList(List<UserModel> users);

    void showError(String messageForError);
}
