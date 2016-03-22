package com.shootr.mobile.ui.activities;

import android.os.Bundle;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.views.AllParticipantsView;
import java.util.List;

public class ContributorsActivity extends BaseToolbarDecoratedActivity implements AllParticipantsView {

    //region Lifecycle
    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {

    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_contributors;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
    }

    @Override protected void initializePresenter() {

    }
    //endregion

    //region View methods
    @Override public void renderAllParticipants(List<UserModel> transform) {

    }

    @Override public void showAllParticipantsList() {

    }

    @Override public void goToSearchParticipants() {

    }

    @Override public void renderParticipantsBelow(List<UserModel> userModels) {

    }

    @Override public void hideProgressView() {

    }

    @Override public void showProgressView() {

    }

    @Override public void showEmpty() {

    }

    @Override public void hideEmpty() {

    }

    @Override public void showLoading() {

    }

    @Override public void hideLoading() {

    }

    @Override public void showError(String message) {

    }
    //endregion
}
