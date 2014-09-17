package gm.mobi.android.ui.activities.registro;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.squareup.otto.Bus;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import gm.mobi.android.R;
import gm.mobi.android.ui.base.BaseActivity;
import gm.mobi.android.util.Gravatar;

public class EmailRegistrationActivity extends BaseActivity {


    @InjectView(R.id.email_registro_email_spinner) Spinner mEmailRegistroEmailSpinner;
    @InjectView(R.id.email_registro_username) EditText mEmailRegistroUsername;
    @InjectView(R.id.email_registro_password) EditText mEmailRegistroPassword;
    @InjectView(R.id.email_registro_avatar) ImageView mEmailRegistroAvatar;

    private List<String> emailAccounts;

    @Inject Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerContent(R.layout.activity_registro_email);
        ButterKnife.inject(this);

        emailAccounts = getEmailAccounts();

        mEmailRegistroEmailSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, emailAccounts));
        mEmailRegistroEmailSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (emailAccounts != null) {
                    loadGravatar(emailAccounts.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    private void loadGravatar(String email) {
        mEmailRegistroAvatar.setVisibility(View.GONE);
        String url = new Gravatar().getImageUrl(email);
        Picasso.with(this).load(url).into(mEmailRegistroAvatar, new Callback() {
            @Override
            public void onSuccess() {
                mEmailRegistroAvatar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {
                mEmailRegistroAvatar.setVisibility(View.GONE);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.registro_email, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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
}
