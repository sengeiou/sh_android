package gm.mobi.android.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import gm.mobi.android.R;
import timber.log.Timber;

public class FollowButton extends FrameLayout {

    private View followButton;
    private View followingButton;
    private boolean isFollowing;

    public FollowButton(Context context) {
        super(context);
        init();
    }

    public FollowButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FollowButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setClickable(true);
        LayoutInflater inflater = LayoutInflater.from(getContext());

        followButton = inflater.inflate(R.layout.button_follow, this, false);
        followingButton = inflater.inflate(R.layout.button_following, this, false);

        addView(followButton);
        addView(followingButton);


        if (isInEditMode()) {
            setFollowing(false);
        }
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    public void setFollowing(boolean isFollowing) {
        this.isFollowing = isFollowing;
        if (isFollowing) {
            followButton.setVisibility(GONE);
            followingButton.setVisibility(VISIBLE);
        } else {
            followButton.setVisibility(VISIBLE);
            followingButton.setVisibility(GONE);
        }
        //TODO editProfile GONE
    }

    public void setEditProfile() {
        followButton.setVisibility(GONE);
        followingButton.setVisibility(GONE);
    }

    public boolean isEditProfile() {
        return false;
    }

    public boolean isFollowing() {
        if(followButton.getVisibility() == View.GONE && followingButton.getVisibility() == View.VISIBLE){
            return true;
        }
        return false;
    }
}
