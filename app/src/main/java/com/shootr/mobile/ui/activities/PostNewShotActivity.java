package com.shootr.mobile.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.dagger.TemporaryFilesDir;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.listeners.OnMentionClickListener;
import com.shootr.mobile.ui.adapters.recyclerview.MentionsAdapter;
import com.shootr.mobile.ui.component.PhotoPickerController;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.PostNewShotPresenter;
import com.shootr.mobile.ui.views.PostNewShotView;
import com.shootr.mobile.ui.widgets.AvatarView;
import com.shootr.mobile.ui.widgets.NestedListView;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.CrashReportTool;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.WritePermissionManager;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class PostNewShotActivity extends BaseToolbarDecoratedActivity implements PostNewShotView {

  public static final int MAX_LENGTH = 140;
  public static final int MAX_MESSAGE_LENGTH = 5000;
  private static final String USERNAME_FORMAT_REGEX = "^@([-_A-Za-z0-9])*$";

  private static final String EXTRA_SELECTED_IMAGE = "image";
  private static final String EXTRA_REPLY_PARENT_ID = "parentId";
  private static final String EXTRA_REPLY_USERNAME = "parentUsername";
  public static final String EXTRA_PHOTO = "photo";
  public static final String EXTRA_ID_STREAM = "idStream";
  public static final String EXTRA_STREAM_TITLE = "streamTitle";
  public static final String EXTRA_IS_PRIVATE_MESSAGE = "privateMessage";
  public static final String EXTRA_ID_TARGET_USER = "extraIdTargetUser";
  public static final String EXTRA_OPEN_STREAM_AFTER_SEDN_SHOT = "openStream";
  public static final String EXTRA_TEXT = "extraText";
  public static final String SPACE = " ";

  @BindView(R.id.new_shot_avatar) AvatarView avatar;
  @BindView(R.id.new_shot_title) TextView name;
  @BindView(R.id.new_shot_subtitle) TextView username;
  @BindView(R.id.new_shot_text) EditText editTextView;
  @BindView(R.id.new_shot_char_counter) TextView charCounter;
  @BindView(R.id.new_shot_send_button) ImageButton sendButton;
  @BindView(R.id.new_shot_send_progress) ProgressBar progress;
  @BindView(R.id.new_shot_image_container) ViewGroup imageContainer;
  @BindView(R.id.new_shot_mentions_container) ViewGroup mentionsContainer;
  @BindView(R.id.new_shot_image) ImageView image;
  @BindView(R.id.new_shot_mentions) NestedListView mentionsListView;

  @BindString(R.string.analytics_action_shot) String analyticsActionSendShot;
  @BindString(R.string.analytics_label_shot) String analyticsLabelSendShot;
  @BindString(R.string.analytics_action_private_message) String analyticsActionSendPrivateMessage;
  @BindString(R.string.analytics_label_private_message) String analyticsLabelSendPrivateMessage;
  @BindString(R.string.analytics_action_response) String analyticsActionResponse;
  @BindString(R.string.analytics_label_response) String analyticsLabelResponse;
  @BindString(R.string.analytics_source_timeline) String timelineSource;
  @BindString(R.string.analytics_source_shot_detail) String shotDetailSource;

  @Inject ImageLoader imageLoader;
  @Inject SessionRepository sessionRepository;
  @Inject PostNewShotPresenter presenter;
  @Inject FeedbackMessage feedbackMessage;
  @Inject @TemporaryFilesDir File tmpFiles;
  @Inject WritePermissionManager writePermissionManager;
  @Inject CrashReportTool crashReportTool;
  @Inject AnalyticsTool analyticsTool;

  private Subscription commentSubscription;
  private MentionsAdapter adapter;

  private int charCounterColorError;
  private int charCounterColorNormal;
  private PhotoPickerController photoPickerController;
  private boolean isReply = false;
  private String idUserReplied;
  private String replyUsername;
  private String idStream;
  private String streamTitle;
  private boolean openStreamAfterSendShot;

  @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
    toolbarDecorator.getToolbar().setVisibility(View.GONE);
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_new_shot;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    initializeViews();
    initializeSubscription();
  }

  @Override protected void initializePresenter() {
    initializePresenterWithIntentExtras(getIntent().getExtras());
    setupPhotoIfAny();
  }

  private void initializePresenterWithIntentExtras(Bundle extras) {
    if (extras != null) {
      String replyToUsername = extras.getString(EXTRA_REPLY_USERNAME);
      String replyParentId = extras.getString(EXTRA_REPLY_PARENT_ID);
      String extraIdStream = extras.getString(EXTRA_ID_STREAM);
      String extraStreamTitle = extras.getString(EXTRA_STREAM_TITLE);
      String extraTextToSend = extras.getString(EXTRA_TEXT);
      Boolean isPrivateMessage = extras.getBoolean(EXTRA_IS_PRIVATE_MESSAGE, false);
      openStreamAfterSendShot = extras.getBoolean(EXTRA_OPEN_STREAM_AFTER_SEDN_SHOT, false);
      idUserReplied = replyParentId;
      if (replyToUsername != null) {
        replyUsername = replyToUsername;
      }
      if (extraIdStream != null) {
        this.idStream = extraIdStream;
      }
      if (extraStreamTitle != null) {
        this.streamTitle = extraStreamTitle;
      }
      if (extraTextToSend != null) {
        editTextView.setText(extraTextToSend);
      }
      isReply = replyToUsername != null;
      if (isReply) {
        presenter.initializeAsReply(this, replyParentId, replyToUsername);
      } else if (isPrivateMessage) {
        String targetUser = getIntent().getStringExtra(EXTRA_ID_TARGET_USER);
        presenter.initializeAsNewMessage(this, targetUser);
        charCounter.setText(String.valueOf(MAX_MESSAGE_LENGTH));
      } else {
        presenter.initializeAsNewShot(this, extraIdStream, extraStreamTitle);
      }
    }
  }

  private void initializeViews() {
    imageLoader.loadProfilePhoto(sessionRepository.getCurrentUser().getPhoto(), avatar,
        sessionRepository.getCurrentUser().getUsername());
    name.setText(sessionRepository.getCurrentUser().getName());
    username.setText("@" + sessionRepository.getCurrentUser().getUsername());

    writePermissionManager.init(this);

    charCounter.setText(String.valueOf(MAX_LENGTH));

    charCounterColorError = getResources().getColor(R.color.error);
    charCounterColorNormal = getResources().getColor(R.color.gray_70);

    setupPhotoPicker();

    if (adapter == null) {
      adapter = new MentionsAdapter(this, new OnMentionClickListener() {
        @Override public void mention(UserModel user) {
          presenter.onMentionClicked(user);
        }
      }, imageLoader);
    }

    mentionsListView.setAdapter(adapter);
  }

  private void setupPhotoPicker() {
    if (tmpFiles != null) {
      setupPhotoControllerWithTmpFilesDir();
    } else {
      crashReportTool.logException("Picker must have a temporary files directory.");
    }
  }

  private void setupPhotoControllerWithTmpFilesDir() {
    photoPickerController = new PhotoPickerController.Builder().onActivity(this)
        .withTemporaryDir(tmpFiles)
        .withHandler(new PhotoPickerController.Handler() {
          @Override public void onSelected(File imageFile) {
            presenter.selectImage(imageFile);
          }

          @Override public void onError(Exception e) {
            Timber.e(e, "Error selecting image");
          }

          @Override public void startPickerActivityForResult(Intent intent, int requestCode) {
            startActivityForResult(intent, requestCode);
          }

          @Override public void openEditTopicDialog() {
                  /* no-op */
          }

          @Override public void onCheckIn() {
            /* no-op */
          }

          @Override public boolean hasWritePermission() {
            return writePermissionManager.hasWritePermission();
          }

          @Override public void requestWritePermissionToUser() {
            writePermissionManager.requestWritePermissionToUser();
          }
        })
        .build();
  }

  private void initializeSubscription() {
    commentSubscription = RxTextView.textChangeEvents(editTextView)//
        .debounce(100, TimeUnit.MILLISECONDS)//
        .observeOn(AndroidSchedulers.mainThread())//
        .subscribe(getShotCommentObserver());
  }

  private Observer<TextViewTextChangeEvent> getShotCommentObserver() {
    return new Observer<TextViewTextChangeEvent>() {
      @Override public void onCompleted() {
        Timber.d("autocomplete mention onComplete");
      }

      @Override public void onError(Throwable e) {
        crashReportTool.logException(e);
      }

      @Override public void onNext(TextViewTextChangeEvent onTextChangeEvent) {
        checkIfWritingMention(onTextChangeEvent);
      }
    };
  }

  public void checkIfWritingMention(TextViewTextChangeEvent onTextChangeEvent) {
    Pattern pattern = Pattern.compile(USERNAME_FORMAT_REGEX);
    String input = onTextChangeEvent.text().toString();
    String[] words = input.split(SPACE);

    Integer wordPosition = 0;
    Integer characterPosition = 0;

    if (input.length() > 0 && !input.endsWith(SPACE)) {
      autocompleteWhenInputEndsWithoutSpace(pattern, input, words, wordPosition, characterPosition);
    } else if (input.length() <= 0) {
      presenter.onStopMentioning();
    } else if (input.endsWith(SPACE)) {
      autocompleteWhenInputEndsWithSpace(pattern, input, words, wordPosition);
    }
  }

  public void autocompleteWhenInputEndsWithoutSpace(Pattern pattern, String input, String[] words,
      Integer wordPosition, Integer characterPosition) {
    for (String word : words) {
      characterPosition += word.length();
      Matcher matcher = pattern.matcher(word);
      if (matcher.find()) {
        autocompleteIfNoMentionedBefore(input, words, wordPosition, word);
        autocompleteIfWritingUsername(words, wordPosition, characterPosition, word);
      } else {
        presenter.onStopMentioning();
      }
      characterPosition++;
      wordPosition++;
    }
  }

  public void autocompleteIfWritingUsername(String[] words, Integer wordPosition,
      Integer characterPosition, String word) {
    if (editTextView.getSelectionEnd() == characterPosition) {
      presenter.autocompleteMention(word, words, wordPosition);
    }
  }

  public void autocompleteIfNoMentionedBefore(String input, String[] words, Integer wordPosition,
      String word) {
    Pattern wordPattern = Pattern.compile(word + SPACE);
    Matcher wordMatcher = wordPattern.matcher(input);
    if (!wordMatcher.find()) {
      presenter.autocompleteMention(word, words, wordPosition);
    }
  }

  public void autocompleteWhenInputEndsWithSpace(Pattern pattern, String input, String[] words,
      Integer wordPosition) {
    if (words.length >= 1) {
      String word = words[wordPosition];
      Matcher matcher = pattern.matcher(word);
      if (matcher.find() && editTextView.getSelectionEnd() != input.length()) {
        presenter.autocompleteMention(word, words, wordPosition);
      } else {
        presenter.onStopMentioning();
      }
    }
  }

  private void setupPhotoIfAny() {
    File inputImageFile = (File) getIntent().getSerializableExtra(EXTRA_PHOTO);
    if (inputImageFile != null) {
      presenter.selectImage(inputImageFile);
    }
  }

  @OnTextChanged(R.id.new_shot_text) public void onTextChanged() {
    if (presenter.isInitialized()) {
      presenter.textChanged(editTextView.getText().toString());
    }
  }

  @OnClick(R.id.new_shot_send_button) public void onSendShot() {
    boolean isPrivateMessage = getIntent().getBooleanExtra(EXTRA_IS_PRIVATE_MESSAGE, false);
    if (isPrivateMessage) {
      presenter.sendMessage(editTextView.getText().toString());
      sendPrivateMessageToMixPanel();
    } else {
      presenter.sendShot(editTextView.getText().toString());
      sendShotToMixPanel();
    }
  }

  private void sendShotToMixPanel() {
    if (isReply) {
      AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
      builder.setContext(getBaseContext());
      builder.setActionId(analyticsActionResponse);
      builder.setLabelId(analyticsLabelResponse);
      builder.setSource(shotDetailSource);
      builder.setUser(sessionRepository.getCurrentUser());
      if (streamTitle != null) {
        builder.setStreamName(streamTitle);
      }
      if (idStream != null) {
        builder.setIdStream(idStream);
      }
      if (idUserReplied != null) {
        builder.setIdTargetUser(idUserReplied);
        builder.setTargetUsername(replyUsername);
      }
      analyticsTool.analyticsSendAction(builder);
      analyticsTool.appsFlyerSendAction(builder);
    } else {
      AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
      builder.setContext(getBaseContext());
      builder.setActionId(analyticsActionSendShot);
      builder.setLabelId(analyticsLabelSendShot);
      builder.setSource(timelineSource);
      builder.setUser(sessionRepository.getCurrentUser());
      if (streamTitle != null) {
        builder.setStreamName(streamTitle);
      }
      if (idStream != null) {
        builder.setIdStream(idStream);
      }
      analyticsTool.analyticsSendAction(builder);
      analyticsTool.appsFlyerSendAction(builder);
    }
  }

  private void sendPrivateMessageToMixPanel() {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsActionSendPrivateMessage);
    builder.setLabelId(analyticsLabelSendPrivateMessage);
    builder.setSource(timelineSource);
    builder.setIdTargetUser(idStream);
    builder.setTargetUsername(streamTitle);
    builder.setUser(sessionRepository.getCurrentUser());
    analyticsTool.analyticsSendAction(builder);
  }

  @OnClick(R.id.new_shot_photo_button) public void onAddImageFromCamera() {
    presenter.takePhotoFromCamera();
  }

  @OnClick(R.id.new_shot_gallery_button) public void onAddImageFromGallery() {
    presenter.choosePhotoFromGallery();
  }

  @OnClick(R.id.new_shot_image_remove) public void onRemoveImage() {
    presenter.removeImage();
  }

  @Override public void onBackPressed() {
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

  @Override protected void onResume() {
    super.onResume();
    presenter.resume();
    initializeSubscription();
  }

  @Override protected void onPause() {
    super.onPause();
    presenter.pause();
    if (commentSubscription != null) {
      commentSubscription.unsubscribe();
    }
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
    if (openStreamAfterSendShot) {
      startActivity(StreamTimelineActivity.newIntent(this, idStream));
    }
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

  @Override public void showImagePreview(File imageFile) {
    int maxScreenDimension = getScreenWidth();
    imageLoader.load(imageFile, image, maxScreenDimension);
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
    photoPickerController.setupPhotoFromCamera();
  }

  @Override public void choosePhotoFromGallery() {
    photoPickerController.setupPhotoGallery();
  }

  @Override public void showReplyToUsername(String replyToUsername) {
    editTextView.setHint(getString(R.string.reply_placeholder_pattern, replyToUsername));
  }

  @Override public void renderMentionSuggestions(List<UserModel> mentionSuggestions) {
    mentionsListView.setVisibility(View.VISIBLE);
    adapter.setItems(mentionSuggestions);
    adapter.notifyDataSetChanged();
  }

  @Override public void showMentionSuggestions() {
    mentionsContainer.setVisibility(View.VISIBLE);
    mentionsListView.setVisibility(View.VISIBLE);
  }

  @Override public void hideMentionSuggestions() {
    mentionsContainer.setVisibility(View.GONE);
    mentionsListView.setVisibility(View.GONE);
  }

  @Override public void mentionUser(String comment) {
    editTextView.setText(comment);
  }

  @Override public void setCursorToEndOfText() {
    editTextView.setSelection(editTextView.getText().length());
  }

  @Override public void hideImageContainer() {
    imageContainer.setVisibility(View.GONE);
  }

  @Override public void showImageContainer() {
    imageContainer.setVisibility(View.VISIBLE);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    try {
      photoPickerController.onActivityResult(requestCode, resultCode, data);
    } catch (NullPointerException error) {
      feedbackMessage.show(getView(), R.string.error_message_invalid_image);
    }
  }

  @Override public void showLoading() {
    progress.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    progress.setVisibility(View.GONE);
  }

  @Override public void showError(String message) {
    feedbackMessage.show(getView(), message);
  }

  public static class IntentBuilder {

    private File imageFile;
    private Context launchingContext;
    private String idShotParent;
    private String replyToUsername;
    private String idStream;
    private String streamTitle;
    private Boolean isPrivateMessage;
    private String idTargetUser;
    private Boolean openStreamAfterSendShot;
    private String textToSend;

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

    public IntentBuilder withText(String textToSend) {
      this.textToSend = textToSend;
      return this;
    }

    public IntentBuilder setStreamData(String idStream, String streamTitle) {
      this.idStream = idStream;
      this.streamTitle = streamTitle;
      return this;
    }

    public IntentBuilder inReplyTo(String idShot, String username) {
      idShotParent = idShot;
      replyToUsername = username;
      return this;
    }

    public IntentBuilder isPrivateMessage(boolean isPrivateMessage) {
      this.isPrivateMessage = isPrivateMessage;
      return this;
    }

    public IntentBuilder withIdTargetUser(String idTargetUser) {
      this.idTargetUser = idTargetUser;
      return this;
    }

    public IntentBuilder openTimelineAfterSendShot() {
      this.openStreamAfterSendShot = true;
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
      if (idStream != null) {
        intent.putExtra(EXTRA_ID_STREAM, idStream);
      }
      if (streamTitle != null) {
        intent.putExtra(EXTRA_STREAM_TITLE, streamTitle);
      }
      if (idTargetUser != null) {
        intent.putExtra(EXTRA_ID_TARGET_USER, idTargetUser);
      }
      if (isPrivateMessage != null) {
        intent.putExtra(EXTRA_IS_PRIVATE_MESSAGE, isPrivateMessage);
      }
      if (openStreamAfterSendShot != null) {
        intent.putExtra(EXTRA_OPEN_STREAM_AFTER_SEDN_SHOT, openStreamAfterSendShot);
      }
      if (textToSend != null) {
        intent.putExtra(EXTRA_TEXT, textToSend);
      }
      return intent;
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode == 1) {
      if (grantResults.length > 0) {
        if (photoPickerController.getLastRequest() == PhotoPickerController.REQUEST_CHOOSE_PHOTO) {
          choosePhotoFromGallery();
        } else {
          takePhotoFromCamera();
        }
      }
    }
  }
}
