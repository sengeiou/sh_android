package com.shootr.mobile.ui.component;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import com.cocosw.bottomsheet.BottomSheet;
import com.shootr.mobile.BuildConfig;
import com.shootr.mobile.R;
import com.shootr.mobile.util.FileChooserUtils;
import java.io.File;
import java.io.IOException;
import timber.log.Timber;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class PhotoPickerController {

  public static final int REQUEST_TAKE_PHOTO = 993;
  public static final int REQUEST_CHOOSE_PHOTO = 994;
  private static final String CAMERA_PHOTO_TMP_FILE = "cameraPhoto.jpg";

  private final Activity activity;
  private final Handler handler;
  private final File temporaryFiles;
  private String pickerTitle;
  private int lastRequest = 0;

  public static class Builder {

    private Activity activity;
    private Handler handler;
    private String title;
    private File temporaryFiles;

    public Builder onActivity(Activity activity) {
      this.activity = activity;
      return this;
    }

    public Builder withTitle(String title) {
      this.title = title;
      return this;
    }

    public Builder withHandler(Handler handler) {
      this.handler = handler;
      return this;
    }

    public Builder withTemporaryDir(File temporaryDir) {
      this.temporaryFiles = temporaryDir;
      return this;
    }

    public PhotoPickerController build() {
      checkNotNull(activity, "Picker must have a source activity");
      checkNotNull(handler, "Picker must have a callback");
      checkNotNull(temporaryFiles, "Picker mus have a temporary files directory.");

      PhotoPickerController pickerController =
          new PhotoPickerController(activity, handler, temporaryFiles);
      pickerController.setTitle(title);
      return pickerController;
    }
  }

  private PhotoPickerController(Activity activity, Handler handler, File temporaryFiles) {
    this.activity = activity;
    this.handler = handler;
    this.temporaryFiles = temporaryFiles;
  }

  private void setTitle(String title) {
    this.pickerTitle = title;
  }

  public void pickPhoto() {
    pickTimelineOptions();
  }

  public void pickOption() {
    pickHolderOptions();
  }

  private void pickTimelineOptions() {
    if (!activity.isFinishing()) {
      BottomSheet.Builder builder = new BottomSheet.Builder(activity).title(pickerTitle)
          .sheet(R.menu.photo_picker_bottom_sheet)
          .listener(new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
              switch (which) {
                case R.id.menu_photo_gallery:
                  setupPhotoGallery();
                  break;
                case R.id.menu_photo_take:
                  setupPhotoFromCamera();
                  break;
                case R.id.menu_check_in:
                  handler.onCheckIn();
                  break;
                default:
                  break;
              }
            }
          });
      builder.show();
    }
  }

  public void pickPrivateMessageOptions() {
    if (!activity.isFinishing()) {
      BottomSheet.Builder builder = new BottomSheet.Builder(activity).title(pickerTitle)
          .sheet(R.menu.private_messages_bottom_sheet)
          .listener(new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
              switch (which) {
                case R.id.menu_photo_gallery:
                  setupPhotoGallery();
                  break;
                case R.id.menu_photo_take:
                  setupPhotoFromCamera();
                  break;
                case R.id.menu_check_in:
                  handler.onCheckIn();
                  break;
                default:
                  break;
              }
            }
          });
      builder.show();
    }
  }

  private void pickHolderOptions() {
    BottomSheet.Builder builder = new BottomSheet.Builder(activity).title(pickerTitle)
        .sheet(R.menu.option_picker_author_bottom_sheet)
        .listener(new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            switch (which) {
              case R.id.menu_stream_topic:
                handler.openEditTopicDialog();
                break;
              case R.id.menu_photo_gallery:
                setupPhotoGallery();
                break;
              case R.id.menu_photo_take:
                setupPhotoFromCamera();
                break;
              case R.id.menu_check_in:
                handler.onCheckIn();
                break;
              default:
                break;
            }
          }
        });
    builder.show();
  }

  public void setupPhotoFromCamera() {
    lastRequest = REQUEST_TAKE_PHOTO;
    if (handler.hasWritePermission()) {
      pickPhotoFromCamera(activity);
    } else {
      handler.requestWritePermissionToUser();
    }
  }

  public void setupPhotoGallery() {
    lastRequest = REQUEST_CHOOSE_PHOTO;
    if (handler.hasWritePermission()) {
      pickPhotoFromGallery();
    } else {
      handler.requestWritePermissionToUser();
    }
  }

  public int getLastRequest() {
    return lastRequest;
  }

  private void pickPhotoFromCamera(Activity activity) {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    File pictureTemporaryFile = getCameraPhotoFile();
    if (!pictureTemporaryFile.exists()) {
      try {
        pictureTemporaryFile.getParentFile().mkdirs();
        pictureTemporaryFile.createNewFile();
      } catch (IOException e) {
        Timber.e(e, "No se pudo crear el archivo temporal para la foto");
        handler.onError(e);
      }
    }
    Uri temporaryPhotoUri =
        FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider",
            pictureTemporaryFile);
    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, temporaryPhotoUri);
    handler.startPickerActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
  }

  private File getCameraPhotoFile() {
    return new File(temporaryFiles, CAMERA_PHOTO_TMP_FILE);
  }

  private void pickPhotoFromGallery() {
    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    handler.startPickerActivityForResult(intent, REQUEST_CHOOSE_PHOTO);
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
      onTakePhotoResult();
    } else if (requestCode == REQUEST_CHOOSE_PHOTO && resultCode == Activity.RESULT_OK) {
      onChoosePhotoResult(data);
    }
  }

  private void onChoosePhotoResult(Intent data) {
    Uri selectedImageUri = data.getData();
    String path = FileChooserUtils.getPath(activity, selectedImageUri);
    if (path != null) {
      handler.onSelected(new File(path));
    }
  }

  private void onTakePhotoResult() {
    //TODO validation, maybe
    handler.onSelected(getCameraPhotoFile());
  }

  public interface Handler {

    void onSelected(File imageFile);

    void onError(Exception e);

    void startPickerActivityForResult(Intent intent, int requestCode);

    void openEditTopicDialog();

    void onCheckIn();

    boolean hasWritePermission();

    void requestWritePermissionToUser();
  }
}
