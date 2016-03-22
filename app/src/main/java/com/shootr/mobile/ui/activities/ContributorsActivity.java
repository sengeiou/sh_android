package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.ContributorsPresenter;
import com.shootr.mobile.ui.views.ContributorsView;
import java.util.List;
import javax.inject.Inject;

public class ContributorsActivity extends BaseToolbarDecoratedActivity implements ContributorsView {

    private static final String EXTRA_STREAM = "stream";

    @Bind(R.id.contributors_list) ListView contributorsListView;
    @Bind(R.id.contributors_progress) ProgressBar progressBar;
    @Bind(R.id.contributors_empty) TextView emptyTextView;

    @Inject ContributorsPresenter presenter;

    public static Intent newIntent(Context context, String idStream) {
        Intent intent = new Intent(context, ContributorsActivity.class);
        intent.putExtra(EXTRA_STREAM, idStream);
        return intent;
    }

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
        String idStream = getIntent().getStringExtra(EXTRA_STREAM);
        presenter.initialize(this, idStream);
    }
    //endregion

    //region View methods
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

    @Override public void renderAllContributors(List<UserModel> contributors) {

    }

    @Override public void showAllContributors() {

    }

    @Override public void goToSearchContributors() {

    }
    //endregion
}
