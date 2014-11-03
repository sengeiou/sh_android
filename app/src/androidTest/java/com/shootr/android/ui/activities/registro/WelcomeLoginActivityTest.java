package com.shootr.android.ui.activities.registro;

import android.test.ActivityInstrumentationTestCase2;
import com.squareup.spoon.Spoon;
import com.shootr.android.R;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;


public class WelcomeLoginActivityTest extends ActivityInstrumentationTestCase2<WelcomeLoginActivity> {

    public WelcomeLoginActivityTest() {
        super(WelcomeLoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    public void testActivitySignInButtonVisible() {
        onView(withId(R.id.login_btn_login)).check(matches(isDisplayed()));
        Spoon.screenshot(getActivity(), "WelcomeLogin");
    }
}
