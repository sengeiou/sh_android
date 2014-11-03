package gm.mobi.android.ui.activities.registro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import gm.mobi.android.R;
import gm.mobi.android.task.events.loginregister.RegistrationCompletedEvent;
import gm.mobi.android.ui.base.BaseActivity;
import javax.inject.Inject;

public class FacebookRegistroActivity extends BaseActivity {

    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME = "usernameSuggestion";
    private static final String KEY_AVATAR = "avatar";

    @Inject JobManager jobManager;
    @Inject Bus bus;

    @InjectView(R.id.facebook_login_email) TextView mEmailText;
    @InjectView(R.id.facebook_login_username) EditText mUsernameText;
    @InjectView(R.id.facebook_login_avatar) ImageView mAvatarImage;
    @InjectView(R.id.facebook_login_progress) ProgressBar progressBar;
    private String email;
    private String usernameSuggestion;
    private String avatarUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerContent(R.layout.activity_registro_facebook);
        ButterKnife.inject(this);

        Bundle extras = savedInstanceState != null ? savedInstanceState : getIntent().getExtras();
        //TODO guardar los datos en el savedInstanceState cuando corresponde para el giro de pantalla y tal

        email = extras.getString(KEY_EMAIL);
        usernameSuggestion = extras.getString(KEY_USERNAME);
        avatarUrl = extras.getString(KEY_AVATAR);

        prefillUserInformation(email, usernameSuggestion, avatarUrl);
    }

    /* --- UI methods --- */

    private void prefillUserInformation(String email, String username, String avatarUrl) {
        mEmailText.setText(email);
        mUsernameText.setText(username);
        // TODO controlar error del avatar
        Picasso.with(this).load(avatarUrl).error(R.drawable.ic_launcher).into(mAvatarImage);
    }


    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
    }

    /* --- Simple logic methods --- */

    /**
     * Envía la información del usuario al servidor para realizar el registro.
     * Esta información consiste en:
     * *** Aún por definir ***
     */
    private void startUserRegistration() {
        // TODO Validate usernameSuggestion only first, maybe
        setLoading(true);
        //TODO validar localmente
        String username = mUsernameText.getText().toString();
        //TODO comprobar si ya estaba ejecutándose, y decidir si debemos lanzar uno nuevo o ignorar

        //TODO launch some job
    }

    @Subscribe
    public void registrationCompleted(RegistrationCompletedEvent event) {
        setLoading(false);
        if (!event.hasError()) {
            //TODO log user in
            Toast.makeText(this, "Listo Ebaristo!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error :(", Toast.LENGTH_SHORT).show();
            Log.e("Goles", "Error de registro", event.getError());
        }
    }


    /* --- Lifecycle methods --- */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.registro_email, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            startUserRegistration();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_EMAIL, email);
        outState.putString(KEY_USERNAME, usernameSuggestion);
        outState.putString(KEY_AVATAR, avatarUrl);
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

    public static Intent getIntent(Context context, String email, String username, String avatarUrl) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EMAIL, email);
        bundle.putString(KEY_USERNAME, username);
        bundle.putString(KEY_AVATAR, avatarUrl);
        return new Intent(context, FacebookRegistroActivity.class).putExtras(bundle);
    }
}
