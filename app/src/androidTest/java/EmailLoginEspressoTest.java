import android.support.test.espresso.action.TypeTextAction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.activities.registro.EmailLoginActivity;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.AllOf.allOf;

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

    @Test public void shouldGoToUserProfile() throws Exception {
        onView(isAssignableFrom(Toolbar.class)).perform(click());
    }

    @Test @Ignore public void shouldLogoutFromProfile() throws Exception {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onData(allOf(instanceOf(MenuItem.class), withTitle("Logout"))).perform(click());
    }


    static MenuItemTitleMatcher withTitle(String title) {
        return new MenuItemTitleMatcher(title);
    }

    static class MenuItemTitleMatcher extends BaseMatcher<Object> {
        private final String title;
        public MenuItemTitleMatcher(String title) { this.title = title; }

        @Override public boolean matches(Object o) {
            if (o instanceof MenuItem) {
                return ((MenuItem) o).getTitle().equals(title);
            }
            return false;
        }
        @Override public void describeTo(Description description) { }

    }
}
