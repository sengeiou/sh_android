package com.shootr.mobile.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.cocosw.bottomsheet.BottomSheet;
import com.shootr.mobile.BuildConfig;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.StreamReadWriteModeAdapter;
import com.shootr.mobile.ui.presenter.NewStreamPresenter;
import com.shootr.mobile.ui.views.NewStreamView;
import com.shootr.mobile.ui.widgets.AvatarView;
import com.shootr.mobile.ui.widgets.FloatLabelLayout;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.CrashReportTool;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.MenuItemValueHolder;
import com.shootr.mobile.util.WritePermissionManager;
import java.io.File;
import java.io.IOException;
import javax.inject.Inject;
import timber.log.Timber;

public class NewStreamActivity extends BaseToolbarDecoratedActivity implements NewStreamView {

  public static final int RESULT_EXIT_STREAM = 3;
  private static final int REQUEST_CHOOSE_PHOTO = 0;
  private static final int REQUEST_TAKE_PHOTO = 5;
  private static final int REQUEST_CROP_PHOTO = 88;
  public static final String KEY_STREAM_ID = "stream_id";
  public static final String SOURCE = "source";

  private static final String EXTRA_EDITED_TITLE = "title";

  @Inject NewStreamPresenter presenter;
  @Inject FeedbackMessage feedbackMessage;
  @Inject AnalyticsTool analyticsTool;
  @Inject SessionRepository sessionRepository;
  @Inject CrashReportTool crashReportTool;
  @Inject WritePermissionManager writePermissionManager;
  @Inject ImageLoader imageLoader;

  @BindView(R.id.new_stream_video_url) EditText videoUrlView;
  @BindView(R.id.new_stream_title) EditText titleView;
  @BindView(R.id.new_stream_title_label) FloatLabelLayout titleLabelView;
  @BindView(R.id.new_stream_title_error) TextView titleErrorView;
  @BindView(R.id.new_stream_description) EditText descriptionView;
  @BindView(R.id.stream_read_write_mode) AppCompatSpinner readWriteModeSpinner;
  @BindView(R.id.cat_avatar) View streamPictureContainer;
  @BindView(R.id.stream_avatar) AvatarView streamPhoto;

  @BindString(R.string.activity_edit_stream_title) String editStreamTitleActionBar;
  @BindString(R.string.activity_new_stream_title) String newStreamTitleActionBar;
  @BindString(R.string.analytics_action_create_stream) String analyticsActionCreateStream;
  @BindString(R.string.analytics_label_create_stream) String analyticsLabelCreateStream;

  private MenuItemValueHolder doneMenuItem = new MenuItemValueHolder();
  private StreamReadWriteModeAdapter spinnerAadapter;

  static {
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
  }

  public static Intent newIntent(Context context, String idStream) {
    Intent launchIntent = new Intent(context, NewStreamActivity.class);
    launchIntent.putExtra(NewStreamActivity.KEY_STREAM_ID, idStream);
    return launchIntent;
  }

  //region Initialization
  @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_new_stream;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    writePermissionManager.init(this);
    String idStreamToEdit = getIntent().getStringExtra(KEY_STREAM_ID);

    setupActionbar(idStreamToEdit);
    setupStatusBarColor();

