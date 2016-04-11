package stream;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.activities.MainTabbedActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class) public class GoToStreamEspressoTest {

    @Rule public ActivityTestRule rule = new ActivityTestRule(MainTabbedActivity.class);

    @Test public void shouldGoToStream() throws Exception {
        onView(withId(R.id.streams_list)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
    }
}
