package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.presenter.ShotDetailPresenter;
import com.shootr.android.ui.views.ShotDetailView;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.TimeFormatter;
import java.util.Date;
import javax.inject.Inject;

public class ShotDetailActivity extends BaseSignedInActivity implements ShotDetailView {

    public static final String EXTRA_SHOT = "shot";

    @InjectView(R.id.shot_avatar) ImageView avatar;
    @InjectView(R.id.shot_user_name) TextView username;
    @InjectView(R.id.shot_timestamp) TextView timestamp;
    @InjectView(R.id.shot_text) TextView shotText;
    @InjectView(R.id.shot_image) ImageView shotImage;

    @Inject PicassoWrapper picasso;
    @Inject TimeFormatter timeFormatter;
    @Inject ShotDetailPresenter presenter;

    public static Intent getIntentForActivity(Context context, ShotModel shotModel) {
        Intent intent = new Intent(context, ShotDetailActivity.class);
        intent.putExtra(EXTRA_SHOT, shotModel);
        return intent;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!restoreSessionOrLogin()) return;

        setContainerContent(R.layout.activity_shot_detail);
        ButterKnife.inject(this);

        setupActionBar();

        ShotModel shotModel = extractShotFromIntent();
        initializePresenter(shotModel);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private ShotModel extractShotFromIntent() {
        return ((ShotModel) getIntent().getSerializableExtra(EXTRA_SHOT));
    }

    private void initializePresenter(ShotModel shotModel) {
        presenter.initialize(this, shotModel);
    }

    @Override public void renderShot(ShotModel shotModel) {
        username.setText(shotModel.getUsername());
        timestamp.setText(getTimestampForDate(shotModel.getCsysBirth()));
        String comment = shotModel.getComment();
        if (comment != null) {
            shotText.setText(comment);
        } else {
            shotText.setVisibility(View.GONE);
        }
        picasso.loadProfilePhoto(shotModel.getPhoto()).into(avatar);
        String imageUrl = shotModel.getImage();
        if (imageUrl != null) {
            picasso.loadTimelineImage(imageUrl).into(shotImage);
        } else {
            shotImage.setVisibility(View.GONE);
        }
    }

    @Override protected void onResume() {
        super.onResume();
        presenter.resume();
    }

    @Override protected void onPause() {
        super.onPause();
        presenter.pause();
    }

    private String getTimestampForDate(Date date) {
        return timeFormatter.getDateAndTimeDetailed(date.getTime());
    }
}
