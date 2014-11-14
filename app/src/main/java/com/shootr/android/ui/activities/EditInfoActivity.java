package com.shootr.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.presenter.EditInfoPresenter;
import com.shootr.android.ui.views.EditInfoView;
import timber.log.Timber;

public class EditInfoActivity extends BaseSignedInActivity implements EditInfoView{

    @InjectView(R.id.edit_info_switch) SwitchCompat watchingSwitch;

    private MenuItem sendMenuItem;

    private EditInfoPresenter editInfoPresenter;

    public static Intent getIntent(Context context, Long idMatch, boolean watchingStatus, String matchTitle) {
        EditInfoPresenter.EditInfoModel editInfoModel =
          new EditInfoPresenter.EditInfoModel(idMatch, matchTitle, watchingStatus);
        Intent launchIntent = new Intent(context, EditInfoActivity.class);
        launchIntent.putExtras(editInfoModel.toBundle());
        return launchIntent;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()){
            return;
        }

        setContainerContent(R.layout.activity_edit_info);
        ButterKnife.inject(this);

        setupActionBar();

        Bundle infoBundle = getIntent().getExtras();
        initializePresenter(infoBundle);

        watchingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editInfoPresenter.watchingStatusChanged(isChecked);
            }
        });
    }

    private void initializePresenter(Bundle infoBundle) {
        editInfoPresenter = getObjectGraph().get(EditInfoPresenter.class);
        editInfoPresenter.initialize(this, EditInfoPresenter.EditInfoModel.fromBundle(infoBundle), getObjectGraph());
    }

    private void sendNewStatus() {
        editInfoPresenter.sendNewStatus();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_info, menu);
        sendMenuItem = menu.getItem(0);
        sendMenuItem.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menu_send:
                sendNewStatus();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override public void setSendButonEnabled(boolean enabled) {
        Timber.d("Send button enabled: "+enabled);
        sendMenuItem.setEnabled(enabled);
    }

    @Override public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override public void setWatchingStatus(boolean watching) {
        watchingSwitch.setChecked(watching);
    }

    @Override public void closeScreen() {
        setResult(RESULT_OK);
        finish();
    }
}
