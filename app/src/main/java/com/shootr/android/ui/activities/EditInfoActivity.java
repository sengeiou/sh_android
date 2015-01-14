package com.shootr.android.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnTextChanged;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.ui.model.UserWatchingModel;
import com.shootr.android.ui.presenter.EditInfoPresenter;
import com.shootr.android.ui.views.EditInfoView;
import com.shootr.android.ui.widgets.SwitchBar;
import timber.log.Timber;

public class EditInfoActivity extends BaseSignedInActivity implements EditInfoView{

    @InjectView(R.id.edit_info_switch_bar) SwitchBar watchingSwitchBar;
    @InjectView(R.id.edit_info_place) EditText place;
    @InjectView(R.id.edit_info_place_divider) View placeDivider;

    private MenuItem sendMenuItem;

    private EditInfoPresenter editInfoPresenter;

    public static Intent getIntent(Context context, MatchModel eventModel, UserWatchingModel watchingModel) {
        String place = watchingModel.hasStatusMessage() ? watchingModel.getPlace() : null;
        return getIntent(context, eventModel.getIdMatch(), watchingModel.isWatching(), eventModel.getTitle(), place);
    }

    @Deprecated
    public static Intent getIntent(Context context, Long idMatch, boolean watchingStatus, String matchTitle, String place) {
        EditInfoPresenter.EditInfoModel editInfoModel =
          new EditInfoPresenter.EditInfoModel(idMatch, matchTitle, watchingStatus, place);
        Intent launchIntent = new Intent(context, EditInfoActivity.class);
        launchIntent.putExtras(editInfoModel.toBundle());
        return launchIntent;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()){return;}

        setContainerContent(R.layout.activity_edit_info);
        ButterKnife.inject(this);

        setupActionBar();

        Bundle infoBundle = getIntent().getExtras();
        initializePresenter(infoBundle);

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
        place.setFilters(new InputFilter[]{
          newlineFilter,
          new InputFilter.LengthFilter(60)
        });

        watchingSwitchBar.addOnSwitchChangeListener(new SwitchBar.OnSwitchChangeListener() {
            @Override public void onSwitchChanged(SwitchCompat switchView, boolean isChecked) {
                editInfoPresenter.watchingStatusChanged();
            }
        });

        // Not done by ButterKnife so it's not called when the presenter sets the text
        place.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                editInfoPresenter.placeTextChanged();
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /* no-op */
            }

            @Override public void afterTextChanged(Editable s) {
                /* no-op */
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

        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_navigation_close);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_info, menu);
        sendMenuItem = menu.findItem(R.id.menu_send);
        sendMenuItem.setEnabled(false);
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
            case R.id.menu_delete:
                deleteMatch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteMatch() {
        editInfoPresenter.deleteMatch();
    }

    @Override public void setSendButonEnabled(boolean enabled) {
        if (sendMenuItem != null) {
            sendMenuItem.setEnabled(enabled);
        }
    }

    @Override public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override public void setWatchingStatus(boolean watching) {
        watchingSwitchBar.setChecked(watching);
    }

    @Override public void closeScreen() {
        setResult(RESULT_OK);
        finish();
    }

    @Override public void showDeleteMatchConfirmation(String confirmationTitle, String confirmationMessage) {
        new AlertDialog.Builder(this)
          .setTitle(confirmationTitle)
          .setMessage(confirmationMessage)
          .setPositiveButton(R.string.delete_match_confirmation, new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  editInfoPresenter.confirmDeleteMatch();
              }
          })
          .setNegativeButton(R.string.cancel, null)
          .show();

    }

    @Override public String getPlaceText() {
        return this.place.getText().toString();
    }

    @Override public void setPlaceText(String place) {
        this.place.setText(place);
    }

    @Override public boolean getWatchingStatus() {
        return watchingSwitchBar.isChecked();
    }

    @Override public void disablePlaceText() {
        place.setEnabled(false);
        place.setVisibility(View.GONE);
        placeDivider.setVisibility(View.INVISIBLE);
    }

    @Override public void enablePlaceText() {
        place.setEnabled(true);
        place.setVisibility(View.VISIBLE);
        placeDivider.setVisibility(View.VISIBLE);
    }

    @Override public void setFocusOnPlace() {
        place.requestFocus();
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(place.getApplicationWindowToken(), InputMethodManager.SHOW_IMPLICIT, 0);
    }

    @Override public void alertPlaceNotWatchingNotAllow() {
        new AlertDialog.Builder(this).setMessage(
          getString(R.string.watching_place_not_watching_alert))
          .setPositiveButton(android.R.string.ok, null)
          .show();
    }

    @Override public void showNotificationsAlert() {
        Toast.makeText(this, R.string.watching_notifications_alert, Toast.LENGTH_LONG).show();
    }
}
