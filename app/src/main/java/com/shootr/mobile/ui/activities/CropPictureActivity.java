package com.shootr.mobile.ui.activities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.LoadCallback;
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

  @BindView(R.id.ivCrop) CropImageView ivCrop;
  @BindView(R.id.crop_loading) ProgressBar cropLoading;
  @BindView(R.id.button_crop) View cropButton;
  private String filename;
  private View parentLayout;
  private Uri uri;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContainerContent(R.layout.activity_crop_picture);
    ButterKnife.bind(this);

    parentLayout = findViewById(R.id.root_view);

    boolean isPictureFromCamera = getIntent().getBooleanExtra(EXTRA_PHOTO_TYPE, false);
    filename = getIntent().getStringExtra(EXTRA_IMAGE_NAME);
    String url = getIntent().getStringExtra(EXTRA_URI);
    uri = Uri.parse(url);

    ivCrop.setCropMode(CropImageView.CropMode.SQUARE);

    if (isPictureFromCamera) {
      cropCameraPicture();
    } else {
      cropGalleryPicture(uri);
    }
  }

  private void cropGalleryPicture(Uri uri) {
    try {
      ivCrop.startLoad(uri, new LoadCallback() {
        @Override public void onSuccess() {
              /* no-op */
        }

        @Override public void onError() {
              /* no-op */
        }
      });
    } catch (Exception e) {
      crashReportTool.logException(e);
    }
  }

  private void cropCameraPicture() {
    try {
      File photoFile = getCameraPhotoFile();
      uri = Uri.fromFile(photoFile);
      ivCrop.startLoad(uri, new LoadCallback() {
        @Override public void onSuccess() {
            /* no-op */
        }

        @Override public void onError() {
            /* no-op */
        }
      });
    } catch (Exception e) {
      crashReportTool.logException(e.getMessage());
    }
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
    } catch (NullPointerException err) {
      crashReportTool.logException(err);
    }

  }

  private File getCameraPhotoFile() {
    return new File(getExternalFilesDir("tmp"), filename);
  }
}
