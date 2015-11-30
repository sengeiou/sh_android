package logout;

import android.content.Context;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.view.MenuItem;
import com.shootr.mobile.ui.activities.ProfileContainerActivity;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.AllOf.allOf;

public class LogoutEspressoTest {

    @Rule
    public ActivityTestRule<ProfileContainerActivity> rule =
      new ActivityTestRule<ProfileContainerActivity>(ProfileContainerActivity.class) {
          @Override
          protected Intent getActivityIntent() {
              Context targetContext = getInstrumentation()
                .getTargetContext();
              Intent result = new Intent(targetContext, ProfileContainerActivity.class);
              result.putExtra("user", "554c7366e4b0cba72c91a828");
              return result;
          }
      };

    @Test public void shouldClickLogout() throws Exception {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onData(allOf(instanceOf(MenuItem.class), withTitle("Logout"))).perform(click());
    }

    private MenuItemTitleMatcher withTitle(String title) {
        return new MenuItemTitleMatcher(title);
    }

    private class MenuItemTitleMatcher extends BaseMatcher<Object> {
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
