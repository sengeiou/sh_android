package com.shootr.android.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.presenter.ProfileEditPresenter;
import com.shootr.android.ui.views.ProfileEditView;
import javax.inject.Inject;

public class ProfileEditActivity extends BaseSignedInActivity implements ProfileEditView {

    @Inject ProfileEditPresenter presenter;

    @InjectView(R.id.scroll) ScrollView scroll;
    @InjectView(R.id.profile_edit_name) TextView name;
    @InjectView(R.id.profile_edit_username) TextView username;
    @InjectView(R.id.profile_edit_team) TextView team;
    @InjectView(R.id.profile_edit_website) TextView website;
    @InjectView(R.id.profile_edit_bio) TextView bio;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()) {
            return;
        }
        setContainerContent(R.layout.activity_profile_edit);

        ButterKnife.inject(this);

        scrollViewFocusHack();
        initializePresenter();
    }

    private void scrollViewFocusHack() {
        scroll.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        scroll.setFocusable(true);
        scroll.setFocusableInTouchMode(true);
        scroll.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                v.requestFocusFromTouch();
                return false;
            }
        });
    }

    private void initializePresenter() {
        presenter.initialize(this);
    }

    @Override public void renderUserInfo(UserModel userModel) {
        name.setText(userModel.getName());
        username.setText(userModel.getUsername());
        team.setText(userModel.getFavoriteTeamName());
        website.setText(userModel.getWebsite());
        bio.setText(userModel.getBio());
    }

    @Override public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(name.getWindowToken(), 0);
    }
}
