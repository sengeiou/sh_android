package com.shootr.mobile.ui.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.shootr.mobile.R;
import com.shootr.mobile.util.ImageLoader;
import java.lang.ref.WeakReference;

public class InAppNotificationView implements View.OnClickListener {

  private static final int DEFAULT_VALUE = -100000;
  private static int height = DEFAULT_VALUE;
  private static String title = "";
  private static String message = "";
  private static String avatar = "";
  private static boolean autoHide = true;
  private static int duration = 3000;
  private static WeakReference<View> layoutWeakReference;
  private static WeakReference<Activity> contextWeakReference;
  private static InAppClickListener listener = null;
  private static ImageLoader imageLoader;


  private InAppNotificationView(Activity activity) {
    contextWeakReference = new WeakReference<>(activity);
  }

  public static InAppNotificationView with(Activity activity) {
    InAppNotificationView notification = new InAppNotificationView(activity);
    setDefault();
    return notification;
  }

  public static void hide() {
    if (getLayout() != null) {
      getLayout().startAnimation(
          AnimationUtils.loadAnimation(getContext(), com.irozon.sneaker.R.anim.popup_hide));
      getActivityDecorView().removeView(getLayout());
    }
  }

  private static ViewGroup getActivityDecorView() {
    ViewGroup decorView = null;

    decorView = (ViewGroup) ((Activity) getContext()).getWindow().getDecorView();

    return decorView;
  }

  private static void setDefault() {
    title = "";
    autoHide = true;
    height = DEFAULT_VALUE;
    listener = null;
  }

  private static Context getContext() {
    return contextWeakReference.get();
  }

  private static View getLayout() {
    return layoutWeakReference.get();
  }

  public InAppNotificationView setTitle(String title) {
    InAppNotificationView.title = title;
    return this;
  }

  public InAppNotificationView setImageLoader(ImageLoader imageLoader) {
    InAppNotificationView.imageLoader = imageLoader;
    return this;
  }

  public InAppNotificationView setMessage(String message) {
    InAppNotificationView.message = message;
    return this;
  }

  public InAppNotificationView setHeight(int height) {
    InAppNotificationView.height = height;
    return this;
  }

  public InAppNotificationView setAvatar(String avatar) {
    InAppNotificationView.avatar = avatar;
    return this;
  }

  public InAppNotificationView setDuration(int duration) {
    InAppNotificationView.duration = duration;
    return this;
  }

  public InAppNotificationView setOnSneakerClickListener(InAppClickListener listener) {
    InAppNotificationView.listener = listener;
    return this;
  }

  public void show() {
    if (getContext() != null) {
      sneakView();
    }
  }

  private void sneakView() {

    LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    View view = layoutInflater.inflate(R.layout.app_notification, null);

    layoutWeakReference = new WeakReference<>(view);
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height
        == DEFAULT_VALUE ? (getStatusBarHeight() + convertToDp(56)) : convertToDp(height));
    view.setLayoutParams(layoutParams);


    LinearLayout layout = (LinearLayout) view.findViewById(R.id.in_app_container);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      layout.setElevation(6);
    }

    TextView titleText = (TextView) view.findViewById(R.id.title);
    titleText.setText(title);

    TextView messageText = (TextView) view.findViewById(R.id.message);
    messageText.setText(message);

    AvatarView image = (AvatarView) view.findViewById(R.id.image);

    imageLoader.loadProfilePhoto(avatar, image, title);


    final ViewGroup viewGroup = getActivityDecorView();
    getExistingOverlayInViewAndRemove(viewGroup);

    view.setOnClickListener(this);
    viewGroup.addView(view);

    view.startAnimation(AnimationUtils.loadAnimation(getContext(), com.irozon.sneaker.R.anim.popup_show));
    if (autoHide) {
      Handler handler = new Handler();
      handler.removeCallbacks(null);
      handler.postDelayed(new Runnable() {
        @Override
        public void run() {
          getLayout().startAnimation(AnimationUtils.loadAnimation(getContext(), com.irozon.sneaker.R.anim.popup_hide));
          viewGroup.removeView(getLayout());
        }
      }, duration);
    }
  }

  public void getExistingOverlayInViewAndRemove(ViewGroup parent) {

    for (int i = 0; i < parent.getChildCount(); i++) {

      View child = parent.getChildAt(i);
      if (child.getId() == R.id.in_app_container) {
        parent.removeView(child);
      }
      if (child instanceof ViewGroup) {
        getExistingOverlayInViewAndRemove((ViewGroup) child);
      }
    }
  }

  private int getStatusBarHeight() {
    Rect rectangle = new Rect();
    Window window = ((Activity) getContext()).getWindow();
    window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
    int statusBarHeight = rectangle.top;
    int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
    int titleBarHeight = contentViewTop - statusBarHeight;

    return statusBarHeight;
  }

  private int convertToDp(float sizeInDp) {
    float scale = getContext().getResources().getDisplayMetrics().density;
    return (int) (sizeInDp * scale + 0.5f);
  }

  @Override
  public void onClick(View view) {
    if (listener != null) {
      listener.onClick(view);
    }
    getLayout().startAnimation(AnimationUtils.loadAnimation(getContext(), com.irozon.sneaker.R.anim.popup_hide));
    getActivityDecorView().removeView(getLayout());
  }

  public interface InAppClickListener {
    void onClick(View view);
  }
}
