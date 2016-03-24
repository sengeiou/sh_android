package com.shootr.mobile.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cocosw.bottomsheet.BottomSheet;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.StreamDetailAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnFollowUnfollowListener;
import com.shootr.mobile.ui.adapters.listeners.OnUserClickListener;
import com.shootr.mobile.ui.base.BaseActivity;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.StreamDetailPresenter;
import com.shootr.mobile.ui.views.StreamDetailView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.CrashReportTool;
import com.shootr.mobile.util.CustomContextMenu;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.FileChooserUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.IntentFactory;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.MenuItemValueHolder;
import com.shootr.mobile.util.WritePermissionManager;
import com.sloydev.collapsingavatartoolbar.CollapsingAvatarToolbar;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class StreamDetailActivity extends BaseActivity implements StreamDetailView {

    private static final int REQUEST_EDIT_STREAM = 3;
    private static final int REQUEST_CHOOSE_PHOTO = 0;
    private static final int REQUEST_TAKE_PHOTO = 5;

    private static final String EXTRA_STREAM_ID = "streamId";
    public static final String EXTRA_STREAM_SHORT_TITLE = "shortTitle";
    private int counterToolbarPrintTimes = 0;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbar_dummy_content) View toolbarDummyContent;
    @Bind(R.id.collapsing_avatar_toolbar) CollapsingAvatarToolbar collapsingAvatarToolbar;
    @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
    @Bind(R.id.stream_title_container) View streamTitleContainer;
    @Bind(R.id.cat_avatar) View streamPictureContainer;
    @Bind(R.id.stream_avatar) ImageView streamPicture;
    @Bind(R.id.image_toolbar_detail_stream) ImageView toolbarImage;
    @Bind(R.id.stream_photo_edit_loading) View streamPictureLoading;
    @Bind(R.id.cat_title) TextView streamTitle;
    @Bind(R.id.subtitle) TextView streamSubtitle;
    @Bind(R.id.blurLayout) FrameLayout blurLayout;

    @Bind(R.id.list) RecyclerView recyclerView;
    @Bind(R.id.loading_progress) View progressView;

    @BindString(R.string.shared_stream_notification) String streamNotification;
    @BindString(R.string.analytics_screen_stream_detail) String analyticsScreenStreamDetail;

    @Inject ImageLoader imageLoader;
    @Inject StreamDetailPresenter streamDetailPresenter;
    @Inject IntentFactory intentFactory;
    @Inject FeedbackMessage feedbackMessage;
    @Inject AnalyticsTool analyticsTool;
    @Inject WritePermissionManager writePermissionManager;
    @Inject CrashReportTool crashReportTool;

    private StreamDetailAdapter adapter;
    private MenuItemValueHolder editMenuItem = new MenuItemValueHolder();
    private MenuItemValueHolder dataInfoMenuItem = new MenuItemValueHolder();
    private MenuItemValueHolder contributorsMenuItem = new MenuItemValueHolder();

    public static Intent getIntent(Context context, String streamId) {
        Intent intent = new Intent(context, StreamDetailActivity.class);
        intent.putExtra(EXTRA_STREAM_ID, streamId);
        return intent;
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_stream_detail;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        analyticsTool.analyticsStart(getBaseContext(), analyticsScreenStreamDetail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
        writePermissionManager.init(this);

        adapter = new StreamDetailAdapter(imageLoader, //
          new View.OnClickListener() {
              @Override public void onClick(View v) {
                  streamDetailPresenter.clickAuthor();
              }
          }, // author
          new View.OnClickListener() {
              @Override public void onClick(View v) {
                  streamDetailPresenter.clickMedia();
              }
          }, // media
          new CompoundButton.OnCheckedChangeListener() {
              @Override public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                  if (isChecked) {
                      streamDetailPresenter.onMuteChecked();
                  } else {
                      streamDetailPresenter.onUnmuteChecked();
                  }
              }
          }, new View.OnClickListener() {
            @Override public void onClick(View view) {
                streamDetailPresenter.clickAllParticipants();
            }
        }, // all participants
          new OnUserClickListener() {
              @Override public void onUserClick(String idUser) {
                  navigateToUser(idUser);
              }
          }, // participant
          new OnFollowUnfollowListener() {
              @Override public void onFollow(UserModel user) {
                  streamDetailPresenter.follow(user.getIdUser());
              }

              @Override public void onUnfollow(final UserModel user) {
                  new AlertDialog.Builder(StreamDetailActivity.this)
                    .setMessage(String.format(getString(R.string.unfollow_dialog_message),
                    user.getUsername()))
                    .setPositiveButton(getString(R.string.unfollow_dialog_yes), new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            streamDetailPresenter.unfollow(user.getIdUser());
                        }
                    })
                    .setNegativeButton(getString(R.string.unfollow_dialog_no), null)
                    .create()
                    .show();
              }
          }); //follow
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @OnClick(R.id.stream_share_button) public void onShareClick() {
        openContextualMenuForSharing();
    }

    private void openContextualMenuForSharing() {
        new CustomContextMenu.Builder(this).addAction(R.string.share_via_shootr, new Runnable() {
            @Override public void run() {
                streamDetailPresenter.shareStreamViaShootr();
            }
        }).addAction(R.string.share_via, new Runnable() {
            @Override public void run() {
                streamDetailPresenter.shareStreamVia();
            }
        }).show();
    }

    @Override protected void initializePresenter() {
        String idStream = getIntent().getStringExtra(EXTRA_STREAM_ID);
        streamDetailPresenter.initialize(this, idStream);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stream, menu);
        editMenuItem.bindRealMenuItem(menu.findItem(R.id.stream_detail_menu_edit));
        contributorsMenuItem.bindRealMenuItem(menu.findItem(R.id.stream_detail_contributors));
        dataInfoMenuItem.bindRealMenuItem(menu.findItem(R.id.stream_detail_menu_data_info));
        dataInfoMenuItem.setVisible(true);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.stream_detail_menu_edit:
                streamDetailPresenter.editStreamClick();
                return true;
            case R.id.stream_detail_menu_data_info:
                streamDetailPresenter.dataInfoClicked();
                return true;
            case R.id.stream_detail_contributors:
                streamDetailPresenter.contributorsClicked();
                return true;
            default:
                return false;
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_STREAM && resultCode == RESULT_OK) {
            streamDetailPresenter.resultFromEditStreamInfo();
        } else if (requestCode == REQUEST_EDIT_STREAM && resultCode == NewStreamActivity.RESULT_EXIT_STREAM) {
            setResult(NewStreamActivity.RESULT_EXIT_STREAM);
            finish();
        } else if (requestCode == REQUEST_CHOOSE_PHOTO && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            try {
                File photoFile = new File(FileChooserUtils.getPath(this, selectedImageUri));
                streamDetailPresenter.photoSelected(photoFile);
            } catch (NullPointerException error) {
                feedbackMessage.show(getView(), R.string.error_message_invalid_image);
            }
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            File photoFile = getCameraPhotoFile();
            streamDetailPresenter.photoSelected(photoFile);
        }
    }

    @Override protected void onResume() {
        super.onResume();
        streamDetailPresenter.resume();
    }

    @Override protected void onPause() {
        super.onPause();
        streamDetailPresenter.pause();
        analyticsTool.analyticsStop(getBaseContext(), this);
    }

    private void setShortTitleResultForPreviousActivity(String shortTitle) {
        setResult(RESULT_OK, new Intent().putExtra(EXTRA_STREAM_SHORT_TITLE, shortTitle));
    }

    //region Edit photo
    @OnClick(R.id.cat_avatar) public void onPhotoClick() {
        streamDetailPresenter.photoClick();
    }

    private void choosePhotoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CHOOSE_PHOTO);
    }

    private void takePhotoFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File pictureTemporaryFile = getCameraPhotoFile();
        if (!pictureTemporaryFile.exists()) {
            try {
                pictureTemporaryFile.getParentFile().mkdirs();
                pictureTemporaryFile.createNewFile();
            } catch (IOException e) {
                Timber.e(e, "No se pudo crear el archivo temporal para la foto de perfil");
                //TODO cancelar operación y avisar al usuario
            }
        }
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(pictureTemporaryFile));
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
    }

    private File getCameraPhotoFile() {
        return new File(getExternalFilesDir("tmp"), "streamUpload.jpg");
    }
    //endregion

    //region View methods
    @Override public void setStreamTitle(String title) {
        streamTitle.setText(title);
    }

    @Override public void setStreamShortTitle(String shortTitle) {
        setShortTitleResultForPreviousActivity(shortTitle);
    }

    @Override public void setStreamAuthor(String author) {
        adapter.setAuthorName(author);
    }

    @Override public void setStreamPicture(String picture) {

        imageLoader.loadStreamPicture(picture, streamPicture);
        imageLoader.loadBlurStreamPicture(picture, toolbarImage, new RequestListener<String, GlideDrawable>() {
            @Override public boolean onException(Exception e, String model, Target<GlideDrawable> target,
              boolean isFirstResource) {
                return false;
            }

            @Override public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
              boolean isFromMemoryCache, boolean isFirstResource) {

                if (counterToolbarPrintTimes == 0) {
                    changeToolbarColor();
                    counterToolbarPrintTimes++;
                }
                return false;
            }
        });
    }

    private void changeToolbarColor() {
        try {
            blurLayout.setBackgroundColor(getResources().getColor(R.color.gray_40));
            streamPicture.buildDrawingCache();
            Bitmap bitmap = streamPicture.getDrawingCache();
            Palette palette = Palette.from(bitmap).generate();
            collapsingToolbar.setContentScrimColor(getDarkVibrantColor(palette));
            collapsingToolbar.setStatusBarScrimColor(getDarkVibrantColor(palette));
            changeStatusBarColor(palette);
        } catch (IllegalArgumentException exception) {
            crashReportTool.logException("IllegalArgumentException. Bitmap is not valid");
        }
    }

    private int getDarkVibrantColor(Palette palette) {
        return palette.getDarkVibrantColor(getResources().getColor(R.color.gray_material));
    }

    private void changeStatusBarColor(Palette palette) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getDarkVibrantColor(palette));
        }
    }

    @Override public void showEditStreamPhotoOrInfo() {
        new BottomSheet.Builder(this).title(getString(R.string.stream_detail_edit_menu_title))
          .sheet(R.menu.stream_edit_photo_or_info)
          .listener(new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  switch (which) {
                      case R.id.menu_stream_edit_photo:
                          streamDetailPresenter.editStreamPhoto();
                          break;
                      case R.id.menu_stream_edit_info:
                          streamDetailPresenter.editStreamInfo();
                          break;
                      default:
                          break;
                  }
              }
          })
          .show();
    }

    @Override public void showPhotoPicker() {
        new BottomSheet.Builder(this).title(R.string.change_photo)
          .sheet(R.menu.profile_photo_bottom_sheet)
          .listener(new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  switch (which) {
                      case R.id.menu_photo_gallery:
                          handlePhotoSelectionFromGallery();
                          break;
                      case R.id.menu_photo_take:
                          takePhotoFromCamera();
                          break;
                      default:
                          break;
                  }
              }
          })
          .show();
    }

    public void handlePhotoSelectionFromGallery() {
        if (writePermissionManager.hasWritePermission()) {
            choosePhotoFromGallery();
        } else {
            writePermissionManager.requestWritePermissionToUser();
        }
    }

    @Override public void showEditPicturePlaceholder() {
        streamPicture.setImageResource(R.drawable.ic_stream_picture_edit);
    }

    @Override public void showLoadingPictureUpload() {
        streamPicture.setVisibility(View.GONE);
        streamPictureLoading.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoadingPictureUpload() {
        streamPicture.setVisibility(View.VISIBLE);
        streamPictureLoading.setVisibility(View.GONE);
    }

    @Override public void zoomPhoto(String picture) {
        Bundle animationBundle = ActivityOptionsCompat.makeScaleUpAnimation(streamPictureContainer,
          streamPictureContainer.getLeft(),
          streamPictureContainer.getTop(),
          streamPictureContainer.getWidth(),
          streamPictureContainer.getBottom()).toBundle();
        Intent photoIntent = PhotoViewActivity.getIntentForActivity(this, picture);
        ActivityCompat.startActivity(this, photoIntent, animationBundle);
    }

    @Override public void setWatchers(List<UserModel> watchers) {
        adapter.setParticipants(watchers);
        adapter.notifyDataSetChanged();
    }

    @Override public void setCurrentUserWatching(UserModel userWatchingModel) {
        //TODO
    }

    @Override public void navigateToEditStream(String idStream) {
        Intent editIntent =
          new Intent(this, NewStreamActivity.class).putExtra(NewStreamActivity.KEY_STREAM_ID, idStream);
        startActivityForResult(editIntent, REQUEST_EDIT_STREAM);
    }

    @Override public void navigateToUser(String userId) {
        Intent userProfileIntent = ProfileContainerActivity.getIntent(this, userId);
        startActivity(userProfileIntent);
    }

    @Override public void showDetail() {
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override public void showEditStreamButton() {
        editMenuItem.setVisible(true);
        collapsingAvatarToolbar.setCollapseChangedListener(new CollapsingAvatarToolbar.CollapseChangedListener() {

            boolean needsToAdjustPadding = true;

            @Override public void onCollapseChanged(float collapseProgress) {
                if (collapseProgress == 0f) {
                    setPaddingRight(0);
                    needsToAdjustPadding = true;
                } else if (needsToAdjustPadding) {
                    int paddingRight = getExtraPaddingForMenu();
                    setPaddingRight(paddingRight);
                    needsToAdjustPadding = false;
                }
            }

            protected int getExtraPaddingForMenu() {
                int limit = toolbarDummyContent.getRight();
                int currRight = streamTitleContainer.getRight();
                return currRight - limit;
            }

            protected void setPaddingRight(int paddingRight) {
                streamTitleContainer.setPadding(0, 0, paddingRight, 0);
            }
        });
    }

    @Override public void navigateToMedia(String idStream, Integer streamMediaCount) {
        Intent intent = StreamMediaActivity.newIntent(this, idStream, streamMediaCount);
        this.startActivity(intent);
    }

    @Override public void setStreamDescription(String description) {
        adapter.setDescription(description);
    }

    @Override public void hideStreamDescription() {
        adapter.setDescription(null);
    }

    @Override public void showAllParticipantsButton() {
        adapter.showAllParticipants();
    }

    @Override public void setFollowingNumber(Integer numberOfFollowing, Integer totalWatchers) {
        if (numberOfFollowing == 0) {
            streamSubtitle.setVisibility(View.VISIBLE);
            streamSubtitle.setText(getResources().getQuantityString(R.plurals.total_watchers_pattern,
              totalWatchers,
              totalWatchers));
        } else {
            streamSubtitle.setVisibility(View.VISIBLE);
            loadSubtitle(numberOfFollowing, totalWatchers);
        }
    }

    public void loadSubtitle(Integer numberOfFollowing, Integer totalWatchers) {
        if (numberOfFollowing <= 0) {
            if (totalWatchers == 1) {
                String participants =
                  getResources().getQuantityString(R.plurals.total_watchers_pattern, totalWatchers, totalWatchers);
                streamSubtitle.setText(participants);
            } else {
                String participants =
                  getResources().getQuantityString(R.plurals.total_watchers_pattern, totalWatchers, totalWatchers);
                streamSubtitle.setText(participants);
            }
        } else {
            streamSubtitle.setText(getString(R.string.stream_subtitle_pattern_multiple_participants,
              numberOfFollowing,
              totalWatchers));
        }
    }

    @Override public void showStreamShared() {
        feedbackMessage.show(getView(), streamNotification);
    }

    @Override public void shareStreamVia(StreamModel stream) {
        Intent shareIntent = intentFactory.shareStreamIntent(this, stream);
        Intents.maybeStartActivity(this, shareIntent);
    }

    @Override public void goToAllParticipants(String idStream) {
        startActivity(AllParticipantsActivity.newIntent(this, idStream));
    }

    @Override public void setMuteStatus(Boolean isChecked) {
        adapter.setMuteStatus(isChecked);
    }

    @Override public void goToStreamDataInfo(StreamModel streamModel) {
        Intent intent = new Intent(this, StreamDataInfoActivity.class);
        intent.putExtra(StreamDataInfoActivity.ARGUMENT_PARTICIPANTS_NUMBER, streamModel.getHistoricWatchers());
        intent.putExtra(StreamDataInfoActivity.ARGUMENT_SHOTS_NUMBER, streamModel.getTotalShots());
        intent.putExtra(StreamDataInfoActivity.ARGUMENT_FAVORITES_NUMBER, streamModel.getTotalFavorites());
        intent.putExtra(StreamDataInfoActivity.ARGUMENT_UNIQUE_SHOTS, streamModel.getUniqueShots());
        intent.putExtra(StreamDataInfoActivity.ARGUMENT_STREAM_NAME, streamModel.getTitle());
        startActivity(intent);
    }

    @Override public void showContributorsButton() {
        contributorsMenuItem.setVisible(true);
    }

    @Override public void goToContributorsActivity(String idStream) {
        startActivity(ContributorsActivity.newIntent(this, idStream));
    }

    @Override public void showLoading() {
        progressView.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoading() {
        progressView.setVisibility(View.GONE);
    }

    @Override public void showError(String message) {
        feedbackMessage.show(getView(), message);
    }
    //endregion
}
