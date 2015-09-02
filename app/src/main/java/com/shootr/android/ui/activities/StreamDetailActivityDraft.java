package com.shootr.android.ui.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.cocosw.bottomsheet.BottomSheet;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.StreamDetailAdapter;
import com.shootr.android.ui.base.BaseActivity;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.presenter.StreamDetailPresenter;
import com.shootr.android.ui.views.StreamDetailView;
import com.shootr.android.util.FileChooserUtils;
import com.shootr.android.util.ImageLoader;
import com.shootr.android.util.MenuItemValueHolder;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class StreamDetailActivityDraft extends BaseActivity implements StreamDetailView {

    private static final int REQUEST_EDIT_STREAM = 3;
    private static final int REQUEST_CHOOSE_PHOTO = 4;
    private static final int REQUEST_TAKE_PHOTO = 5;

    private static final String EXTRA_STREAM_ID = "streamId";
    private static final String EXTRA_STREAM_MEDIA_COUNT = "streamMediaCount";
    public static final String EXTRA_STREAM_SHORT_TITLE = "shortTitle";

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.cat_avatar) View streamPictureContainer;
    @Bind(R.id.stream_avatar) ImageView streamPicture;
    @Bind(R.id.stream_photo_edit_loading) View streamPictureLoading;
    @Bind(R.id.cat_title) TextView streamTitle;
    @Bind(R.id.subtitle) TextView streamSubtitle;

    @Bind(R.id.list) RecyclerView recyclerView;
    @Bind(R.id.loading_progress) View progressView;

    @Inject ImageLoader imageLoader;
    @Inject StreamDetailPresenter streamDetailPresenter;

    private StreamDetailAdapter adapter;
    private MenuItemValueHolder editMenuItem = new MenuItemValueHolder();

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_stream_detail_draft;
    }

    @Override
    protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        adapter = new StreamDetailAdapter(null, imageLoader);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void initializePresenter() {
        String idStream = getIntent().getStringExtra(EXTRA_STREAM_ID);
        streamDetailPresenter.initialize(this, idStream);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stream, menu);
        editMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_edit));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_edit:
                streamDetailPresenter.editStreamClick();
                return true;
            default:
                return false;
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_STREAM && resultCode == RESULT_OK) {
            streamDetailPresenter.resultFromEditStreamInfo();
        }else if (requestCode == REQUEST_EDIT_STREAM && resultCode == NewStreamActivity.RESULT_EXIT_STREAM) {
            setResult(NewStreamActivity.RESULT_EXIT_STREAM);
            finish();
        } else if (requestCode == REQUEST_CHOOSE_PHOTO && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            File photoFile = new File(FileChooserUtils.getPath(this, selectedImageUri));
            streamDetailPresenter.photoSelected(photoFile);
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            File photoFile = getCameraPhotoFile();
            streamDetailPresenter.photoSelected(photoFile);
        }
    }

    private void setShortTitleResultForPreviousActivity(String shortTitle) {
        setResult(RESULT_OK, new Intent().putExtra(EXTRA_STREAM_SHORT_TITLE, shortTitle));
    }

    //region Edit photo
    @OnClick(R.id.cat_avatar)
    public void onPhotoClick() {
        streamDetailPresenter.photoClick();
    }

    private void choosePhotoFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.photo_edit_gallery)),
          REQUEST_CHOOSE_PHOTO);
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
    @Override
    public void setStreamTitle(String title) {
        streamTitle.setText(title);
    }

    @Override
    public void setStreamShortTitle(String shortTitle) {
        setShortTitleResultForPreviousActivity(shortTitle);
    }

    @Override
    public void setStreamAuthor(String author) {
        adapter.setAuthorName(author);
    }

    @Override
    public void setStreamPicture(String picture) {
        imageLoader.loadStreamPicture(picture, streamPicture);
    }

    @Override
    public void showEditStreamPhotoOrInfo() {
        new BottomSheet.Builder(this).title(getString(R.string.stream_detail_edit_menu_title))
          .sheet(R.menu.stream_edit_photo_or_info)
          .listener(new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                  switch (which) {
                      case R.id.menu_stream_edit_photo:
                          streamDetailPresenter.editStreamPhoto();
                          break;
                      case R.id.menu_stream_edit_info:
                          streamDetailPresenter.editStreamInfo();
                          break;
                  }
              }
          })
          .show();
    }

    @Override
    public void showPhotoPicker() {
        new BottomSheet.Builder(this).title(R.string.change_photo)
          .sheet(R.menu.profile_photo_bottom_sheet)
          .listener(new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                  switch (which) {
                      case R.id.menu_photo_gallery:
                          choosePhotoFromGallery();
                          break;
                      case R.id.menu_photo_take:
                          takePhotoFromCamera();
                          break;
                  }
              }
          })
          .show();
    }

    @Override
    public void showEditPicturePlaceholder() {
        streamPicture.setImageResource(R.drawable.ic_stream_picture_edit);
    }

    @Override
    public void showLoadingPictureUpload() {
        streamPicture.setVisibility(View.GONE);
        streamPictureLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingPictureUpload() {
        streamPicture.setVisibility(View.VISIBLE);
        streamPictureLoading.setVisibility(View.GONE);
    }

    @Override
    public void zoomPhoto(String picture) {
        Bundle animationBundle = ActivityOptionsCompat.makeScaleUpAnimation(streamPictureContainer,
          streamPictureContainer.getLeft(),
          0,
          streamPictureContainer.getWidth(),
          streamPictureContainer.getBottom()).toBundle();
        Intent photoIntent = PhotoViewActivity.getIntentForActivity(this, picture);
        ActivityCompat.startActivity(this, photoIntent, animationBundle);
    }

    @Override
    public void setWatchers(List<UserModel> watchers) {
        adapter.setParticipants(watchers);
    }

    @Override
    public void setWatchersCount(int watchersCount) {
        if (watchersCount == 0) {
            streamSubtitle.setVisibility(View.GONE);
        } else {
            streamSubtitle.setVisibility(View.VISIBLE);
            streamSubtitle.setText(getString(R.string.stream_participants_following_number, watchersCount));
        }
    }

    @Override
    public void setCurrentUserWatching(UserModel userWatchingModel) {
        //TODO
    }

    @Override
    public void navigateToEditStream(String idStream) {
        //TODO getIntent
        Intent editIntent =
          new Intent(this, NewStreamActivity.class).putExtra(NewStreamActivity.KEY_STREAM_ID, idStream);
        startActivityForResult(editIntent, REQUEST_EDIT_STREAM);
    }

    @Override
    public void navigateToUser(String userId) {
        Intent userProfileIntent = ProfileContainerActivity.getIntent(this, userId);
        startActivity(userProfileIntent);
    }

    @Override
    public void showDetail() {
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEditStreamButton() {
        editMenuItem.setVisible(true);
    }

    @Override
    public void hideEditStreamButton() {
        editMenuItem.setVisible(false);
    }

    @Override
    public void setMediaCount(Integer mediaCount) {
        adapter.setMediaCount(mediaCount);
    }

    @Override
    public void navigateToMedia(String idStream, Integer streamMediaCount) {
        //TODO getintent
        Intent intent = new Intent(this, StreamMediaActivity.class);
        intent.putExtra(EXTRA_STREAM_ID, idStream);
        intent.putExtra(EXTRA_STREAM_MEDIA_COUNT, streamMediaCount);
        this.startActivity(intent);
    }

    @Override
    public void setStreamDescription(String description) {
        adapter.setDescription(description);
    }

    @Override
    public void hideStreamDescription() {
        adapter.setDescription(null);
    }

    @Override
    public void showLoading() {
        progressView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressView.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    //endregion
}
