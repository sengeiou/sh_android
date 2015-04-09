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
import butterknife.OnClick;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.component.PhotoPickerController;
import com.shootr.android.ui.fragments.NewShotBarViewDelegate;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.presenter.NewShotBarPresenter;
import com.shootr.android.ui.presenter.ShotDetailPresenter;
import com.shootr.android.ui.views.NewShotBarView;
import com.shootr.android.ui.views.ShotDetailView;
import com.shootr.android.ui.widgets.ClickableTextView;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.TimeFormatter;
import java.io.File;
import java.util.Date;
import javax.inject.Inject;
import timber.log.Timber;

public class ShotDetailActivity extends BaseSignedInActivity implements ShotDetailView, NewShotBarView {

    public static final String EXTRA_SHOT = "shot";

    @InjectView(R.id.shot_avatar) ImageView avatar;
    @InjectView(R.id.shot_user_name) TextView username;
    @InjectView(R.id.shot_timestamp) TextView timestamp;
    @InjectView(R.id.shot_text) ClickableTextView shotText;
    @InjectView(R.id.shot_image) ImageView shotImage;
    @InjectView(R.id.shot_event_title) TextView eventTitle;
    @InjectView(R.id.shot_bar_text) TextView replyPlaceholder;
    @InjectView(R.id.shot_bar_photo) View replyPhotoButton;
    @InjectView(R.id.shot_bar_drafts) View replyDraftsButton;

    @Inject PicassoWrapper picasso;
    @Inject TimeFormatter timeFormatter;
    @Inject ShotDetailPresenter detailPresenter;
    @Inject NewShotBarPresenter newShotBarPresenter;

    private PhotoPickerController photoPickerController;
    private NewShotBarViewDelegate newShotBarViewDelegate;

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
        setupPhotoPicker();
        setupNewShotBarDelegate();
        initializePresenter(shotModel);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        photoPickerController.onActivityResult(requestCode, resultCode, data);
    }

    private void setupPhotoPicker() {
        photoPickerController =
          new PhotoPickerController.Builder().onActivity(this).withHandler(new PhotoPickerController.Handler() {
              @Override public void onSelected(File imageFile) {
                  newShotBarPresenter.newShotImagePicked(imageFile);
              }

              @Override public void onError(Exception e) {
                  //TODO mostrar algo
                  Timber.e(e, "Error selecting image");
              }

              @Override public void startPickerActivityForResult(Intent intent, int requestCode) {
                  startActivityForResult(intent, requestCode);
              }
          }).build();
    }

    private void setupNewShotBarDelegate() {
        newShotBarViewDelegate = new NewShotBarViewDelegate(this, photoPickerController, replyDraftsButton);
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
        detailPresenter.initialize(this, shotModel);
        newShotBarPresenter.initialize(this);
    }

    @OnClick(R.id.shot_image)
    public void onImageClick() {
        detailPresenter.imageClick();
    }

    @OnClick(R.id.shot_avatar)
    public void onAvatarClick() {
        detailPresenter.avatarClick();
    }

    @OnClick(R.id.shot_bar_text)
    public void onReplyClick() {
        newShotBarPresenter.newShotFromTextBox();
    }

    @Override public void renderShot(ShotModel shotModel) {

        username.setText(shotModel.getUsername());
        timestamp.setText(getTimestampForDate(shotModel.getCsysBirth()));
        String comment = shotModel.getComment();
        if (comment != null) {
            shotText.setText(comment);
            shotText.addLinks();
        } else {
            shotText.setVisibility(View.GONE);
        }
        showEventTitle(shotModel);
        picasso.loadProfilePhoto(shotModel.getPhoto()).into(avatar);
        String imageUrl = shotModel.getImage();
        if (imageUrl != null) {
            picasso.loadTimelineImage(imageUrl).into(shotImage);
        } else {
            shotImage.setVisibility(View.GONE);
        }
    }

    private void showEventTitle(ShotModel shotModel) {
        String title = shotModel.getEventTitle();
        if (title != null) {
            eventTitle.setText(shotModel.getEventTitle());
            eventTitle.setVisibility(View.VISIBLE);
        } else {
            eventTitle.setVisibility(View.GONE);
        }
    }

    @Override public void openImage(String imageUrl) {
        Intent intentForImage = PhotoViewActivity.getIntentForActivity(this, imageUrl);
        startActivity(intentForImage);
    }

    @Override public void openProfile(Long idUser) {
        Intent intentForUser = ProfileContainerActivity.getIntent(this, idUser);
        startActivity(intentForUser);
    }

    @Override protected void onResume() {
        super.onResume();
        detailPresenter.resume();
        newShotBarPresenter.resume();
    }

    @Override protected void onPause() {
        super.onPause();
        detailPresenter.pause();
        newShotBarPresenter.resume();
    }

    private String getTimestampForDate(Date date) {
        return timeFormatter.getDateAndTimeDetailed(date.getTime());
    }

    @Override public void openNewShotView() {
        newShotBarViewDelegate.openNewShotView();
    }

    @Override public void pickImage() {
        newShotBarViewDelegate.pickImage();
    }

    @Override public void openNewShotViewWithImage(File image) {
        newShotBarViewDelegate.openNewShotViewWithImage(image);
    }

    @Override public void showDraftsButton() {
        newShotBarViewDelegate.showDraftsButton();
    }

    @Override public void hideDraftsButton() {
        newShotBarViewDelegate.hideDraftsButton();
    }
}