    if (savedInstanceState != null) {
      String editedTitle = savedInstanceState.getString(EXTRA_EDITED_TITLE);
      titleView.setText(editedTitle);
    }
    setupTextViews();
    setupSpinner();
  }

  private void setupSpinner() {
    spinnerAadapter = new StreamReadWriteModeAdapter(this, R.layout.item_spinner_read_write_mode);
    readWriteModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        spinnerAadapter.setSelectedItem(position);
      }

      @Override public void onNothingSelected(AdapterView<?> adapterView) {
                /* no-op */
      }
    });

    readWriteModeSpinner.setAdapter(spinnerAadapter);
  }

  @Override protected void initializePresenter() {
    String idStreamToEdit = getIntent().getStringExtra(KEY_STREAM_ID);
    initializePresenter(idStreamToEdit);
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(EXTRA_EDITED_TITLE, titleView.getText().toString());
  }

  private void setupTextViews() {
    titleView.addTextChangedListener(new TextWatcher() {
      @Override public void afterTextChanged(Editable s) {
        presenter.titleTextChanged(s.toString());
        resetTitleError();
      }

      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /* no-op */
      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                /* no-op */
      }
    });
  }

  private void setupActionbar(String idStreamToEdit) {
    ActionBar actionBar = getSupportActionBar();
    actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setDisplayShowHomeEnabled(false);

    if (idStreamToEdit != null) {
      getToolbarDecorator().setTitle(editStreamTitleActionBar);
    } else {
      getToolbarDecorator().setTitle(newStreamTitleActionBar);
    }
  }

  private void setupStatusBarColor() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = getWindow();
      window.setStatusBarColor(getResources().getColor(R.color.primary_dark));
    }
  }

  private void initializePresenter(String idStreamToEdit) {
    presenter.initialize(this, idStreamToEdit);
  }
  //endregion

  //region Activity methods
  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.new_stream, menu);
    doneMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_done));
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    } else if (item.getItemId() == R.id.menu_done) {
      presenter.done(getStreamTitle(), getStreamDescription(), getStreamMode(), getVideoUrl());
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }
  //endregion

  //region View Methods

  @Override public void setStreamTitle(String title) {
    titleLabelView.showLabelWithoutAnimation();
    titleView.setText(title);
  }

  @Override public void showTitleError(String errorMessage) {
    titleErrorView.setText(errorMessage);
  }

  @Override public void closeScreenWithResult(String streamId) {
    setResult(RESULT_OK, new Intent() //
        .putExtra(KEY_STREAM_ID, streamId));
    sendAnalytics(streamId, getStreamTitle());
    finish();
  }

  @Override public void doneButtonEnabled(boolean enable) {
    doneMenuItem.setEnabled(enable);
  }

  @Override public void hideKeyboard() {
    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(titleView.getWindowToken(), 0);
  }

  @Override public void showNotificationConfirmation() {
    new AlertDialog.Builder(this).setMessage(
        getString(R.string.stream_notification_confirmation_message))
        .setPositiveButton(getString(R.string.stream_notification_confirmation_yes),
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                presenter.confirmNotify(getStreamTitle(), getStreamDescription(), getStreamMode(),
                    true);
              }
            })
        .setNegativeButton(getString(R.string.stream_notification_confirmation_no),
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                presenter.confirmNotify(getStreamTitle(), getStreamDescription(), getStreamMode(),
                    false);
              }
            })
        .create()
        .show();
  }

  private void sendAnalytics(String streamId, String streamName) {
    String source = getIntent().getStringExtra(SOURCE);
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsActionCreateStream);
    builder.setLabelId(analyticsLabelCreateStream);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setStreamName(streamName);
    builder.setIdStream(streamId);
    if (source != null) {
      builder.setSource(source);
    }
    analyticsTool.analyticsSendAction(builder);
  }

  @Override public void showDescription(String description) {
    descriptionView.setText(description);
  }

  @Override public void setModeValue(Integer readWriteMode) {
    readWriteModeSpinner.setSelection(readWriteMode);
  }

  @Override public void showPhotoOptions() {
    new BottomSheet.Builder(this).title(R.string.title_menu_photo)
        .sheet(R.menu.photo_options_bottom_sheet_new_stream)
        .listener(new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            switch (which) {
              case R.id.menu_photo_view:
                presenter.viewPhotoClicked();
                break;
              case R.id.menu_photo_gallery:
                handlePhotoSelectionFromGallery();
                break;
              case R.id.menu_photo_take:
                takePhotoFromCamera();
                break;
              case R.id.menu_photo_remove:
                presenter.removePhoto();
                break;
              default:
                break;
            }
          }
        })
        .show();
  }

  @Override public void showPhotoPicker() {
    new BottomSheet.Builder(this).title(R.string.title_menu_photo)
        .sheet(R.menu.photo_picker_bottom_sheet_new_stream)
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

  @Override public void zoomPhoto(String picture) {
    Bundle animationBundle = ActivityOptionsCompat.makeScaleUpAnimation(streamPictureContainer,
        streamPictureContainer.getLeft(), streamPictureContainer.getTop(),
        streamPictureContainer.getWidth(), streamPictureContainer.getBottom()).toBundle();
    Intent photoIntent = PhotoViewActivity.getIntentForActivity(this, picture);
    ActivityCompat.startActivity(this, photoIntent, animationBundle);
  }

  @Override public void setStreamPhoto(String picture, String title) {
    imageLoader.loadProfilePhoto(picture, streamPhoto, title);
  }

  @Override public void loadDefaultPhoto() {
    imageLoader.loadStreamPicture(null, streamPhoto);
  }

  @Override public void showPhotoSelected(File photoFile) {
    imageLoader.load(photoFile, streamPhoto);
  }

  @Override public void goToShareStream(String id) {
    Intent intent = ShareStreamActivity.newIntent(this, id);
    startActivity(intent);
    finish();
  }

  @Override public void showEditPhotoPlaceHolder() {
    streamPhoto.setImageResource(R.drawable.ic_stream_picture_edit);
  }

  @Override public void showLoading() {
    doneMenuItem.setActionView(R.layout.item_list_loading);
  }

  @Override public void hideLoading() {
    doneMenuItem.setActionView(null);
  }

  @Override public void showError(String message) {
    feedbackMessage.show(getView(), message);
  }

  private String getStreamTitle() {
    return titleView.getText().toString();
  }

  private String getVideoUrl() {
    return videoUrlView.getText().toString();
  }

  private String getStreamDescription() {
    return descriptionView.getText().toString();
  }

  private void resetTitleError() {
    titleErrorView.setError(null);
  }

  private Integer getStreamMode() {
    return readWriteModeSpinner.getSelectedItemPosition();
  }

  //endregion

  @OnClick(R.id.cat_avatar) public void onPhotoClick() {
    presenter.photoClick();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CHOOSE_PHOTO && resultCode == Activity.RESULT_OK) {
      cropGalleryPicture(data);
    } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
      cropCameraPicture();
    } else if (requestCode == REQUEST_CROP_PHOTO && resultCode == Activity.RESULT_OK) {
      File photoFile = getCameraPhotoFile();
      presenter.photoSelected(photoFile);
    }
  }

  private void cropCameraPicture() {
    Intent intent = new Intent(this, CropPictureActivity.class);
    intent.putExtra(CropPictureActivity.EXTRA_PHOTO_TYPE, true);
    intent.putExtra(CropPictureActivity.EXTRA_URI, "");
    intent.putExtra(CropPictureActivity.EXTRA_IMAGE_NAME, "cropUpload.jpg");
    startActivityForResult(intent, REQUEST_CROP_PHOTO);
  }

  private void cropGalleryPicture(Intent data) {
    Uri selectedImageUri = data.getData();
    Intent intent = new Intent(this, CropPictureActivity.class);
    intent.putExtra(CropPictureActivity.EXTRA_PHOTO_TYPE, false);
    intent.putExtra(CropPictureActivity.EXTRA_IMAGE_NAME, "cropUpload.jpg");
    intent.putExtra(CropPictureActivity.EXTRA_URI, selectedImageUri.toString());
    startActivityForResult(intent, REQUEST_CROP_PHOTO);
  }

  private File getCameraPhotoFile() {
    File photoFile = new File(getExternalFilesDir("tmp"), "cropUpload.jpg");
    if (!photoFile.exists()) {
      try {
        photoFile.getParentFile().mkdirs();
        photoFile.createNewFile();
      } catch (IOException e) {
        Timber.e(e, "No se pudo crear el archivo temporal para la foto de perfil");
        throw new IllegalStateException(e);
      }
    }
    return photoFile;
  }

  public void handlePhotoSelectionFromGallery() {
    if (writePermissionManager.hasWritePermission()) {
      choosePhotoFromGallery();
    } else {
      writePermissionManager.requestWritePermissionToUser();
    }
  }

  private void takePhotoFromCamera() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    File pictureTemporaryFile = getCameraPhotoFile();
    if (!pictureTemporaryFile.exists()) {
      try {
        pictureTemporaryFile.getParentFile().mkdirs();
        pictureTemporaryFile.createNewFile();
      } catch (IOException e) {
        crashReportTool.logException("No se pudo crear el archivo temporal para la foto de perfil");
      }
    }
    Uri temporaryPhotoUri =
        FileProvider.getUriForFile(NewStreamActivity.this, BuildConfig.APPLICATION_ID + ".provider",
            pictureTemporaryFile);
    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, temporaryPhotoUri);
    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
  }

  private void choosePhotoFromGallery() {
    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    startActivityForResult(intent, REQUEST_CHOOSE_PHOTO);
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode == 1) {
      if (grantResults.length > 0) {
        onPhotoClick();
      }
    }
  }
}
