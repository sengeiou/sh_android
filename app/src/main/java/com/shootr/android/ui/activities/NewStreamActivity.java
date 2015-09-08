package com.shootr.android.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.presenter.NewStreamPresenter;
import com.shootr.android.ui.views.NewStreamView;
import com.shootr.android.ui.widgets.FloatLabelLayout;
import com.shootr.android.util.FeedbackMessage;
import com.shootr.android.util.MenuItemValueHolder;
import javax.inject.Inject;

public class NewStreamActivity extends BaseToolbarDecoratedActivity implements NewStreamView {

    public static final int RESULT_EXIT_STREAM = 3;
    public static final String KEY_STREAM_ID = "stream_id";

    public static final String KEY_STREAM_TITLE = "stream_title";

    private static final String EXTRA_EDITED_TITLE = "title";
    private  static final String EXTRA_EDITED_SHORT_TITLE = "short_title";

    @Inject NewStreamPresenter presenter;
    @Inject FeedbackMessage feedbackMessage;

    @Bind(R.id.new_stream_title) EditText titleView;
    @Bind(R.id.new_stream_title_label) FloatLabelLayout titleLabelView;
    @Bind(R.id.new_stream_title_error) TextView titleErrorView;
    @Bind(R.id.new_stream_short_title) EditText shortTitleView;
    @Bind(R.id.new_stream_short_title_warning) TextView shortTitleErrorView;
    @Bind(R.id.new_stream_description) EditText descriptionView;

    private MenuItemValueHolder doneMenuItem = new MenuItemValueHolder();
    private MenuItemValueHolder removeMenuItem = new MenuItemValueHolder();
    private MenuItemValueHolder restoreMenuItem = new MenuItemValueHolder();

    public static Intent newIntent(Context context, String idStream) {
        Intent launchIntent = new Intent(context, NewStreamActivity.class);
        launchIntent.putExtra(NewStreamActivity.KEY_STREAM_ID, idStream);
        return launchIntent;
    }

    //region Initialization
    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_new_stream;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        String idStreamToEdit = getIntent().getStringExtra(KEY_STREAM_ID);

        initializeViews();
        setupActionbar(idStreamToEdit);

        if (savedInstanceState != null) {
            String editedTitle = savedInstanceState.getString(EXTRA_EDITED_TITLE);
            String editedShortTitle = savedInstanceState.getString(EXTRA_EDITED_SHORT_TITLE);

            titleView.setText(editedTitle);
            shortTitleView.setText(editedShortTitle);
        }
    }

    @Override protected void initializePresenter() {
        String idStreamToEdit = getIntent().getStringExtra(KEY_STREAM_ID);
        initializePresenter(idStreamToEdit);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_EDITED_TITLE, titleView.getText().toString());
        outState.putString(EXTRA_EDITED_SHORT_TITLE, shortTitleView.getText().toString());
    }

    private void initializeViews() {
        ButterKnife.bind(this);
        titleView.addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) {
                presenter.titleTextChanged(s.toString());
                resetTitleError();
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /* no-op */
            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                /* no-op */
            }
        });
        shortTitleView.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /* no-op */
            }

            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /* no-op */
            }

            @Override public void afterTextChanged(Editable editable) {
                presenter.shortTitleTextChanged(editable.toString());
            }
        });
    }

    private void setupActionbar(String idStreamToEdit) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_navigation_close);

        if (idStreamToEdit != null) {
            actionBar.setTitle(R.string.activity_edit_stream_title);
        }
    }

    private void initializePresenter(String idStreamToEdit) {
        presenter.initialize(this, idStreamToEdit);
    }
    //endregion

    //region Activity methods
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_stream, menu);
        doneMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_done));
        removeMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_remove));
        restoreMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_restore));
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_done) {
            presenter.done();
            return true;
        }else if (item.getItemId() == R.id.menu_remove) {
            presenter.remove();
            return true;
        }else if (item.getItemId() == R.id.menu_restore) {
            presenter.restore();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    //endregion

    //region View Methods

    @Override public void setStreamTitle(String title) {
        titleLabelView.showLabelWithoutAnimation();
        titleView.setText(title);
    }

    @Override public String getStreamTitle() {
        return titleView.getText().toString();
    }

    @Override public void showTitleError(String errorMessage) {
        titleErrorView.setText(errorMessage);
    }

    @Override public void closeScreenWithResult(String streamId, String title) {
        setResult(RESULT_OK, new Intent() //
          .putExtra(KEY_STREAM_ID, streamId) //
          .putExtra(KEY_STREAM_TITLE, title));
        finish();
    }

    @Override
    public void closeScreenWithExitStream() {
        setResult(RESULT_EXIT_STREAM);
        finish();
    }

    @Override public void doneButtonEnabled(boolean enable) {
        doneMenuItem.setEnabled(enable);
    }

    @Override public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(titleView.getWindowToken(), 0);
    }

    @Override public void showNotificationConfirmation() {
        new AlertDialog.Builder(this)
          .setMessage(getString(R.string.stream_notification_confirmation_message))
          .setPositiveButton(getString(R.string.stream_notification_confirmation_yes), new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  presenter.confirmNotify(true);
              }
          })
          .setNegativeButton(getString(R.string.stream_notification_confirmation_no), new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  presenter.confirmNotify(false);
              }
          })
          .create().show();
    }

    @Override
    public void showRemoveStreamButton() {
        removeMenuItem.setVisible(true);
    }

    @Override
    public void askRemoveStreamConfirmation() {
        new AlertDialog.Builder(this).setMessage(R.string.remove_stream_confirmation)
          .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  presenter.confirmRemoveStream();
              }
          })
          .setNegativeButton(R.string.cancel, null)
          .show();
    }

    @Override public void showShortTitle(String currentShortTitle) {
        shortTitleView.setText(currentShortTitle);
    }

    @Override public String getStreamShortTitle() {
        return shortTitleView.getText().toString();
    }

    @Override public String getStreamDescription() {
        return descriptionView.getText().toString();
    }

    @Override public void showDescription(String description) {
        descriptionView.setText(description);
    }

    @Override public void showRestoreStreamButton() {
        restoreMenuItem.setVisible(true);
    }

    @Override public void showLoading() {
        doneMenuItem.setActionView(R.layout.item_list_loading);
    }

    @Override public void hideLoading() {
        doneMenuItem.setActionView(null);
    }

    @Override public void showError(String message) {
        feedbackMessage.show(getView(), message);
    }

    private void resetTitleError() {
        titleErrorView.setError(null);
    }

    //endregion
}
