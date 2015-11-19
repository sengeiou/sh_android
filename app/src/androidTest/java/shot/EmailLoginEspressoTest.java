package shot;

import android.support.test.espresso.action.TypeTextAction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.activities.registro.EmailLoginActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class EmailLoginEspressoTest {

    @Rule
    public ActivityTestRule rule = new ActivityTestRule(EmailLoginActivity.class);

    @Test public void shouldShowErrorIfUsernameAndPasswordNotCorrect() throws Exception {
        onView(withId(R.id.email_login_username_email)).perform(new TypeTextAction("artjimlop"));
        onView(withId(R.id.email_login_password)).perform(new TypeTextAction("000000"));
        onView(withId(R.id.email_login_button)).perform(click());
    }

    @Test public void shouldLoginWithUsernameAndPassword() throws Exception {
        onView(withId(R.id.email_login_username_email)).perform(new TypeTextAction("artjimlop"));
        onView(withId(R.id.email_login_password)).perform(new TypeTextAction("654321"));
        onView(withId(R.id.email_login_button)).perform(click());
    }

}
