package com.shootr.mobile.ui.activities;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.adamstyrc.cookiecutter.CookieCutterImageView;
import com.adamstyrc.cookiecutter.CookieCutterShape;
import com.adamstyrc.cookiecutter.ImageUtils;
import com.artjimlop.altex.AltexImageDownloader;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.base.BaseNoToolbarActivity;
import com.shootr.mobile.util.CrashReportTool;
import com.shootr.mobile.util.FeedbackMessage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.inject.Inject;

public class CropPictureActivity extends BaseNoToolbarActivity {

  public static final String EXTRA_PHOTO_TYPE = "photoType";
  public static final String EXTRA_URI = "uri";
  public static final String EXTRA_IMAGE_NAME = "imageName";

  @Inject CrashReportTool crashReportTool;
  @Inject FeedbackMessage feedbackMessage;

  @Bind(R.id.ivCrop) CookieCutterImageView ivCrop;
  @Bind(R.id.crop_loading) ProgressBar cropLoading;
  @Bind(R.id.button_crop) View cropButton;
  private String filename;
  private View parentLayout;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContainerContent(R.layout.activity_crop_picture);
    ButterKnife.bind(this);

    parentLayout = findViewById(R.id.root_view);

    boolean isPictureFromCamera = getIntent().getBooleanExtra(EXTRA_PHOTO_TYPE, false);
    filename = getIntent().getStringExtra(EXTRA_IMAGE_NAME);
    String url = getIntent().getStringExtra(EXTRA_URI);
    Uri uri = Uri.parse(url);

    ivCrop.getParams().setShape(CookieCutterShape.SQUARE);

    if (isPictureFromCamera) {
      cropCameraPicture();
    } else {
      cropGalleryPicture(uri);
    }
  }

  private void cropGalleryPicture(Uri uri) {
    try {
      Point screenSize = ImageUtils.getScreenSize(this);
      Bitmap scaledBitmap =
          ImageUtils.decodeUriToScaledBitmap(this, uri, screenSize.x, screenSize.y);
      ivCrop.setImageBitmap(scaledBitmap);
    } catch (FileNotFoundException e) {
      crashReportTool.logException(e);
    }
  }

  private void cropCameraPicture() {
    File photoFile = getCameraPhotoFile();
    ivCrop.setImageBitmap(AltexImageDownloader.readFromDisk(photoFile));
  }

  @OnClick(R.id.button_crop) public void cropClicked() {
    cropButton.setVisibility(View.GONE);
    cropLoading.setVisibility(View.VISIBLE);
    cropPicture();
  }

  @OnClick(R.id.button_cancel) public void cancelClicked() {
    finish();
  }

  private void cropPicture() {
    try {
      Bitmap bitmap = ivCrop.getCroppedBitmap();
      FileOutputStream fileOutputStream = new FileOutputStream(getCameraPhotoFile());
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
      setResult(RESULT_OK);
      finish();
    } catch (FileNotFoundException | IllegalArgumentException error) {
      crashReportTool.logException(error);
      feedbackMessage.show(parentLayout, getString(R.string.error_message_unknown));
      cropButton.setVisibility(View.VISIBLE);
      cropLoading.setVisibility(View.GONE);
    }
  }

  private File getCameraPhotoFile() {
    return new File(getExternalFilesDir("tmp"), filename);
  }
}
