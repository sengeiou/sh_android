package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.adapters.ShotDetailWithRepliesAdapter;
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

public class ShotDetailActivity extends BaseToolbarDecoratedActivity implements ShotDetailView, NewShotBarView {

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

    @Override protected int getLayoutResource() {
        return R.layout.activity_shot_detail;
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        toolbarDecorator.hideTitle();
    }

    @Override protected void initializeViews() {
        ButterKnife.inject(this);
        setupPhotoPicker();
        setupNewShotBarDelegate(extractShotFromIntent());
        setupAdapter();
    }

    @Override protected void initializePresenter() {
        initializePresenter(extractShotFromIntent());
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

    private void setupNewShotBarDelegate(final ShotModel shotModel) {
        newShotBarViewDelegate = new NewShotBarViewDelegate(photoPickerController, replyDraftsButton) {
            @Override public void openNewShotView() {
                Intent newShotIntent = PostNewShotActivity.IntentBuilder //
                  .from(ShotDetailActivity.this) //
                  .inReplyTo(shotModel.getIdShot(), shotModel.getUsername()) //
                  .build();
                startActivity(newShotIntent);
            }

            @Override public void openNewShotViewWithImage(File image) {
                Intent newShotIntent = PostNewShotActivity.IntentBuilder //
                  .from(ShotDetailActivity.this) //
                    //TODO reply
                  .withImage(image) //
                  .build();
                startActivity(newShotIntent);
            }
        };
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

    @OnClick(R.id.shot_bar_drafts)
    public void openDrafts() {
        startActivity(new Intent(this, DraftsActivity.class));
    }
    
    @Override public void renderShot(ShotModel shotModel) {

        username.setText(getUsernameTitle(shotModel));
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

    private String getUsernameTitle(ShotModel shotModel) {
        if (shotModel.isReply()) {
            return getString(R.string.reply_name_pattern, shotModel.getUsername(), shotModel.getReplyUsername());
        } else {
            return shotModel.getUsername();
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

    @Override public void setReplyUsername(String username) {
        replyPlaceholder.setText(getString(R.string.reply_placeholder_pattern, username));
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
