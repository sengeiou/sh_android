package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.UserWatchingModel;
import com.shootr.android.ui.presenter.EditInfoPresenter;
import com.shootr.android.ui.views.EditInfoView;
import javax.inject.Inject;

public class EditStatusActivity extends BaseSignedInActivity implements EditInfoView{

    public static final String KEY_STATUS = "status";
    private static final String KEY_TITLE = "title";

    @InjectView(R.id.edit_info_status) EditText statusText;

    @Inject EditInfoPresenter editInfoPresenter;

    private int sendMenuIcon;
    private MenuItem sendMenuItem;

    public static Intent getIntent(Context context, EventModel eventModel, UserWatchingModel watchingModel) {
        String title = eventModel.getTitle();
        String status = watchingModel.getStatus();

        Intent intent = new Intent(context, EditStatusActivity.class);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_STATUS, status);
        return intent;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()){return;}

        setContainerContent(R.layout.activity_edit_info);
        ButterKnife.inject(this);

        setupActionBar();

        Bundle initialInfoBundle = getIntent().getExtras();
        initializePresenter(initialInfoBundle);

        //TODO esto es lógica de la vista, probablemente debería ir al presenter
        InputFilter newlineFilter = new InputFilter() {
            @Override public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart,
              int dend) {
                String sourceString = String.valueOf(source);
                if (sourceString.contains("\n")) {
                    return sourceString.replace("\n", "");
                }
                return null;
            }
        };
        statusText.setFilters(new InputFilter[] {
          newlineFilter, new InputFilter.LengthFilter(60)
        });

        // Not done by ButterKnife so it's not called when the presenter sets the text
        statusText.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                editInfoPresenter.statusTextChanged();
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /* no-op */
            }

            @Override public void afterTextChanged(Editable s) {
                /* no-op */
            }
        });
    }

    private void initializePresenter(Bundle initialInfoBundle) {
        String eventTitle = initialInfoBundle.getString(KEY_TITLE);
        String statusText = initialInfoBundle.getString(KEY_STATUS);
        editInfoPresenter.initialize(this, statusText);
    }

    private void sendNewStatus() {
        editInfoPresenter.sendNewStatus();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_navigation_close);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_info, menu);
        sendMenuItem = menu.findItem(R.id.menu_send);
        sendMenuItem.setEnabled(false);
        if (sendMenuIcon != 0) {
            sendMenuItem.setIcon(sendMenuIcon);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_send:
                sendNewStatus();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override public void setSendButonEnabled(boolean enabled) {
        if (sendMenuItem != null) {
            sendMenuItem.setEnabled(enabled);
        }
    }

    @Override public void closeScreenWithResult(String stautsText) {
        Intent resultIntent = getIntent();
        resultIntent.putExtra(KEY_STATUS, stautsText);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override public String getStatusText() {
        return this.statusText.getText().toString();
    }

    @Override public void setMenuShoot() {
        sendMenuIcon = R.drawable.ic_action_send;
        if (sendMenuItem != null) {
            sendMenuItem.setIcon(sendMenuIcon);
        }
    }

    @Override public void setStatusText(String status) {
        this.statusText.setText(status);
    }

    @Override public void setFocusOnStatus() {
        statusText.requestFocus();
        statusText.setSelection(statusText.getText().length());
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(statusText.getApplicationWindowToken(), InputMethodManager.SHOW_IMPLICIT, 0);
    }
}
