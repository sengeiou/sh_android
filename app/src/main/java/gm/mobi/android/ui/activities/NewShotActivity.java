package gm.mobi.android.ui.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
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
import gm.mobi.android.db.objects.User;
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
    @InjectView(R.id.new_shot_text) TextView text;
    @InjectView(R.id.new_shot_char_counter) TextView charCounter;
    @InjectView(R.id.new_shot_send_button) TextView sendButton;
    @InjectView(R.id.new_shot_send_progress) ProgressBar progress;

    @Inject Picasso picasso;
    @Inject JobManager jobManager;
    @Inject Bus bus;

    private User currentUser;


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

        // Compound drawable hack
//        Drawable icon = getResources().getDrawable(R.drawable.ic_send);
//        icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
//        sendButton.setCompoundDrawables(null, null, icon, null);
    }

    @OnTextChanged(R.id.new_shot_text)
    public void textChanged() {
        String text = filteredText(this.text.getText().toString());
        int textLength = text.length();
        // Send button disabled when no text
        sendButton.setEnabled(isValidComment(text));
        // Set remaining characters
        int remainingLength = MAX_LENGTH - textLength;
        charCounter.setText(String.valueOf(remainingLength));
        if (remainingLength < 0) {
            //TODO setear estado negativo
        } else {
            //TODO setear positivo
        }
    }

    @OnClick(R.id.new_shot_send_button)
    public void sendShot() {
        String comment = filteredText(text.getText().toString());
        if (isValidComment(comment)) {
            jobManager.addJobInBackground(new NewShotJob(this, currentUser, comment));
            progress.setVisibility(View.VISIBLE);
            sendButton.setVisibility(View.GONE);
        } else {
            Timber.i("Comment invalid: \"%s\"", comment);
            //TODO definir comportamiento de inválido

        }
    }

    @Subscribe
    public void shotSent(PostNewShotResultEvent event) {
        if (event.getStatus() == ResultEvent.STATUS_SUCCESS) {
            Timber.d("Shot sent successfuly :D");
            setResult(RESULT_OK);
            finish(); //TODO animación hacia abjo
        } else {
            Timber.e("Shot not sent successfuly :(");
            progress.setVisibility(View.GONE);
            sendButton.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Error :(", Toast.LENGTH_SHORT).show();
        }

    }

    private String filteredText(String originalText) {
        //TODO definir criterio de filtrado de caracteres
        return originalText.trim();
    }

    private boolean isValidComment(String text) {
        return text.length() > 0;
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
}
