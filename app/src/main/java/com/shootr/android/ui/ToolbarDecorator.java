package com.shootr.android.ui;

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
import com.shootr.android.R;
import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.util.PicassoWrapper;
import de.hdodenhof.circleimageview.CircleImageView;
import javax.inject.Inject;

public class ToolbarDecorator implements ViewContainerDecorator {

    private final Context context;
    private Toolbar toolbar;
    private ActionBar supportActionBar;
    private TextView titleText;
    private TextView subtitleText;
    private ViewGroup titleContainer;
    private CircleImageView circleImageView;

    @Inject PicassoWrapper picasso;
    @Inject SessionRepository sessionRepository;

    public ToolbarDecorator(Context context) {
        this.context = context;
    }

    @Override public ViewGroup decorateContainer(ViewGroup originalRoot) {
        View inflatedView = LayoutInflater.from(context).inflate(R.layout.action_bar_decor, originalRoot, true);
        toolbar = ((Toolbar) inflatedView.findViewById(R.id.toolbar_actionbar));
        titleText = (TextView) toolbar.findViewById(R.id.toolbar_title);
        subtitleText = (TextView) toolbar.findViewById(R.id.toolbar_subtitle);
        titleContainer = (ViewGroup) toolbar.findViewById(R.id.toolbar_title_container);
        circleImageView = (CircleImageView) toolbar.findViewById(R.id.toolbar_user_avatar);
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
        setTitle(getCurrentUser().getName());
        setSubtitle(getCurrentUser().getUsername());
        setAvatarImage(getCurrentUser().getPhoto());
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
        titleContainer.setOnClickListener(clickListener);
    }

    public void setSubtitle(@StringRes int subtitle) {
        setSubtitle(context.getString(subtitle));
    }

    public void setSubtitle(String subtitle) {
        if (subtitle == null) {
            hideSubtitle();
        } else {
            subtitleText.setText(subtitle);
            subtitleText.setVisibility(View.VISIBLE);
        }
    }

    public User getCurrentUser(){
        User user = sessionRepository.getCurrentUser();
        return user;
    }

    public void setAvatarImage(String imageURL) {
        if (imageURL == null) {
            hideSubtitle();
        } else {
            circleImageView.setVisibility(View.VISIBLE);
            picasso.loadProfilePhoto(imageURL).into(circleImageView);
        }
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

    public void hideSubtitle() {
        subtitleText.setVisibility(View.GONE);
    }

    private void setupTitleContainerTransitions() {
        LayoutTransition layoutTransition = titleContainer.getLayoutTransition();
        layoutTransition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        layoutTransition.setStartDelay(LayoutTransition.APPEARING, 0);
    }
}
