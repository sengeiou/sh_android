package com.shootr.android.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
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
import com.shootr.android.R;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.component.PhotoPickerController;
import com.shootr.android.ui.presenter.PostNewShotPresenter;
import com.shootr.android.ui.views.PostNewShotView;
import com.shootr.android.util.PicassoWrapper;
import java.io.File;
import javax.inject.Inject;
import timber.log.Timber;

public class PostNewShotActivity extends BaseSignedInActivity implements PostNewShotView {

    public static final int MAX_LENGTH = 140;

    private static final String EXTRA_SELECTED_IMAGE = "image";
    private static final String EXTRA_REPLY_PARENT_ID = "parentId";
    private static final String EXTRA_REPLY_USERNAME = "parentUsername";
    public static final String EXTRA_PHOTO = "photo";

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
    @Inject SessionRepository sessionRepository;
    @Inject PostNewShotPresenter presenter;

    private int charCounterColorError;
    private int charCounterColorNormal;
    private PhotoPickerController photoPickerController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()) {
            return;
        }
        // Bypass custom layout inyection, translucent activity doesn't get along with Navigation Drawers
        setContentView(R.layout.activity_new_shot);
        ButterKnife.inject(this);

        initializeViews();
        setupPhotoIfAny();
        initializePresenterWithIntentExtras(getIntent().getExtras());
        setTextReceivedFromIntentIfAny();
    }

    private void initializePresenterWithIntentExtras(Bundle extras) {
        if (extras != null) {
            String replyToUsername = extras.getString(EXTRA_REPLY_USERNAME);
            //TODO usar idshot para pasarselo al interactor
            boolean isReply = replyToUsername != null;
            if (isReply) {
                presenter.initialize(this, replyToUsername);
            }
        } else {
            presenter.initialize(this);
        }
    }

    private void initializeViews() {
        picasso.loadProfilePhoto(sessionRepository.getCurrentUser().getPhoto()).into(avatar);
        name.setText(sessionRepository.getCurrentUser().getName());
        username.setText("@" + sessionRepository.getCurrentUser().getUsername());

        charCounter.setText(String.valueOf(MAX_LENGTH));

        charCounterColorError = getResources().getColor(R.color.error);
        charCounterColorNormal = getResources().getColor(R.color.gray_70);

        photoPickerController =
          new PhotoPickerController.Builder().onActivity(this).withHandler(new PhotoPickerController.Handler() {
              @Override public void onSelected(File imageFile) {
                  presenter.selectImage(imageFile);
              }

              @Override public void onError(Exception e) {
                  Timber.e(e, "Error selecting image");
              }

              @Override public void startPickerActivityForResult(Intent intent, int requestCode) {
                  startActivityForResult(intent, requestCode);
              }
          }).build();
    }

    private void setTextReceivedFromIntentIfAny() {
        String sentText = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        editTextView.setText(sentText);
    }

    private void setupPhotoIfAny() {
        File inputImageFile = (File) getIntent().getSerializableExtra(EXTRA_PHOTO);
        if (inputImageFile != null) {
            presenter.selectImage(inputImageFile);
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
        presenter.navigateBack();
    }

    @Override public void showDiscardAlert() {
        new AlertDialog.Builder(this).setMessage(R.string.new_shot_discard_message)
          .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  presenter.confirmDiscard();
              }
          })
          .setNegativeButton(R.string.no, null)
          .show();
    }

    @Override public void performBackPressed() {
        super.onBackPressed();
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

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        File selectedImageFile = presenter.getSelectedImageFile();
        outState.putSerializable(EXTRA_SELECTED_IMAGE, selectedImageFile);
    }

    @Override protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        File selectedFile = (File) savedInstanceState.getSerializable(EXTRA_SELECTED_IMAGE);
        presenter.selectImage(selectedFile);
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
        int maxScreenDimension = getScreenWidth();
        picasso.load(imageFile).resize(maxScreenDimension, maxScreenDimension).centerInside().skipMemoryCache().into(image);
        imageContainer.setVisibility(View.VISIBLE);
    }

    private int getScreenWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;
    }

    @Override public void hideImagePreview() {
        imageContainer.setVisibility(View.GONE);
        image.setImageDrawable(null);
    }

    @Override public void takePhotoFromCamera() {
        photoPickerController.pickPhotoFromCamera();
    }

    @Override public void choosePhotoFromGallery() {
        photoPickerController.pickPhotoFromGallery();
    }

    @Override public void showReplyToUsername(String replyToUsername) {
        editTextView.setHint(getString(R.string.reply_placeholder_pattern, replyToUsername));
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        photoPickerController.onActivityResult(requestCode, resultCode, data);
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

    public static class IntentBuilder {

        private File imageFile;
        private Context launchingContext;
        private Long idShotParent;
        private String replyToUsername;

        public static IntentBuilder from(Context launchingContext) {
            IntentBuilder intentBuilder = new IntentBuilder();
            intentBuilder.setLaunchingContext(launchingContext);
            return intentBuilder;
        }

        public IntentBuilder setLaunchingContext(Context launchingContext) {
            this.launchingContext = launchingContext;
            return this;
        }

        public IntentBuilder withImage(File imageFile) {
            this.imageFile = imageFile;
            return this;
        }

        public IntentBuilder inReplyTo(Long idShot, String username) {
            idShotParent = idShot;
            replyToUsername = username;
            return this;
        }

        public Intent build() {
            Intent intent = new Intent(launchingContext, PostNewShotActivity.class);
            if (imageFile != null) {
                intent.putExtra(EXTRA_PHOTO, imageFile);
            }
            if (idShotParent != null && replyToUsername != null) {
                intent.putExtra(EXTRA_REPLY_PARENT_ID, idShotParent);
                intent.putExtra(EXTRA_REPLY_USERNAME, replyToUsername);
            }
            return intent;
        }
    }
}
