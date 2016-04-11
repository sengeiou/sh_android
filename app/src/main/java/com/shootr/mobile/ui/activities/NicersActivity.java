package com.shootr.mobile.ui.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.views.NicersView;
import com.shootr.mobile.util.FeedbackMessage;
import java.util.List;
import javax.inject.Inject;

public class NicersActivity extends BaseToolbarDecoratedActivity implements NicersView {

    @Bind(R.id.nicerslist_list) ListView nicerslistListView;
    @Bind(R.id.nicerslist_progress) ProgressBar progressBar;

    @Inject FeedbackMessage feedbackMessage;

    View progressViewContent;
    View progressView;

    @Override protected int getLayoutResource() {
        return R.layout.activity_nicers;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        //TODO: nicerslistListView.setAdapter(getNicersAdapter());

        progressView = getLoadingView();
        progressViewContent = ButterKnife.findById(progressView, R.id.loading_progress);
    }

    private View getLoadingView() {
        return LayoutInflater.from(this).inflate(R.layout.item_list_loading, nicerslistListView, false);
    }

    @Override protected void initializePresenter() {
        //TODO: initialize presenter
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override public void renderNicers(List<UserModel> transform) {
        //TODO: call adapter.renderNicers()
    }

    @Override public void showNicersList() {
        nicerslistListView.setVisibility(View.VISIBLE);
    }

    @Override public void hideProgressView() {
        //TODO
    }

    @Override public void showProgressView() {
        //TODO
    }

    @Override public void showEmpty() {
        /* no-op */
    }

    @Override public void hideEmpty() {
        /* no-op */
    }

    @Override public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override public void showError(String message) {
        feedbackMessage.show(getView(), message);
    }
}
