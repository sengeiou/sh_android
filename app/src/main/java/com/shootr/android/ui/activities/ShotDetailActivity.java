package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.adapters.ShotDetailWithRepliesAdapter;
import com.shootr.android.ui.adapters.TimelineAdapter;
import com.shootr.android.ui.adapters.listeners.NiceShotListener;
import com.shootr.android.ui.component.PhotoPickerController;
import com.shootr.android.ui.fragments.NewShotBarViewDelegate;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.presenter.NewShotBarPresenter;
import com.shootr.android.ui.presenter.ShotDetailPresenter;
import com.shootr.android.ui.views.NewShotBarView;
import com.shootr.android.ui.views.ShotDetailView;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.TimeFormatter;
import com.shootr.android.util.UsernameClickListener;
import java.io.File;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class ShotDetailActivity extends BaseToolbarDecoratedActivity implements ShotDetailView, NewShotBarView {

    public static final String EXTRA_SHOT = "shot";

    @Bind(R.id.shot_detail_list) RecyclerView detailList;
    @Bind(R.id.detail_new_shot_bar) View newShotBar;
    @Bind(R.id.shot_bar_text) TextView replyPlaceholder;
    @Bind(R.id.shot_bar_drafts) View replyDraftsButton;

    @Inject PicassoWrapper picasso;
    @Inject TimeFormatter timeFormatter;
    @Inject AndroidTimeUtils timeUtils;
    @Inject ShotDetailPresenter detailPresenter;
    @Inject NewShotBarPresenter newShotBarPresenter;

    private PhotoPickerController photoPickerController;
    private NewShotBarViewDelegate newShotBarViewDelegate;
    private ShotDetailWithRepliesAdapter detailAdapter;

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

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
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

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupAdapter() {
        detailAdapter = new ShotDetailWithRepliesAdapter(picasso, //
          new ShotDetailWithRepliesAdapter.AvatarClickListener() {
              @Override
              public void onClick(String userId) {
                  onAvatarClick(userId);
              }
          }, //
          new ShotDetailWithRepliesAdapter.ImageClickListener() {
              @Override
              public void onClick(ShotModel shot) {
                  onImageClick(shot);
              }
          }, //
          new TimelineAdapter.VideoClickListener() {
              @Override
              public void onClick(String url) {
                  onVideoClick(url);
              }
          }, //
          new UsernameClickListener() {
              @Override
              public void onClick(String username) {
                  onUsernameClick(username);
              }
          }, //
          new ShotDetailWithRepliesAdapter.OnParentShownListener() {
              @Override
              public void onShown() {
                  detailList.scrollToPosition(0);
              }
          }, //
          new NiceShotListener() {
              @Override
              public void markNice(String idShot) {
                  detailPresenter.markNiceShot(idShot);
              }

              @Override
              public void unmarkNice(String idShot) {
                  detailPresenter.unmarkNiceShot(idShot);
              }
          }, //
          timeFormatter, getResources(), timeUtils);
        detailList.setLayoutManager(new LinearLayoutManager(this));
        detailList.setAdapter(detailAdapter);
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
                  .withImage(image) //
                  .inReplyTo(shotModel.getIdShot(), shotModel.getUsername()) //
                  .build();
                startActivity(newShotIntent);
            }

            @Override public void showStreamIsRemoved() {
                /* no-op */
            }

            @Override public void openDrafts() {
                /* no-op */
            }
        };
    }

    private ShotModel extractShotFromIntent() {
        return (ShotModel) getIntent().getSerializableExtra(EXTRA_SHOT);
    }

    private void initializePresenter(ShotModel shotModel) {
        detailPresenter.initialize(this, shotModel);
        newShotBarPresenter.initialize(this);
    }

    //region Listeners
    public void onImageClick(ShotModel shot) {
        detailPresenter.imageClick(shot);
    }

    public void onAvatarClick(String userId) {
        detailPresenter.avatarClick(userId);
    }

    public void onUsernameClick(String username){
        detailPresenter.usernameClick(username);
    }


    private void onVideoClick(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @OnClick(R.id.shot_bar_text) public void onReplyClick() {
        newShotBarPresenter.newShotFromTextBox();
    }


    @OnClick(R.id.shot_bar_photo)
    public void onStartNewShotWithPhoto() {
        newShotBarPresenter.newShotFromImage();
    }

    @OnClick(R.id.shot_bar_drafts) public void openDrafts() {
        startActivity(new Intent(this, DraftsActivity.class));
    }
    //endregion

    //region View methods
    @Override public void renderShot(ShotModel shotModel) {
        detailAdapter.renderMainShot(shotModel);
    }

    @Override public void renderReplies(List<ShotModel> shotModels) {
        detailAdapter.renderReplies(shotModels);
    }

    @Override public void openImage(String imageUrl) {
        Intent intentForImage = PhotoViewActivity.getIntentForActivity(this, imageUrl);
        startActivity(intentForImage);
    }

    @Override public void openProfile(String idUser) {
        Intent intentForUser = ProfileContainerActivity.getIntent(this, idUser);
        startActivity(intentForUser);
    }

    @Override public void setReplyUsername(String username) {
        replyPlaceholder.setText(getString(R.string.reply_placeholder_pattern, username));
    }

    @Override public void hideNewReply() {
        newShotBar.setVisibility(View.GONE);
    }

    @Override public void scrollToBottom() {
        detailList.smoothScrollToPosition(detailAdapter.getItemCount() - 1);
    }

    @Override public void renderParent(ShotModel parentShot) {
        detailAdapter.renderParentShot(parentShot);
    }

    @Override
    public void showUserNotFoundNotification() {
        Toast.makeText(this,"User not found",Toast.LENGTH_LONG).show();
    }

    @Override
    public void startProfileContainerActivity(String username) {
        Intent intentForUser = ProfileContainerActivity.getIntentWithUsername(this, username);
        startActivity(intentForUser);
    }

    @Override
    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
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

    @Override public void showStreamIsRemoved() {
        Toast.makeText(this, getString(R.string.error_stream_read_only), Toast.LENGTH_SHORT).show();
    }
    //endregion
}
