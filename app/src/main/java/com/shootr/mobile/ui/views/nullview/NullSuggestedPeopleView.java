package com.shootr.mobile.ui.views.nullview;

import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.views.SuggestedPeopleView;
import java.util.List;

public class NullSuggestedPeopleView implements SuggestedPeopleView {

    @Override public void renderSuggestedPeopleList(List<UserModel> users) {
        /* no-op */
    }

    @Override public void showError(String messageForError) {
        /* no-op */
    }

    @Override public void refreshSuggestedPeople(List<UserModel> suggestedPeople) {
        /* no-op */
    }
}
