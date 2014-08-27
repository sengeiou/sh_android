package gm.mobi.android.ui.activities.registro;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.dd.CircularProgressButton;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.R;
import gm.mobi.android.task.BusProvider;
import gm.mobi.android.task.events.LoginResultEvent;
import gm.mobi.android.task.jobs.LoginUserJob;
import gm.mobi.android.ui.activities.MainActivity;
import gm.mobi.android.ui.base.BaseActivity;

public class EmailLoginActivity extends BaseActivity {

    @InjectView(R.id.email_login_username_email) AutoCompleteTextView mEmailUsername;
    @InjectView(R.id.email_login_password) EditText mPassword;
    @InjectView(R.id.email_login_button) CircularProgressButton mLoginButton;
    @InjectView(R.id.email_login_progress) ProgressBar mProgress;
    private LoginUserJob currentLoginJob;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);
        ButterKnife.inject(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Set the email accounts in the AutoComplete
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getEmailAccounts());
        mEmailUsername.setAdapter(adapter);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }


    /* --- UI methods --- */

    @Subscribe
    public void onLoginResult(LoginResultEvent event) {
        setLoading(false);
        currentLoginJob = null;
        if (event.getStatus() == LoginResultEvent.STATUS_SUCCESS) {
            // Yey!
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(MainActivity.PREF_IS_USER_REGISTERED, true).commit();
            startActivity(MainActivity.getIntent(this));
        } else {
            //TODO depende de si el servidor dice qué credencial es incorrecta. Si no lo hace, hay que poner un error genérico para ambos campos
            mEmailUsername.setError("Credenciales incorrectas");
            mEmailUsername.requestFocus();
        }
    }


    private void setLoading(boolean loading) {
        //mProgress.setVisibility(loading ? View.VISIBLE : View.GONE);
        //TODO
        mLoginButton.setIndeterminateProgressMode(true);
        mLoginButton.setProgress(loading ? 1 : 0);
//        mLoginButton.setEnabled(!loading);
    }

    /* --- Simple logic methods --- */

    private void attemptLogin() {
        // Is a login executing already?

        String emailUsername = mEmailUsername.getText().toString();
        String password = mPassword.getText().toString();
        //TODO validar formato primero, maybe

        if (currentLoginJob != null) {
            currentLoginJob.cancelJob();
        }
        currentLoginJob = new LoginUserJob(emailUsername, password);
        GolesApplication.getInstance().getJobManager().addJobInBackground(currentLoginJob);
        setLoading(true);
    }


    public List<String> getEmailAccounts() {
        List<String> emailAccounts = new ArrayList<String>();
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
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
}
