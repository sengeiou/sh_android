package com.shootr.android.ui.activities;

import android.test.ActivityUnitTestCase;
import com.squareup.spoon.Spoon;

import com.shootr.android.R;
import com.shootr.android.data.entity.UserEntity;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

public class MainActivityTest extends ActivityUnitTestCase<MainActivity> {

    private MainActivity activity;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        UserEntity currentUser = new UserEntity();
        currentUser.setIdUser(5L);
        //TODO make test aware of the current user
        activity = getActivity();
    }

    public void testDefaultDrawerItemIsTimeline() {
        onView(withId(R.id.menu_drawer)).check(matches(isDisplayed()));
        Spoon.screenshot(getActivity(), "MainActivity");
    }
}
