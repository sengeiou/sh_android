package shot;

import android.content.Context;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.activities.StreamTimelineActivity;
import com.shootr.mobile.ui.model.ShotModel;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.*;

public class GoToShotDetailEspressoTest {

    @Rule
    public ActivityTestRule<StreamTimelineActivity> rule =
      new ActivityTestRule<StreamTimelineActivity>(StreamTimelineActivity.class) {
          @Override
          protected Intent getActivityIntent() {
              Context targetContext = getInstrumentation()
                .getTargetContext();
              Intent result = new Intent(targetContext, StreamTimelineActivity.class);
              result.putExtra("streamId", "563a3239e4b0149d10d68d2b");
              result.putExtra("userId", "563a114fe4b076203c9738a3");
              result.putExtra("streamShortTitle", "Sálvame Diario");
              return result;
          }
      };

    @Test public void shouldGoShotDetail() throws Exception {
        onData(is(instanceOf(ShotModel.class))).inAdapterView(withId(R.id.timeline_shot_list)).atPosition(0).perform(
          click());
    }

}
