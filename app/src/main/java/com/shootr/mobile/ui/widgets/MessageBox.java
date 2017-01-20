package com.shootr.mobile.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.mobile.R;
import com.shootr.mobile.ShootrApplication;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.MessageBoxPresenter;
import com.shootr.mobile.ui.views.MessageBoxView;
import java.util.List;
import javax.inject.Inject;

public class MessageBox extends RelativeLayout implements MessageBoxView {

  @BindView(R.id.shot_bar_text) EditText newShotText;
  @BindView(R.id.shot_bar_drafts) ImageButton draftButton;
  @BindView(R.id.shot_bar_photo) ImageButton sendImageButton;

  @Inject MessageBoxPresenter presenter;

  public MessageBox(Context context) {
    super(context);
    init();
  }

  public MessageBox(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public MessageBox(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  public MessageBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    ShootrApplication.get(getContext()).getObjectGraph().inject(this);
    setClickable(true);
    LayoutInflater inflater = LayoutInflater.from(getContext());
    inflater.inflate(R.layout.new_message_box, this);
    ButterKnife.bind(this);
    presenter.initializeAsNewShot(this);
  }

  @OnClick(R.id.shot_bar_photo) public void onSendShot() {
    boolean isPrivateMessage = false; //TODO
    if (isPrivateMessage) {
      presenter.sendMessage(newShotText.getText().toString());
      //sendPrivateMessageToMixPanel();
    } else {
      presenter.sendShot(newShotText.getText().toString());
      //sendShotToMixPanel();
    }
  }


  @Override public void enableSendButton() {

  }

  @Override public void disableSendButton() {

  }

  @Override public void showSendButton() {

  }

  @Override public void hideSendButton() {

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
}
