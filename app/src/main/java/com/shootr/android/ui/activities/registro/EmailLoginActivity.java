package com.shootr.android.ui.activities.registro;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.dd.CircularProgressButton;
import com.path.android.jobqueue.JobManager;
import com.shootr.android.data.bus.Main;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.User;
import com.shootr.android.domain.UserList;
import com.shootr.android.domain.interactor.event.GetEventInteractor;
import com.shootr.android.domain.interactor.user.GetPeopleInteractor;
import com.shootr.android.ui.activities.TimelineActivity;
import com.shootr.android.ui.base.BaseToolbarActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.shootr.android.ShootrApplication;
import com.shootr.android.R;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.task.events.loginregister.LoginResultEvent;
import com.shootr.android.task.jobs.loginregister.LoginUserJob;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.inject.Inject;
import timber.log.Timber;

public class EmailLoginActivity extends BaseToolbarActivity {

    private static final int BUTTON_ERROR = -1;
    private static final int BUTTON_NORMAL = 0;
    private static final int BUTTON_LOADING = 1;

    @Inject JobManager jobManager;
    @Inject @Main Bus bus;
    @Inject @Deprecated UserEntityMapper userEntityMapper;
    @Inject GetPeopleInteractor getPeopleInteractor;
    @Inject GetEventInteractor getEventInteractor;


    @Inject SessionRepository sessionRepository;
    @InjectView(R.id.email_login_username_email) AutoCompleteTextView mEmailUsername;
    @InjectView(R.id.email_login_password) EditText mPassword;
    @InjectView(R.id.email_login_button) CircularProgressButton mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerContent(R.layout.activity_login_email);
        ButterKnife.inject(this);

        // Set the email accounts in the AutoComplete
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getEmailAccounts());
        mEmailUsername.setAdapter(adapter);
    }


    /* --- UI methods --- */

    @Subscribe
    public void onLoginResult(LoginResultEvent event) {
        UserEntity userEntity = event.getResult();
        User user = userEntityMapper.transform(userEntity, userEntity.getIdUser());
        // Yey!
        Timber.d("Succesfuly logged in %s", user.getUsername());
        // Store user in current session
        sessionRepository.createSession(user.getIdUser(), userEntity.getSessionToken(), user); //TODO quitar token del User

        // Another ñapa
        if (user.getVisibleEventId() != null) {
            getEventInteractor.loadEvent(user.getVisibleEventId(), new GetEventInteractor.Callback() {
                @Override public void onLoaded(Event event) {
                    getPeopleInteractor.obtainPeople();
                }
            });
        } else {
            getPeopleInteractor.obtainPeople();

        }

    }

    @Subscribe
    public void onPeopleLoaded(UserList userList){
        goToTimeline();
    }


    private void goToTimeline() {
        setLoading(false);
        // Launch main activity, and destroy the stack trace
        finish();
        Intent i = new Intent(this,TimelineActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Subscribe
    public void onCommunicationError(CommunicationErrorEvent event) {
        Toast.makeText(this,R.string.communication_error,Toast.LENGTH_LONG).show();
    }

    @Subscribe
    public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        mLoginButton.setErrorText(getString(R.string.connection_lost));
        mLoginButton.setProgress(BUTTON_ERROR);
    }

    @Subscribe
    public void onCredentialError(LoginUserJob.CredentialErrorEvent event) {
        mLoginButton.setErrorText(getString(R.string.activity_login_email_error_credentials));
        mLoginButton.setProgress(BUTTON_ERROR);
    }

    private void setLoading(boolean loading) {
        mLoginButton.setIndeterminateProgressMode(true);
        mLoginButton.setProgress(loading ? BUTTON_LOADING : BUTTON_NORMAL);
    }

    @OnTextChanged({R.id.email_login_username_email, R.id.email_login_password})
    public void validateInput() {
        if (TextUtils.isEmpty(mEmailUsername.getText().toString()) || TextUtils.isEmpty(mPassword.getText().toString())) {
            mLoginButton.setEnabled(false);
        } else {
            mLoginButton.setEnabled(true);
        }
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
        startJob(emailUsername,password);
        setLoading(true);
    }

    public void startJob(String emailUsername, String password){
        LoginUserJob currentLoginJob = ShootrApplication.get(this).getObjectGraph().get(LoginUserJob.class);
        currentLoginJob.init(emailUsername, password);
        jobManager.addJobInBackground(currentLoginJob);
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
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
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
