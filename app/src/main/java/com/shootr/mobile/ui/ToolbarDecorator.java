package com.shootr.mobile.ui;

import android.animation.LayoutTransition;
import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.shootr.mobile.R;
import com.shootr.mobile.util.ImageLoader;
import de.hdodenhof.circleimageview.CircleImageView;

public class ToolbarDecorator implements ViewContainerDecorator {

    private final Context context;
    private final ImageLoader imageLoader;

    private Toolbar toolbar;
    private ActionBar supportActionBar;
    private TextView titleText;
    private ViewGroup titleContainer;

    private CircleImageView avatar;

    public ToolbarDecorator(Context context, ImageLoader imageLoader) {
        this.context = context;
        this.imageLoader = imageLoader;
    }

    @Override public ViewGroup decorateContainer(ViewGroup originalRoot) {
        View inflatedView = LayoutInflater.from(context).inflate(R.layout.action_bar_decor, originalRoot, true);
        toolbar = ((Toolbar) inflatedView.findViewById(R.id.toolbar_actionbar));
        titleText = (TextView) toolbar.findViewById(R.id.toolbar_title);
        titleContainer = (ViewGroup) toolbar.findViewById(R.id.toolbar_title_container);
        avatar = (CircleImageView) toolbar.findViewById(R.id.toolbar_user_avatar);
        setupTitleContainerTransitions();
        return (ViewGroup) inflatedView.findViewById(R.id.action_bar_activity_content);
    }

    public void bindActionbar(ActionBarActivity activity) {
        activity.setSupportActionBar(toolbar);
        supportActionBar = activity.getSupportActionBar();
        setTitle(supportActionBar.getTitle());
        supportActionBar.setDisplayShowTitleEnabled(false);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDisplayShowHomeEnabled(true);
        titleContainer.setVisibility(View.VISIBLE);
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

    public void setTitleClickListener(View.OnClickListener clickListener) {
        titleContainer.setOnClickListener(clickListener);
    }

    public void setAvatarImage(String imageURL) {
        avatar.setVisibility(View.VISIBLE);
        imageLoader.loadProfilePhoto(imageURL,avatar);
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

    private void setupTitleContainerTransitions() {
        LayoutTransition layoutTransition = titleContainer.getLayoutTransition();
        layoutTransition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        layoutTransition.setStartDelay(LayoutTransition.APPEARING, 0);
    }

}
