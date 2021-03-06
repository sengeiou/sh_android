package com.shootr.mobile.domain.asserts;

import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.stream.Timeline;
import java.util.List;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.internal.Iterables;

/**
 * {@link Timeline} specific assertions - Generated by CustomAssertionGenerator.
 */
public class TimelineAssert extends AbstractAssert<TimelineAssert, Timeline> {

    /**
     * Creates a new <code>{@link TimelineAssert}</code> to make assertions on actual Timeline.
     *
     * @param actual the Timeline we want to make assertions on.
     */
    public TimelineAssert(Timeline actual) {
        super(actual, TimelineAssert.class);
    }

    /**
     * An entry point for TimelineAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
     * With a static import, one can write directly: <code>assertThat(myTimeline)</code> and get specific assertion
     * with
     * code completion.
     *
     * @param actual the Timeline we want to make assertions on.
     * @return a new <code>{@link TimelineAssert}</code>
     */
    public static TimelineAssert assertThat(Timeline actual) {
        return new TimelineAssert(actual);
    }

    /**
     * Verifies that the actual Timeline's shots contains the given Shot elements.
     *
     * @param shots the given elements that should be contained in actual Timeline's shots.
     * @return this assertion object.
     * @throws AssertionError if the actual Timeline's shots does not contain all given Shot elements.
     */
    public TimelineAssert hasShots(List<Shot> shots) {
        // check that actual Timeline we want to make assertions on is not null.
        isNotNull();

        // check that given Shot varargs is not null.
        if (shots == null) throw new AssertionError("Expecting shots parameter not to be null.");

        Iterables.instance().assertContainsAll(info, actual.getShots(), shots);

        // return the current assertion for method chaining
        return this;
    }

    /**
     * Verifies that the actual Timeline has no shots.
     *
     * @return this assertion object.
     * @throws AssertionError if the actual Timeline's shots is not empty.
     */
    public TimelineAssert hasNoShots() {
        // check that actual Timeline we want to make assertions on is not null.
        isNotNull();

        // we override the default error message with a more explicit one
        String assertjErrorMessage = "\nExpected :\n  <%s>\nnot to have shots but had :\n  <%s>";

        // check
        if (actual.getShots().iterator().hasNext()) {
            failWithMessage(assertjErrorMessage, actual, actual.getShots());
        }

        // return the current assertion for method chaining
        return this;
    }
}
