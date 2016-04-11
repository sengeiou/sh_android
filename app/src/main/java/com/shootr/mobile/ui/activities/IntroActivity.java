package com.shootr.mobile.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.fragments.IntroSlideFragment;

public class IntroActivity extends AppIntro {

    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(IntroSlideFragment.newInstance(R.layout.intro_shootr));
        addSlide(IntroSlideFragment.newInstance(R.layout.intro_streams));
        addSlide(IntroSlideFragment.newInstance(R.layout.intro_friends));
        addSlide(IntroSlideFragment.newInstance(R.layout.intro_create));

        setSeparatorColor(Color.TRANSPARENT);
        setSkipText(getString(R.string.intro_start_shooting_button));
        setDoneText(getString(R.string.intro_start_shooting_button));
    }

    private void loadMainActivity(){
        Intent intent = new Intent(this, MainTabbedActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSkipPressed() {
        loadMainActivity();
    }

    @Override
    public void onDonePressed() {
        loadMainActivity();
    }

}
