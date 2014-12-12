package com.shootr.android.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.shootr.android.data.SessionManager;
import com.shootr.android.ui.presenter.PostNewShotPresenter;
import com.shootr.android.ui.views.PostNewShotView;
import com.shootr.android.util.FileChooserUtils;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseSignedInActivity;
import java.io.File;
import java.io.IOException;
import javax.inject.Inject;
import timber.log.Timber;

public class PostNewShotActivity extends BaseSignedInActivity implements PostNewShotView {

    public static final int MAX_LENGTH = 140;
    private static final int REQUEST_CHOOSE_PHOTO = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;

    public static final String EXTRA_DEFAULT_INPUT_MODE = "input";
    public static final int INPUT_CAMERA = 1;
    public static final int INPUT_GALLERY = 2;

    @InjectView(R.id.new_shot_avatar) ImageView avatar;
    @InjectView(R.id.new_shot_title) TextView name;
    @InjectView(R.id.new_shot_subtitle) TextView username;
    @InjectView(R.id.new_shot_text) EditText editTextView;
    @InjectView(R.id.new_shot_char_counter) TextView charCounter;
    @InjectView(R.id.new_shot_send_button) ImageButton sendButton;
    @InjectView(R.id.new_shot_send_progress) ProgressBar progress;
    @InjectView(R.id.new_shot_image_container) ViewGroup imageContainer;
    @InjectView(R.id.new_shot_image) ImageView image;

    @Inject PicassoWrapper picasso;
    @Inject SessionManager sessionManager;

    private int charCounterColorError;
    private int charCounterColorNormal;

    private PostNewShotPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()) {
            return;
        }
        // Bypass custom layout inyection, translucent activity doesn't get along with Navigation Drawers
        setContentView(R.layout.activity_new_shot);
        ButterKnife.inject(this);

        initializePresenter();
        initializeViews();
        setTextReceivedFromIntent();
        openDefaultInputIfAny();
    }

    private void initializePresenter() {
        presenter = getObjectGraph().get(PostNewShotPresenter.class);
        presenter.initialize(this, getObjectGraph());
    }

    private void initializeViews() {
        picasso.loadProfilePhoto(sessionManager.getCurrentUser().getPhoto()).into(avatar);
        name.setText(sessionManager.getCurrentUser().getName());
        username.setText("@" + sessionManager.getCurrentUser().getUserName());

        charCounter.setText(String.valueOf(MAX_LENGTH));

        charCounterColorError = getResources().getColor(R.color.error);
        charCounterColorNormal = getResources().getColor(R.color.gray_70);
    }

    private void setTextReceivedFromIntent() {
        String sentText = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        editTextView.setText(sentText);
    }

    private void openDefaultInputIfAny() {
        int defaultInputMode = getIntent().getIntExtra(EXTRA_DEFAULT_INPUT_MODE, 0);
        switch (defaultInputMode) {
            case INPUT_CAMERA:
                presenter.takePhotoFromCamera();
                break;
            case INPUT_GALLERY:
                presenter.choosePhotoFromGallery();
                break;
        }
    }

    @OnTextChanged(R.id.new_shot_text)
    public void onTextChanged() {
        presenter.textChanged(editTextView.getText().toString());
    }

    @OnClick(R.id.new_shot_send_button)
    public void onSendShot() {
        presenter.sendShot(editTextView.getText().toString());
    }

    @OnClick(R.id.new_shot_photo_button)
    public void onAddImageFromCamera() {
        presenter.takePhotoFromCamera();
    }

    @OnClick(R.id.new_shot_gallery_button)
    public void onAddImageFromGallery() {
        presenter.choosePhotoFromGallery();
    }

    @OnClick(R.id.new_shot_image_remove)
    public void onRemoveImage() {
        presenter.removeImage();
    }

    @Override
    public void onBackPressed() {
        if (TextUtils.isEmpty(editTextView.getText().toString().trim())) {
            super.onBackPressed();
        } else {
            new AlertDialog.Builder(this).setMessage("Discard shot?")
              .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                  @Override public void onClick(DialogInterface dialog, int which) {
                      finish();
                  }
              })
              .setNegativeButton("No", null)
              .create()
              .show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.pause();
    }

    @Override public void setResultOk() {
        setResult(RESULT_OK);
    }

    @Override public void closeScreen() {
        finish();
    }

    @Override public void setRemainingCharactersCount(int remainingCharacters) {
        charCounter.setText(String.valueOf(remainingCharacters));
    }

    @Override public void setRemainingCharactersColorValid() {
        charCounter.setTextColor(charCounterColorNormal);
    }

    @Override public void setRemainingCharactersColorInvalid() {
        charCounter.setTextColor(charCounterColorError);
    }

    @Override public void enableSendButton() {
        sendButton.setEnabled(true);
    }

    @Override public void disableSendButton() {
        sendButton.setEnabled(false);
    }

    @Override public void showSendButton() {
        sendButton.setVisibility(View.VISIBLE);
    }

    @Override public void hideSendButton() {
        sendButton.setVisibility(View.GONE);
    }

    @Override public void hideKeyboard() {
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(editTextView.getWindowToken(), 0);
    }

    @Override public void showImagePreviewFromUrl(String imageUrl) {
        picasso.load(imageUrl).into(image);
        imageContainer.setVisibility(View.VISIBLE);
    }

    @Override public void showImagePreview(File imageFile) {
        int maxScreenDimension = getMaxScreenDimension();
        picasso.load(imageFile).resize(maxScreenDimension, maxScreenDimension).centerInside().skipMemoryCache().into(image);
        imageContainer.setVisibility(View.VISIBLE);
    }

    private int getMaxScreenDimension() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        int maxDimension = width > height ? width : height;
        return maxDimension;
    }

    @Override public void hideImagePreview() {
        imageContainer.setVisibility(View.GONE);
        image.setImageDrawable(null);
    }

    @Override public void takePhotoFromCamera() {
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

    @Override public void choosePhotoFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.photo_edit_choose)),
          REQUEST_CHOOSE_PHOTO);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            File selectedPhotoFile = null;
            if (requestCode == REQUEST_CHOOSE_PHOTO) {
                Uri selectedImageUri = data.getData();
                selectedPhotoFile = new File(FileChooserUtils.getPath(this, selectedImageUri));
            }else if (requestCode == REQUEST_TAKE_PHOTO) {
                selectedPhotoFile = getCameraPhotoFile();
            }
            presenter.selectImage(selectedPhotoFile);
        }
    }

    private File getCameraPhotoFile() {
        return new File(this.getExternalFilesDir("tmp"), "profileUpload.jpg");
    }

    @Override public void showLoading() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override public void hideLoading() {
        progress.setVisibility(View.GONE);
    }

    @Override public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
