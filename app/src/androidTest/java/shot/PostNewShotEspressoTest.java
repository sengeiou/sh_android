package shot;

import android.support.test.espresso.action.TypeTextAction;
import android.support.test.rule.ActivityTestRule;

import com.shootr.mobile.R;
import com.shootr.mobile.ui.activities.PostNewShotActivity;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class PostNewShotEspressoTest {

    @Rule
    public ActivityTestRule rule = new ActivityTestRule(PostNewShotActivity.class);

    @Test public void shouldWriteShot() throws Exception {
        onView(withId(R.id.new_shot_text)).perform(new TypeTextAction("test"));
    }

    @Test public void shouldSendShot() throws Exception {
        onView(withId(R.id.new_shot_send_button)).perform(click());
    }

}
