package com.shootr.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.adapters.TimelineAdapter;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.views.ListingView;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.UsernameClickListener;
import javax.inject.Inject;

public class ListingActivity extends BaseToolbarDecoratedActivity implements ListingView {

    public static final String EXTRA_ID_USER = "idUser";

    @InjectView(R.id.listing_list) ListView listingView;
    @InjectView(R.id.listing_loading) View loadingView;

    @Inject AndroidTimeUtils timeUtils;

    private TimelineAdapter adapter;
    private View.OnClickListener avatarClickListener;
    private View.OnClickListener imageClickListener;
    private TimelineAdapter.VideoClickListener videoClickListener;
    private UsernameClickListener usernameClickListener;

    @Override protected int getLayoutResource() {
        return R.layout.activity_listing;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.inject(this);
        setupListAdapter();
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

            }
        };

        adapter = new TimelineAdapter(this, picasso, avatarClickListener,
          imageClickListener, videoClickListener, usernameClickListener, timeUtils);
        listingView.setAdapter(adapter);
    }

    private void goToUserProfile(String username) {
        startProfileContainerActivity(username);
    }

    private void startProfileContainerActivity(String username) {
        Intent intentForUser = ProfileContainerActivity.getIntentWithUsername(this, username);
        startActivity(intentForUser);
    }

    private void openImage(int position) {
        ShotModel shotVO = adapter.getItem(position);
        String imageUrl = shotVO.getImage();
        if (imageUrl != null) {
            Intent intentForImage = PhotoViewActivity.getIntentForActivity(this, imageUrl);
            startActivity(intentForImage);
        }
    }

    private void openProfile(int position) {
        ShotModel shotVO = adapter.getItem(position);
        Intent profileIntent = ProfileContainerActivity.getIntent(this, shotVO.getIdUser());
        startActivity(profileIntent);
    }

    @Override protected void initializePresenter() {
        Intent intent = getIntent();
        String idUser = intent.getStringExtra(EXTRA_ID_USER);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }
}
