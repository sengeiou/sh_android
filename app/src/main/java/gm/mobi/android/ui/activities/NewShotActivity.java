package gm.mobi.android.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.R;
import gm.mobi.android.db.manager.ShotManager;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.ResultEvent;
import gm.mobi.android.task.events.shots.PostNewShotResultEvent;
import gm.mobi.android.task.jobs.shots.NewShotJob;
import gm.mobi.android.ui.base.BaseSignedInActivity;
import timber.log.Timber;

public class NewShotActivity extends BaseSignedInActivity {

    public static final int MAX_LENGTH = 140;

    @InjectView(R.id.new_shot_avatar) ImageView avatar;
    @InjectView(R.id.new_shot_title) TextView name;
    @InjectView(R.id.new_shot_subtitle) TextView username;
    @InjectView(R.id.new_shot_text) EditText text;
    @InjectView(R.id.new_shot_char_counter) TextView charCounter;
    @InjectView(R.id.new_shot_send_button) TextView sendButton;
    @InjectView(R.id.new_shot_send_progress) ProgressBar progress;

    @Inject Picasso picasso;
    @Inject JobManager jobManager;
    @Inject Bus bus;
    @Inject SQLiteOpenHelper dbHelper;

    private User currentUser;

    private int charCounterColorError;
    private int charCounterColorNormal;
    private Shot previousShot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()) {
            return;
        }
        // Bypass custom layout inyection, translucent activity doesn't get along with Navigation Drawers
        setContentView(R.layout.activity_new_shot);
        ButterKnife.inject(this);

        currentUser = GolesApplication.get(this).getCurrentUser();

        picasso.load(currentUser.getPhoto()).into(avatar);
        name.setText(currentUser.getName());
        username.setText("@" + currentUser.getUserName());

        charCounter.setText(String.valueOf(MAX_LENGTH));

        charCounterColorError = getResources().getColor(R.color.error);
        charCounterColorNormal = getResources().getColor(R.color.gray_70);

        previousShot = ShotManager.retrieveLastShotFromUser(dbHelper.getReadableDatabase(),
            GolesApplication.get(this).getCurrentUser().getIdUser());

        // Compound drawable hack
        //        Drawable icon = getResources().getDrawable(R.drawable.ic_send);
        //        icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        //        sendButton.setCompoundDrawables(null, null, icon, null);

        setTextReceivedFromIntent();
    }

    private void setTextReceivedFromIntent() {
        String sentText = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        text.setText(sentText);
    }

    @OnTextChanged(R.id.new_shot_text)
    public void textChanged() {
        // We don't trim or filter the text for character checking, so the user doesn't see incoherences.
        String text = this.text.getText().toString();
        int textLength = text.length();
        // Send button disabled when no text
        sendButton.setEnabled(isValidComment(text));
        // Set remaining characters
        int remainingLength = MAX_LENGTH - textLength;
        charCounter.setText(String.valueOf(remainingLength));

        setCharCounterStatus(remainingLength > 0);
    }

    private void setCharCounterStatus(boolean valid) {
        charCounter.setTextColor(valid ? charCounterColorNormal : charCounterColorError);
    }

    @OnClick(R.id.new_shot_send_button)
    public void sendShot() {
        String comment = filteredText(text.getText().toString());
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(text.getWindowToken(), 0);

        if (isCommentRepeated(comment)) {
            Toast.makeText(this, R.string.new_shot_repeated, Toast.LENGTH_SHORT).show();
            return;
        }
        if (isValidComment(comment)) {
            jobManager.addJobInBackground(new NewShotJob(this, currentUser, comment));
            setProgressUI(true);
        } else {
            Timber.i("Comment invalid: \"%s\"", comment);
            Toast.makeText(this, "Invalid text", Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        setProgressUI(false);
        Toast.makeText(this, R.string.connection_lost, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void shotSent(PostNewShotResultEvent event) {
        if (event.getStatus() == ResultEvent.STATUS_SUCCESS) {
            Timber.d("Shot sent successfuly :D");
            setResult(RESULT_OK);
            finish(); //TODO animación hacia abjo
        } else {
            Timber.e("Shot not sent successfuly :(");
            setProgressUI(false);
            Toast.makeText(this, R.string.communication_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void setProgressUI(boolean showProgress) {
        progress.setVisibility(showProgress ? View.VISIBLE : View.GONE);
        sendButton.setVisibility(showProgress ? View.GONE : View.VISIBLE);
    }

    private String filteredText(String originalText) {
        //TODO definir criterio de filtrado de caracteres
        String trimmed = originalText.trim();
        while (trimmed.contains("\n\n\n")) {
            trimmed = trimmed.replace("\n\n\n", "\n\n");
        }
        return trimmed;
    }

    private boolean isValidComment(String text) {
        return text.length() > 0 && text.length() <= MAX_LENGTH;
    }

    private boolean isCommentRepeated(String text) {
        if (previousShot != null) {
            return previousShot.getComment().equals(text);
        } else {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (TextUtils.isEmpty(text.getText().toString().trim())) {
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
}
