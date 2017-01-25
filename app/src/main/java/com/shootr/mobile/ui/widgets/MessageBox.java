package com.shootr.mobile.ui.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.eftimoff.androidplayer.Player;
import com.eftimoff.androidplayer.actions.property.PropertyAction;
import com.eftimoff.androidplayer.listeners.PlayerEndListener;
import com.shootr.mobile.R;
import com.shootr.mobile.ShootrApplication;
import com.shootr.mobile.ui.activities.DraftsActivity;
import com.shootr.mobile.ui.activities.PostNewShotActivity;
import com.shootr.mobile.ui.component.PhotoPickerController;
import com.shootr.mobile.ui.fragments.NewShotBarViewDelegate;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.MessageBoxPresenter;
import com.shootr.mobile.ui.views.MessageBoxView;
import com.shootr.mobile.ui.views.NewShotBarView;
import com.shootr.mobile.util.FeedbackMessage;
import java.io.File;
import java.util.List;
import javax.inject.Inject;

public class MessageBox extends RelativeLayout implements MessageBoxView {

  private static final String EMPTY_TEXT = "";
  
  @BindView(R.id.shot_bar_text) EditText newShotText;
  @BindView(R.id.shot_bar_drafts) ImageButton draftButton;
  @BindView(R.id.shot_bar_photo) ImageButton sendImageButton;
  @BindView(R.id.new_shot_send_button) ImageButton sendShotButton;

  @Inject MessageBoxPresenter presenter;

  private PhotoPickerController photoPickerController;
  private NewShotBarViewDelegate newShotBarViewDelegate;
  private Activity activity;

  public MessageBox(Context context) {
    super(context);
  }

  public MessageBox(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public MessageBox(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public MessageBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  public NewShotBarViewDelegate getNewShotBarViewDelegate() {
    return newShotBarViewDelegate;
  }

  public void init(final String idStream, final String streamTitle, Activity parentActivity,
      PhotoPickerController photoPickerController, FeedbackMessage feedbackMessage, OnClickListener attachClickListener) {
    this.photoPickerController = photoPickerController;
    this.activity = parentActivity;
    ShootrApplication.get(getContext()).getObjectGraph().inject(this);
    setClickable(true);
    LayoutInflater inflater = LayoutInflater.from(getContext());
    inflater.inflate(R.layout.new_message_box, this);
    ButterKnife.bind(this);
    presenter.initializeAsNewShot(this);

    newShotBarViewDelegate =
        new NewShotBarViewDelegate(photoPickerController, draftButton, feedbackMessage) {
          @Override public void openNewShotView() {
            Intent newShotIntent = PostNewShotActivity.IntentBuilder //
                .from(activity) //
                .setStreamData(idStream, streamTitle).build();
            activity.startActivity(newShotIntent);
          }

          @Override public void openNewShotViewWithImage(File image) {
            Intent newShotIntent = PostNewShotActivity.IntentBuilder //
                .from(activity) //
                .withImage(image) //
                .setStreamData(idStream, streamTitle).build();
            activity.startActivity(newShotIntent);
          }

          @Override public void openEditTopicDialog() {
            //setupTopicCustomDialog();
          }
        };

    sendImageButton.setOnClickListener(attachClickListener);
  }

  @OnClick(R.id.new_shot_send_button) public void onSendShot() {
    boolean isPrivateMessage = false; //TODO
    if (isPrivateMessage) {
      presenter.sendMessage(newShotText.getText().toString());
      //sendPrivateMessageToMixPanel();
    } else {
      presenter.sendShot(newShotText.getText().toString());
      //sendShotToMixPanel();
    }
  }

  @OnClick(R.id.shot_bar_drafts) public void openDraftsClicked() {
    activity.startActivity(new Intent(activity, DraftsActivity.class));
  }

  @OnTextChanged(R.id.shot_bar_text) public void onTextChanged() {
    if (presenter.isInitialized()) {
      presenter.textChanged(newShotText.getText().toString());
    }
  }


  @Override public void showSendButton() {
    animateView(sendImageButton, sendShotButton);
  }

  @Override public void hideSendButton() {
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

  }

  @Override public void setRemainingCharactersColorValid() {

  }

  @Override public void setRemainingCharactersColorInvalid() {

  }

  @Override public void renderMentionSuggestions(List<UserModel> mentionSuggestions) {

  }

  @Override public void showMentionSuggestions() {

  }

  @Override public void hideMentionSuggestions() {

  }

  @Override public void mentionUser(String comment) {

  }

  @Override public void setCursorToEndOfText() {

  }

  @Override public void showError(String message) {

  }

  @Override public void clearTextBox() {
    newShotText.setText(EMPTY_TEXT);
  }

  public void pickImage() {
    newShotBarViewDelegate.pickImage();
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
    //newShotBarViewDelegate.showDraftsButton();
  }

  public void hideDraftsButton() {
    //newShotBarViewDelegate.hideDraftsButton();
  }
}
