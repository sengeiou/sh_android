package com.shootr.android.ui.component;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import com.cocosw.bottomsheet.BottomSheet;
import com.shootr.android.R;
import com.shootr.android.util.FileChooserUtils;
import java.io.File;
import java.io.IOException;
import timber.log.Timber;

public class PhotoPickerController {

    private static final int REQUEST_TAKE_PHOTO = 993;
    private static final int REQUEST_CHOOSE_PHOTO = 994;

    private Activity activity;
    private Handler handler;
    private String pickerTitle;

    public static class Builder {

        private Activity activity;
        private Handler handler;
        private String title;

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

        public PhotoPickerController build() {
            if (activity == null) {
                throw new IllegalStateException("Picker must have a source activity");
            }
            if (handler == null) {
                throw new IllegalStateException("Picker must have a callback");
            }
            PhotoPickerController pickerController = new PhotoPickerController(activity, handler);
            pickerController.setTitle(title);
            return pickerController;
        }
    }

    private PhotoPickerController(Activity activity, Handler handler) {
        this.activity = activity;
        this.handler = handler;
    }

    private void setTitle(String title) {
        this.pickerTitle = title;
    }

    public void pickPhoto() {
        showPhotoSourcePicker();
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
        return new File(activity.getExternalFilesDir("tmp"), "cameraPhoto.jpg");
    }

    public void pickPhotoFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Intent photoChooserIntent = Intent.createChooser(intent, activity.getString(R.string.photo_edit_gallery));
        handler.startPickerActivityForResult(photoChooserIntent, REQUEST_CHOOSE_PHOTO);
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
        handler.onSelected(new File(FileChooserUtils.getPath(activity, selectedImageUri)));
    }

    private void onTakePhotoResult() {
        //TODO validation, maybe
        handler.onSelected(getCameraPhotoFile());
    }

    public interface Handler {

        void onSelected(File imageFile);

        void onError(Exception e);

        void startPickerActivityForResult(Intent intent, int requestCode);
    }
}
