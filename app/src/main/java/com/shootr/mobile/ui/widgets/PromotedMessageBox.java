package com.shootr.mobile.ui.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.shootr.mobile.R;
import com.shootr.mobile.ShootrApplication;
import com.shootr.mobile.ui.activities.DraftsActivity;
import com.shootr.mobile.ui.adapters.listeners.OnMentionClickListener;
import com.shootr.mobile.ui.adapters.recyclerview.MentionsAdapter;
import com.shootr.mobile.ui.component.PhotoPickerController;
import com.shootr.mobile.ui.fragments.NewShotBarViewDelegate;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.MessageBoxPresenter;
import com.shootr.mobile.ui.views.MessageBoxView;
import com.shootr.mobile.util.CrashReportTool;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
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

public class PromotedMessageBox extends RelativeLayout implements MessageBoxView {

  private static final String EMPTY_TEXT = "";
  public static final int MAX_LENGTH = 140;
  public static final int MAX_MESSAGE_LENGTH = 5000;
  private static final String USERNAME_FORMAT_REGEX = "^@([-_A-Za-z0-9])*$";

  public static final int PROMOTED_SHOW_INFO = 0;
  public static final int PROMOTED_ENABLED = 1;

  public static final String EXTRA_STREAM_TITLE = "streamTitle";
  public static final String SPACE = " ";

  @BindView(R.id.shot_bar_text) EditText newShotText;
  @BindView(R.id.shot_bar_drafts) ImageButton draftButton;
  @BindView(R.id.shot_bar_photo) ImageButton sendImageButton;
  @BindView(R.id.new_shot_send_button) ImageButton sendShotButton;
  @BindView(R.id.new_shot_mentions) NestedListView mentionsListView;
  @BindView(R.id.new_shot_char_counter)
  TextView charCounter;
  @BindView(R.id.container) LinearLayout container;
  @BindView(R.id.promoted_shot_button) RelativeLayout promotedButton;
  @BindView(R.id.pending_badge) View pendingBadge;

  @Inject MessageBoxPresenter presenter;
  @Inject
  CrashReportTool crashReportTool;

  private PhotoPickerController photoPickerController;
  private NewShotBarViewDelegate newShotBarViewDelegate;
  private Activity activity;
  private Subscription commentSubscription;
  private MentionsAdapter adapter;
  private int charCounterColorError;
  private int charCounterColorNormal;
  private boolean isPrivateMessage;
  private OnActionsClick onActionsClick;
  private boolean canShowPromotedButton = false;
  private boolean isReply = false;
  private String parentId;
  private boolean isInited = false;
  private int promotedButtonState;

  public PromotedMessageBox(Context context) {
    super(context);
  }

  public PromotedMessageBox(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public PromotedMessageBox(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public PromotedMessageBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  public NewShotBarViewDelegate getNewShotBarViewDelegate() {
    return newShotBarViewDelegate;
  }

  public boolean isInited() {
    return isInited;
  }

  public void init(Activity parentActivity, PhotoPickerController photoPickerController,
      ImageLoader imageLoader, FeedbackMessage feedbackMessage, final OnActionsClick onActionsClick,
      boolean isPrivateMessage, String idTargetUser, boolean isReply, String parentId, String idStream) {
    this.isInited = true;
    this.isPrivateMessage = isPrivateMessage;
    this.isReply = isReply;
    this.parentId = parentId;
    this.photoPickerController = photoPickerController;
    this.onActionsClick = onActionsClick;
    this.activity = parentActivity;
    ShootrApplication.get(getContext()).getObjectGraph().inject(this);
    setClickable(true);
    LayoutInflater inflater = LayoutInflater.from(getContext());
    inflater.inflate(R.layout.promoted_message_box, this);
    ButterKnife.bind(this);

    if (!isPrivateMessage) {
      if (!isReply) {
        presenter.initializeAsNewShot(this);
      } else {
        presenter.initializeAsReply(this, parentId, idStream);
      }
    } else {
      presenter.initializeAsNewMessage(this, idTargetUser);
    }

    newShotBarViewDelegate =
        new NewShotBarViewDelegate(photoPickerController, draftButton, feedbackMessage) {
          @Override public void openNewShotView() {
            onActionsClick.onNewShotClick();
          }

          @Override public void openNewShotViewWithImage(File image) {
            onActionsClick.onShotWithImageClick(image);
          }

          @Override public void openEditTopicDialog() {
            onActionsClick.onTopicClick();
          }
        };

    sendImageButton.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View view) {
        onActionsClick.onAttachClick();
      }
    });

