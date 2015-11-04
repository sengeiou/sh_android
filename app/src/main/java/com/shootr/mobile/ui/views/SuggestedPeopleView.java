package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.UserModel;
import java.util.List;

public interface SuggestedPeopleView {

    void renderSuggestedPeopleList(List<UserModel> users);

    void showError(String messageForError);

    void refreshSuggestedPeople(List<UserModel> suggestedPeople);
}
