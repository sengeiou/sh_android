package com.shootr.android.ui.views.nullview;

import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.views.SuggestedPeopleView;
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