    if (adapter == null) {
      adapter = new MentionsAdapter(getContext(), new OnMentionClickListener() {
        @Override public void mention(UserModel user) {
          presenter.onMentionClicked(user);
        }
      }, imageLoader);
    }

    mentionsListView.setAdapter(adapter);

    initializeSubscription();

    charCounterColorError = getResources().getColor(R.color.error);
    charCounterColorNormal = getResources().getColor(R.color.gray_70);
  }

  private void initializeSubscription() {
    commentSubscription = RxTextView.textChangeEvents(newShotText)//
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

  public void showMessageBox() {
    container.setVisibility(VISIBLE);
  }

  public void hideMessageBox() {
    container.setVisibility(GONE);
  }

  public boolean canPostPromotedShot() {
    return canShowPromotedButton;
  }

  public void setCanShowPromotedButton(boolean canShowPromotedButton) {
    this.canShowPromotedButton = canShowPromotedButton;
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
    if (newShotText.getSelectionEnd() == characterPosition) {
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
      if (matcher.find() && newShotText.getSelectionEnd() != input.length()) {
        presenter.autocompleteMention(word, words, wordPosition);
      } else {
        presenter.onStopMentioning();
      }
    }
  }

  @OnClick(R.id.new_shot_send_button) public void onSendShot() {
    if (isPrivateMessage) {
      presenter.sendMessage(newShotText.getText().toString());
    } else {
      presenter.sendShot(newShotText.getText().toString());
    }
    onActionsClick.onSendClick();
  }

  @OnClick(R.id.shot_bar_drafts) public void openDraftsClicked() {
    activity.startActivity(new Intent(activity, DraftsActivity.class));
  }

  @OnClick(R.id.promoted_shot_button) public void promotedButtonClicked() {
    if (promotedButtonState == PROMOTED_ENABLED) {
      onActionsClick.onPromotedClick();
    } else if (promotedButtonState == PROMOTED_SHOW_INFO) {
      onActionsClick.onPromotedShowInfoClick();
    }
  }

  @OnTextChanged(R.id.shot_bar_text) public void onTextChanged() {
    if (presenter.isInitialized()) {
      presenter.textChanged(newShotText.getText().toString());
    }
  }

  public void setHintText(String text) {
    newShotText.setHint(text);
  }

  @Override public void showSendButton() {
    charCounter.setVisibility(VISIBLE);
    animateView(sendImageButton, sendShotButton);
  }

  @Override public void hideSendButton() {
    charCounter.setVisibility(GONE);
    animateView(sendShotButton, sendImageButton);
  }

  private void animateView(View viewToHide, final View viewToShow) {
    viewToHide.setVisibility(GONE);
    if (viewToShow.getVisibility() == GONE) {
      viewToShow.setVisibility(View.VISIBLE);
      viewToShow.setScaleX(0);
      viewToShow.setScaleY(0);
      ObjectAnimator scaleX = ObjectAnimator.ofFloat(viewToShow, "scaleX", 0f, 1f);
      ObjectAnimator scaleY = ObjectAnimator.ofFloat(viewToShow, "scaleY", 0f, 1f);
      AnimatorSet set = new AnimatorSet();
      set.playTogether(scaleX, scaleY);
      set.setDuration(150);
      set.setInterpolator(new DecelerateInterpolator());
      set.addListener(new AnimatorListenerAdapter() {
        @Override public void onAnimationEnd(Animator animation) {
          viewToShow.setScaleX(1f);
          viewToShow.setScaleY(1f);
        }
      });
      set.start();
    }
  }

  @Override public void hideKeyboard() {
    InputMethodManager im = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    im.hideSoftInputFromWindow(newShotText.getWindowToken(), 0);
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

  @Override public void renderMentionSuggestions(List<UserModel> mentionSuggestions) {
    mentionsListView.setVisibility(View.VISIBLE);
    adapter.setItems(mentionSuggestions);
    adapter.notifyDataSetChanged();
  }

  @Override public void showMentionSuggestions() {
    mentionsListView.setVisibility(View.VISIBLE);
  }

  @Override public void hideMentionSuggestions() {
    mentionsListView.setVisibility(View.GONE);
  }

  @Override public void mentionUser(String comment) {
    newShotText.setText(comment);
  }

  @Override public void setCursorToEndOfText() {
    newShotText.setSelection(newShotText.getText().length());
  }

  @Override public void showError(String message) {

  }

  @Override public void clearTextBox() {
    newShotText.setText(EMPTY_TEXT);
  }

  @Override public void enableSendButton() {
    sendShotButton.setEnabled(true);
  }

  @Override public void disableSendButton() {
    sendShotButton.setEnabled(false);
  }

  @Override public void hidePromotedButton() {
    promotedButton.setVisibility(GONE);
  }

  @Override public void showPromotedButton() {
    if (canShowPromotedButton && promotedButton.getVisibility() == GONE) {
      promotedButton.setVisibility(View.VISIBLE);
      promotedButton.setScaleX(0);
      promotedButton.setScaleY(0);
      ObjectAnimator scaleX = ObjectAnimator.ofFloat(promotedButton, "scaleX", 0f, 1f);
      ObjectAnimator scaleY = ObjectAnimator.ofFloat(promotedButton, "scaleY", 0f, 1f);
      AnimatorSet set = new AnimatorSet();
      set.playTogether(scaleX, scaleY);
      set.setDuration(150);
      set.setInterpolator(new DecelerateInterpolator());
      set.addListener(new AnimatorListenerAdapter() {
        @Override public void onAnimationEnd(Animator animation) {
          promotedButton.setScaleX(1f);
          promotedButton.setScaleY(1f);
        }
      });
      set.start();
    }
  }

  @Override public void showPromotedBadge() {
    pendingBadge.setVisibility(VISIBLE);
  }

  public void pickImage() {
    newShotBarViewDelegate.pickImage();
  }

  public void pickPrivateMessageOptions() {
    newShotBarViewDelegate.showPrivateMessageOptions();
  }

  public void showHolderOptions() {
    newShotBarViewDelegate.showHolderOptions();
  }

  public void openNewShotViewWithImage(File image) {
    newShotBarViewDelegate.openNewShotViewWithImage(image);
  }

  public void openEditTopicDialog() {
    newShotBarViewDelegate.openEditTopicDialog();
  }

  public void showDraftsButton() {
    newShotBarViewDelegate.showDraftsButton();
  }

  public void hideDraftsButton() {
    newShotBarViewDelegate.hideDraftsButton();
  }

  public int getPromotedButtonState() {
    return promotedButtonState;
  }

  public void setPromotedButtonState(int promotedButtonState) {
    this.promotedButtonState = promotedButtonState;
    if (promotedButtonState == PROMOTED_SHOW_INFO) {
      promotedButton.setBackgroundResource(R.drawable.promoted_round_layout_dissabled);
    } else if (promotedButtonState == PROMOTED_ENABLED) {
      promotedButton.setBackgroundResource(R.drawable.promoted_round_layout);
    }
  }

  public interface OnActionsClick {
    void onTopicClick();

    void onNewShotClick();

    void onShotWithImageClick(File image);

    void onAttachClick();

    void onSendClick();

    void onCheckInClick();

    void onPromotedClick();

    void onPromotedShowInfoClick();
  }
}
