package stream;

import android.support.test.espresso.action.TypeTextAction;
import android.support.test.rule.ActivityTestRule;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.activities.NewStreamActivity;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

public class CreateStreamEspressoTest {

    @Rule public ActivityTestRule rule = new ActivityTestRule(NewStreamActivity.class);

    @Test public void shouldFillStreamData() throws Exception {
        onView(withId(R.id.new_stream_title)).perform(new TypeTextAction("Test"));
        onView(withId(R.id.new_stream_description)).perform(new TypeTextAction("Test Description"));
        onView(withId(R.id.menu_done)).perform(click());
    }
}
