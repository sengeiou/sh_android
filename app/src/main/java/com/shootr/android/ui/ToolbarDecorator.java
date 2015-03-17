package com.shootr.android.ui;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.android.R;

public class ToolbarDecorator implements ViewContainerDecorator {

    private final Context context;
    private Toolbar toolbar;
    private ActionBar supportActionBar;

    public ToolbarDecorator(Context context) {
        this.context = context;
    }

    @Override public ViewGroup decorateContainer(ViewGroup originalRoot) {
        View inflatedView = LayoutInflater.from(context).inflate(R.layout.action_bar_decor, originalRoot, true);
        toolbar = ((Toolbar) inflatedView.findViewById(R.id.toolbar_actionbar));
        return (ViewGroup) inflatedView.findViewById(R.id.action_bar_activity_content);
    }

    public void bindActionbar(ActionBarActivity activity) {
        activity.setSupportActionBar(toolbar);
        supportActionBar = activity.getSupportActionBar();
    }

    public void setTitle(@StringRes int titleResource) {
        toolbar.setTitle(titleResource);
    }

    public void setTitle(String title) {
        toolbar.setTitle(title);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public ActionBar getActionBar() {
        return supportActionBar;
    }
}
