package shot;

import android.support.test.rule.ActivityTestRule;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.activities.MainTabbedActivity;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class GoToNewStreamEspressoTest {

    @Rule
    public ActivityTestRule rule = new ActivityTestRule(MainTabbedActivity.class);

    @Test public void shouldGoToNewStream() throws Exception {
        onView(withId(R.id.streams_add_stream)).perform(click());
    }

}
