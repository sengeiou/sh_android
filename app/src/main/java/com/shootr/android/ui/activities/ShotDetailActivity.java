package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.TimeFormatter;
import java.io.File;
import javax.inject.Inject;
import timber.log.Timber;

public class ShotDetailActivity extends BaseToolbarDecoratedActivity implements ShotDetailView, NewShotBarView {

    public static final String EXTRA_SHOT = "shot";

    @InjectView(R.id.shot_detail_list) RecyclerView detailList;
    @InjectView(R.id.shot_bar_text) TextView replyPlaceholder;
    @InjectView(R.id.shot_bar_drafts) View replyDraftsButton;

    @Inject PicassoWrapper picasso;
    @Inject TimeFormatter timeFormatter;
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

    private void setupAdapter() {
        detailAdapter = new ShotDetailWithRepliesAdapter(picasso, timeFormatter, getResources());
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
                    //TODO reply
                  .withImage(image) //
                  .build();
                startActivity(newShotIntent);
            }
        };
    }

    private ShotModel extractShotFromIntent() {
        return ((ShotModel) getIntent().getSerializableExtra(EXTRA_SHOT));
    }

    private void initializePresenter(ShotModel shotModel) {
        detailPresenter.initialize(this, shotModel);
        newShotBarPresenter.initialize(this);
    }

    //region Listeners
    public void onImageClick() {
        detailPresenter.imageClick();
    }

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
    //endregion

    //region View methods
    @Override public void renderShot(ShotModel shotModel) {
        detailAdapter.renderMainShot(shotModel);
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
