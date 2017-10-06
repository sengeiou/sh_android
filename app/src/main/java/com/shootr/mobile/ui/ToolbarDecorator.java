package com.shootr.mobile.ui;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.widgets.AvatarView;
import com.shootr.mobile.util.ImageLoader;

public class ToolbarDecorator implements ViewContainerDecorator {

  private final Context context;
  private final ImageLoader imageLoader;

  private Toolbar toolbar;
  private ActionBar supportActionBar;
  private TextView titleText;
  private TextView subtitleFilteredText;
  private ViewGroup titleContainer;
  private TextView subtitleText;
  private ImageView muteImage;
  private ImageView verifiedImage;
  private AvatarView avatar;

  public ToolbarDecorator(Context context, ImageLoader imageLoader) {
    this.context = context;
    this.imageLoader = imageLoader;
  }

  @Override public ViewGroup decorateContainer(ViewGroup originalRoot) {
    View inflatedView =
        LayoutInflater.from(context).inflate(R.layout.action_bar_decor, originalRoot, true);
    toolbar = ((Toolbar) inflatedView.findViewById(R.id.toolbar_actionbar));
    titleText = (TextView) toolbar.findViewById(R.id.toolbar_title);
    subtitleText = (TextView) toolbar.findViewById(R.id.toolbar_subtitle);
    titleContainer = (ViewGroup) toolbar.findViewById(R.id.toolbar_title_container);
    avatar = (AvatarView) toolbar.findViewById(R.id.toolbar_user_avatar);
    subtitleFilteredText = (TextView) toolbar.findViewById(R.id.toolbar_filtered_subtitle);
    muteImage = (ImageView) toolbar.findViewById(R.id.user_muted);
    verifiedImage = (ImageView) toolbar.findViewById(R.id.stream_verified);
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

  public void setSubtitle(String subtitle) {

    if (subtitle == null) {
      hideSubtitle();
    } else {
      if (subtitleFilteredText.getVisibility() == View.GONE) {
        subtitleText.setVisibility(View.VISIBLE);
      }
      subtitleText.setText(subtitle);
      subtitleText.setTextColor(Color.parseColor("#FFDAEDFB"));
    }
  }

  public void putFilterSubtitle() {
    subtitleText.setVisibility(View.GONE);
    subtitleFilteredText.setVisibility(View.VISIBLE);
  }

  public void hideFilterSubtitle() {
    subtitleText.setVisibility(
        subtitleText.getText().toString().isEmpty() ? View.GONE : View.VISIBLE);
    subtitleFilteredText.setVisibility(View.GONE);
  }

  public void showSubtitle() {
    if (subtitleFilteredText.getVisibility() != View.VISIBLE) {
      subtitleText.setVisibility(View.VISIBLE);
    }
  }

  public void setTitleClickListener(View.OnClickListener clickListener) {
    titleContainer.setOnClickListener(clickListener);
  }

  public void setAvatarImage(String imageURL, String username) {
    avatar.setVisibility(View.VISIBLE);
    imageLoader.loadProfilePhoto(imageURL, avatar, username);
  }

  public void setMutedUser(boolean isMuted) {
    if (isMuted) {
      muteImage.setVisibility(View.VISIBLE);
    } else {
      muteImage.setVisibility(View.GONE);
    }
  }

  public void setVerifiedStream(boolean isVerified) {
    if (isVerified) {
      verifiedImage.setVisibility(View.VISIBLE);
    } else {
      verifiedImage.setVisibility(View.GONE);
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
