package com.shootr.android.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.path.android.jobqueue.JobManager;
import com.shootr.android.data.SessionManager;
import com.shootr.android.ui.presenter.PostNewShotPresenter;
import com.shootr.android.ui.views.PostNewShotView;
import com.shootr.android.util.PicassoWrapper;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.shootr.android.ShootrApplication;
import com.shootr.android.R;
import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.db.objects.ShotEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.task.events.shots.PostNewShotResultEvent;
import com.shootr.android.task.jobs.shots.PostNewShotJob;
import com.shootr.android.ui.base.BaseSignedInActivity;
import javax.inject.Inject;
import timber.log.Timber;

public class PostNewShotActivity extends BaseSignedInActivity implements PostNewShotView {

    public static final int MAX_LENGTH = 140;

    @InjectView(R.id.new_shot_avatar) ImageView avatar;
    @InjectView(R.id.new_shot_title) TextView name;
    @InjectView(R.id.new_shot_subtitle) TextView username;
    @InjectView(R.id.new_shot_text) EditText editTextView;
    @InjectView(R.id.new_shot_char_counter) TextView charCounter;
    @InjectView(R.id.new_shot_send_button) Button sendButton;
    @InjectView(R.id.new_shot_send_progress) ProgressBar progress;

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

    @OnTextChanged(R.id.new_shot_text)
    public void textChanged() {
        presenter.textChanged(editTextView.getText().toString());
    }

    @OnClick(R.id.new_shot_send_button)
    public void sendShot() {
        presenter.sendShot(editTextView.getText().toString());
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

    @Override public void hideKeyboard() {
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(editTextView.getWindowToken(), 0);
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
