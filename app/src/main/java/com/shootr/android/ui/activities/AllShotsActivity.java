package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.adapters.TimelineAdapter;
import com.shootr.android.ui.fragments.ProfileFragment;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.presenter.AllShotsPresenter;
import com.shootr.android.ui.views.AllShotsView;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.UsernameClickListener;
import java.util.List;
import javax.inject.Inject;

public class AllShotsActivity extends BaseToolbarDecoratedActivity implements AllShotsView {

    @Inject AllShotsPresenter presenter;
    @Inject AndroidTimeUtils timeUtils;

    @Bind(R.id.all_shots_list) ListView listView;
    @Bind(R.id.timeline_empty) View emptyView;
    @Bind(R.id.all_shots_loading) View loadingView;

    @Deprecated private TimelineAdapter adapter;
    private View.OnClickListener avatarClickListener;
    private View.OnClickListener imageClickListener;
    private TimelineAdapter.VideoClickListener videoClickListener;
    private UsernameClickListener usernameClickListener;
    private View footerProgress;

    public static Intent newIntent(Context context, String userId) {
        Intent intent = new Intent(context, AllShotsActivity.class);
        intent.putExtra(ProfileFragment.ARGUMENT_USER, userId);
        return intent;
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_all_shots;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        if (getIntent().getExtras() == null) {
            throw new RuntimeException("No intent extras, no party");
        }
        setupListAdapter();
    }

    @Override protected void initializePresenter() {
        String userId = getIntent().getStringExtra(ProfileFragment.ARGUMENT_USER);
        presenter.initialize(this, userId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    @OnItemClick(R.id.all_shots_list)
    public void openShot(int position) {
        ShotModel shot = adapter.getItem(position);
        Intent intent = ShotDetailActivity.getIntentForActivity(this, shot);
        startActivity(intent);
    }

    private void setupListAdapter() {
        avatarClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = ((TimelineAdapter.ViewHolder) v.getTag()).position;
                openProfile(position);
            }
        };

        imageClickListener = new View.OnClickListener() {
            @Override public void onClick(View v) {
                int position = ((TimelineAdapter.ViewHolder) v.getTag()).position;
                openImage(position);
            }
        };

        usernameClickListener = new UsernameClickListener() {
            @Override
            public void onClick(String username) {
                goToUserProfile(username);
            }
        };

        videoClickListener = new TimelineAdapter.VideoClickListener() {
            @Override
            public void onClick(String url) {
                onVideoClick(url);
            }
        };

        View footerView = LayoutInflater.from(this).inflate(R.layout.item_list_loading, listView, false);
        footerProgress = ButterKnife.findById(footerView, R.id.loading_progress);

        footerProgress.setVisibility(View.GONE);

        listView.addFooterView(footerView, null, false);

        adapter = new TimelineAdapter(this, picasso, avatarClickListener,
          imageClickListener, videoClickListener, usernameClickListener, timeUtils){
            @Override protected boolean shouldShowTag() {
                return true;
            }
        };
        listView.setAdapter(adapter);
    }

    public void openProfile(int position) {
        ShotModel shotVO = adapter.getItem(position);
        Intent profileIntent = ProfileContainerActivity.getIntent(this, shotVO.getIdUser());
        startActivity(profileIntent);
    }

    public void openImage(int position) {
        ShotModel shotVO = adapter.getItem(position);
        String imageUrl = shotVO.getImage();
        if (imageUrl != null) {
            Intent intentForImage = PhotoViewActivity.getIntentForActivity(this, imageUrl);
            startActivity(intentForImage);
        }
    }

    private void startProfileContainerActivity(String username) {
        Intent intentForUser = ProfileContainerActivity.getIntentWithUsername(this, username);
        startActivity(intentForUser);
    }

    private void goToUserProfile(String username) {
        startProfileContainerActivity(username);
    }

    private void onVideoClick(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override public void showError(String messageForError) {
        Toast.makeText(this, messageForError, Toast.LENGTH_SHORT).show();
    }

    @Override public void hideLoading() {
        loadingView.setVisibility(View.GONE);
    }

    @Override public void setShots(List<ShotModel> shotModels) {
        adapter.setShots(shotModels);
    }

    @Override public void hideEmpty() {
        emptyView.setVisibility(View.GONE);
    }

    @Override public void showShots() {
        listView.setVisibility(View.VISIBLE);
    }

    @Override public void showEmpty() {
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override public void hideShots() {
        listView.setVisibility(View.GONE);
    }

    @Override public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }
}
