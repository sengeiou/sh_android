import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.shootr.mobile.R;
import com.shootr.mobile.ui.activities.registro.LoginSelectionActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class LoginSelectionActivityEspressoTest {

    @Rule public IntentsTestRule<LoginSelectionActivity> activityTestRule =
      new IntentsTestRule<>(LoginSelectionActivity.class);

    @Test
    public void emailRegistrationButtonWorksIntentTest() {
        onView(withId(R.id.login_buttons))
          .check(new ViewAssertion() {
              @Override public void check(View view, NoMatchingViewException noViewFoundException) {
                  view.findViewById(R.id.login_btn_facebook);
                  view.findViewById(R.id.login_btn_email);
                  view.findViewById(R.id.login_btn_login);
              }
          });
        onView(withId(R.id.login_btn_email)).perform(click());
    }

    @Test
    public void signUpButtonWorksIntentTest() {
        onView(withId(R.id.login_buttons))
          .check(new ViewAssertion() {
              @Override public void check(View view, NoMatchingViewException noViewFoundException) {
                  view.findViewById(R.id.login_btn_facebook);
                  view.findViewById(R.id.login_btn_email);
                  view.findViewById(R.id.login_btn_login);
              }
          });
        onView(withId(R.id.login_btn_login)).perform(click());
    }
}
