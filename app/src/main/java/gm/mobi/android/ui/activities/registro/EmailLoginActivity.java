package gm.mobi.android.ui.activities.registro;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.dd.CircularProgressButton;
import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.R;
import gm.mobi.android.task.BusProvider;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.LoginResultEvent;
import gm.mobi.android.task.jobs.LoginUserJob;
import gm.mobi.android.ui.activities.MainActivity;
import gm.mobi.android.ui.base.BaseActivity;
import hugo.weaving.DebugLog;
import timber.log.Timber;

public class EmailLoginActivity extends BaseActivity {

    private static final int BUTTON_ERROR = -1;
    private static final int BUTTON_NORMAL = 0;
    private static final int BUTTON_LOADING = 1;

    @Inject JobManager jobManager;

    @InjectView(R.id.email_login_username_email) AutoCompleteTextView mEmailUsername;
    @InjectView(R.id.email_login_password) EditText mPassword;
    @InjectView(R.id.email_login_button) CircularProgressButton mLoginButton;

    private LoginUserJob currentLoginJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerContent(R.layout.activity_login_email);
        ButterKnife.inject(this);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Set the email accounts in the AutoComplete
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getEmailAccounts());
        mEmailUsername.setAdapter(adapter);
    }


    /* --- UI methods --- */

    @Subscribe
    public void onLoginResult(LoginResultEvent event) {
        setLoading(false);
        currentLoginJob = null;
        if (event.getStatus() == LoginResultEvent.STATUS_SUCCESS && event.getSignedUser()!=null) {
            // Yey!
            Timber.d("Welcome, %s", event.getSignedUser().getName());
            //TODO gestionar de forma más avanzada?

            finish();
            Intent i = new Intent(this,MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else {
            mLoginButton.setErrorText(getString(R.string.activity_login_email_error_credentials));
            mLoginButton.setProgress(BUTTON_ERROR);
        }
    }

    @Subscribe
    public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        mLoginButton.setErrorText(getString(R.string.connection_lost));
        mLoginButton.setProgress(BUTTON_ERROR);
    }


    private void setLoading(boolean loading) {
        mLoginButton.setIndeterminateProgressMode(true);
        mLoginButton.setProgress(loading ? BUTTON_LOADING : BUTTON_NORMAL);
    }

    @OnTextChanged({R.id.email_login_username_email, R.id.email_login_password})
    public void resetErrorStatus() {
        if(mLoginButton.getProgress()==BUTTON_ERROR){
            mLoginButton.setProgress(BUTTON_NORMAL);
        }
    }

    /* --- Simple logic methods --- */

    @OnClick(R.id.email_login_button)
    public void attemptLogin() {
        // Is a login executing already?

        String emailUsername = mEmailUsername.getText().toString();
        String password = mPassword.getText().toString();
        //TODO validar formato primero, maybe

        if (currentLoginJob != null) {
            currentLoginJob.cancelJob();
        }
        currentLoginJob = new LoginUserJob(this, emailUsername, password);
        jobManager.addJobInBackground(currentLoginJob);
        setLoading(true);
    }


    /**
     * Retrieves Gmail accounts from the phone.
     */
    public List<String> getEmailAccounts() {
        List<String> emailAccounts = new ArrayList<String>();
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        //TODO ¿other types?
        for (Account account : AccountManager.get(this).getAccountsByType("com.google")) {
            if (emailPattern.matcher(account.name).matches()) {
                emailAccounts.add(account.name);
            }
        }
        return emailAccounts;
    }

    /* --- Lifecycle methdos --- */

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentLoginJob != null) {
            currentLoginJob.cancelJob();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
