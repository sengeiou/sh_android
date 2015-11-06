package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.dagger.TemporaryFilesDir;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.ShotDetailWithRepliesAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.component.PhotoPickerController;
import com.shootr.mobile.ui.fragments.NewShotBarViewDelegate;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.presenter.NewShotBarPresenter;
import com.shootr.mobile.ui.presenter.ShotDetailPresenter;
import com.shootr.mobile.ui.views.NewShotBarView;
import com.shootr.mobile.ui.views.ShotDetailView;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.Clipboard;
import com.shootr.mobile.util.CustomContextMenu;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.IntentFactory;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.TimeFormatter;
import java.io.File;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class ShotDetailActivity extends BaseToolbarDecoratedActivity implements ShotDetailView, NewShotBarView {

    public static final String EXTRA_SHOT = "shot";

    @Bind(com.shootr.mobile.R.id.shot_detail_list) RecyclerView detailList;
    @Bind(com.shootr.mobile.R.id.detail_new_shot_bar) View newShotBar;
    @Bind(R.id.shot_bar_text) TextView replyPlaceholder;
    @Bind(com.shootr.mobile.R.id.shot_bar_drafts) View replyDraftsButton;
    @BindString(com.shootr.mobile.R.string.shot_shared_message) String shotShared;
    @BindString(com.shootr.mobile.R.string.user_not_found_message) String userNotFoundMessage;

    @Inject ImageLoader imageLoader;
    @Inject TimeFormatter timeFormatter;
    @Inject AndroidTimeUtils timeUtils;
    @Inject ShotDetailPresenter detailPresenter;
    @Inject NewShotBarPresenter newShotBarPresenter;
    @Inject IntentFactory intentFactory;
    @Inject FeedbackMessage feedbackMessage;
    @Inject @TemporaryFilesDir File tmpFiles;

    private PhotoPickerController photoPickerController;
    private NewShotBarViewDelegate newShotBarViewDelegate;
    private ShotDetailWithRepliesAdapter detailAdapter;

    public static Intent getIntentForActivity(Context context, ShotModel shotModel) {
        Intent intent = new Intent(context, ShotDetailActivity.class);
        intent.putExtra(EXTRA_SHOT, shotModel);
        return intent;
    }

    @Override protected int getLayoutResource() {
        return com.shootr.mobile.R.layout.activity_shot_detail;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.shootr.mobile.R.menu.menu_shot_detail, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }else if (item.getItemId() == com.shootr.mobile.R.id.menu_share) {
            ShotModel shotModel = extractShotFromIntent();
            openContextualMenu(shotModel);
        }else if (item.getItemId() == R.id.menu_copy_text) {
            Clipboard.copyShotComment(this, extractShotFromIntent());
        }
        return super.onOptionsItemSelected(item);
    }

    private void openContextualMenu(final ShotModel shotModel) {
        new CustomContextMenu.Builder(this)
          .addAction(com.shootr.mobile.R.string.menu_share_shot_via_shootr, new Runnable() {
              @Override public void run() {
                  detailPresenter.shareShot(shotModel);
              }
          })
          .addAction(R.string.menu_share_shot_via, new Runnable() {
              @Override public void run() {
                  shareShot(shotModel);
              }
          })
          .show();
    }

    private void shareShot(ShotModel shotModel) {
        Intent shareIntent = intentFactory.shareShotIntent(this, shotModel);
        Intents.maybeStartActivity(this, shareIntent);
    }

    private void setupAdapter() {
        detailAdapter = new ShotDetailWithRepliesAdapter(
          imageLoader, new ShotDetailWithRepliesAdapter.AvatarClickListener() {
              @Override
              public void onClick(String userId) {
                  onShotAvatarClick(userId);
              }
          }, //
          new ShotDetailWithRepliesAdapter.ImageClickListener() {
              @Override
              public void onClick(ShotModel shot) {
                  onShotImageClick(shot);
              }
          }, //
          new OnVideoClickListener() {
              @Override
              public void onVideoClick(String url) {
                  onShotVideoClick(url);
              }
          }, //
          new OnUsernameClickListener() {
              @Override
              public void onUsernameClick(String username) {
                  onShotUsernameClick(username);
              }
          }, //
          new ShotDetailWithRepliesAdapter.OnParentShownListener() {
              @Override
              public void onShown() {
                  detailList.scrollToPosition(0);
              }
          }, //
          new OnNiceShotListener() {
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
        photoPickerController = new PhotoPickerController.Builder().onActivity(this)
          .withTemporaryDir(tmpFiles)
          .withHandler(new PhotoPickerController.Handler() {
              @Override
              public void onSelected(File imageFile) {
                  newShotBarPresenter.newShotImagePicked(imageFile);
              }

              @Override
              public void onError(Exception e) {
                  Timber.e(e, "Error selecting image");
              }

              @Override
              public void startPickerActivityForResult(Intent intent, int requestCode) {
                  startActivityForResult(intent, requestCode);
              }
          })
          .build();
    }

    private void setupNewShotBarDelegate(final ShotModel shotModel) {
        newShotBarViewDelegate = new NewShotBarViewDelegate(photoPickerController, replyDraftsButton,
          feedbackMessage) {
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
        };
    }

    private ShotModel extractShotFromIntent() {
        return (ShotModel) getIntent().getSerializableExtra(EXTRA_SHOT);
    }

    private void initializePresenter(ShotModel shotModel) {
        detailPresenter.initialize(this, shotModel);
        newShotBarPresenter.initialize(this, shotModel.getStreamId());
    }

    //region Listeners
    public void onShotImageClick(ShotModel shot) {
        detailPresenter.imageClick(shot);
    }

    public void onShotAvatarClick(String userId) {
        detailPresenter.avatarClick(userId);
    }

    public void onShotUsernameClick(String username){
        detailPresenter.usernameClick(username);
    }


    private void onShotVideoClick(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @OnClick(com.shootr.mobile.R.id.shot_bar_text) public void onReplyClick() {
        newShotBarPresenter.newShotFromTextBox();
    }


    @OnClick(com.shootr.mobile.R.id.shot_bar_photo)
    public void onStartNewShotWithPhoto() {
        newShotBarPresenter.newShotFromImage();
    }

    @OnClick(com.shootr.mobile.R.id.shot_bar_drafts) public void openDrafts() {
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
        replyPlaceholder.setText(getString(com.shootr.mobile.R.string.reply_placeholder_pattern, username));
    }

    @Override public void scrollToBottom() {
        detailList.smoothScrollToPosition(detailAdapter.getItemCount() - 1);
    }

    @Override public void renderParent(ShotModel parentShot) {
        detailAdapter.renderParentShot(parentShot);
    }

    @Override
    public void startProfileContainerActivity(String username) {
        Intent intentForUser = ProfileContainerActivity.getIntentWithUsername(this, username);
        startActivity(intentForUser);
    }

    @Override
    public void showError(String errorMessage) {
        feedbackMessage.show(getView(), errorMessage);
    }

    @Override public void showShotShared() {
        feedbackMessage.show(getView(), shotShared);
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

    //endregion
}
