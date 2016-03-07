package com.shootr.mobile.ui.component;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import com.cocosw.bottomsheet.BottomSheet;
import com.shootr.mobile.R;
import com.shootr.mobile.util.FileChooserUtils;
import java.io.File;
import java.io.IOException;
import timber.log.Timber;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class PhotoPickerController {

    private static final int REQUEST_TAKE_PHOTO = 993;
    private static final int REQUEST_CHOOSE_PHOTO = 994;
    private static final String CAMERA_PHOTO_TMP_FILE = "cameraPhoto.jpg";

    private final Activity activity;
    private final Handler handler;
    private final File temporaryFiles;
    private String pickerTitle;

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

            PhotoPickerController pickerController = new PhotoPickerController(activity, handler, temporaryFiles);
            pickerController.setTitle(title);
            return pickerController;
        }
    }

    protected PhotoPickerController(Activity activity, Handler handler, File temporaryFiles) {
        this.activity = activity;
        this.handler = handler;
        this.temporaryFiles = temporaryFiles;
    }

    private void setTitle(String title) {
        this.pickerTitle = title;
    }

    public void pickPhoto() {
        showPhotoSourcePicker();
    }

    public void pickOption(){
        showOptionAuthorSourcePicker();
    }

    private void showPhotoSourcePicker() {
        BottomSheet.Builder builder = new BottomSheet.Builder(activity).title(pickerTitle)
          .sheet(R.menu.photo_picker_bottom_sheet)
          .listener(new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  switch (which) {
                      case R.id.menu_photo_gallery:
                          pickPhotoFromGallery();
                          break;
                      case R.id.menu_photo_take:
                          pickPhotoFromCamera();
                          break;
                      default:
                          break;
                  }
              }
          });
        builder.show();
    }

    private void showOptionAuthorSourcePicker(){
        BottomSheet.Builder builder = new BottomSheet.Builder(activity).title(pickerTitle)
          .sheet(R.menu.option_picker_author_bottom_sheet)
          .listener(new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  switch (which) {
                      case R.id.menu_stream_topic:
                          handler.openEditTopicDialog();
                          break;
                      case R.id.menu_photo_gallery:
                          pickPhotoFromGallery();
                          break;
                      case R.id.menu_photo_take:
                          pickPhotoFromCamera();
                          break;
                      default:
                          break;
                  }
              }
          });
        builder.show();
    }

    public void pickPhotoFromCamera() {
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
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(pictureTemporaryFile));
        handler.startPickerActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
    }

    private File getCameraPhotoFile() {
        return new File(temporaryFiles, CAMERA_PHOTO_TMP_FILE);
    }

    public void pickPhotoFromGallery() {
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
    }
}
