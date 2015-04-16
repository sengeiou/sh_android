package com.shootr.android.ui;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.shootr.android.R;

public class ToolbarDecorator implements ViewContainerDecorator {

    private final Context context;
    private Toolbar toolbar;
    private ActionBar supportActionBar;
    private TextView titleText;

    public ToolbarDecorator(Context context) {
        this.context = context;
    }

    @Override public ViewGroup decorateContainer(ViewGroup originalRoot) {
        View inflatedView = LayoutInflater.from(context).inflate(R.layout.action_bar_decor, originalRoot, true);
        toolbar = ((Toolbar) inflatedView.findViewById(R.id.toolbar_actionbar));
        titleText = (TextView) toolbar.findViewById(R.id.toolbar_title);
        return (ViewGroup) inflatedView.findViewById(R.id.action_bar_activity_content);
    }

    public void bindActionbar(ActionBarActivity activity) {
        activity.setSupportActionBar(toolbar);
        supportActionBar = activity.getSupportActionBar();
        setTitle(supportActionBar.getTitle());
        supportActionBar.setDisplayShowTitleEnabled(false);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDisplayShowHomeEnabled(true);
    }

    public void setTitle(@StringRes int titleResource) {
        setTitle(context.getString(titleResource));
    }

    public void setTitle(CharSequence title) {
        if (title == null) {
            hideTitle();
        } else {
            titleText.setVisibility(View.VISIBLE);
            titleText.setText(title);
        }
    }

    public void showDropdownIcon(boolean show) {
        titleText.setCompoundDrawablesWithIntrinsicBounds(0, 0, show ? R.drawable.spinner_triangle_white : 0, 0);
    }

    public void setTitleClickListener(View.OnClickListener clickListener) {
        titleText.setOnClickListener(clickListener);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public ActionBar getActionBar() {
        return supportActionBar;
    }

    public void hideTitle() {
        titleText.setVisibility(View.GONE);
    }
}
