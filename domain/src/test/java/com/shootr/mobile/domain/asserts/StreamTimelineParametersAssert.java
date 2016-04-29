package com.shootr.mobile.domain.asserts;

import com.shootr.mobile.domain.StreamTimelineParameters;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.util.Objects;

/**
 * {@link StreamTimelineParameters} specific assertions - Generated by CustomAssertionGenerator.
 */
public class StreamTimelineParametersAssert
  extends AbstractAssert<StreamTimelineParametersAssert, StreamTimelineParameters> {

    /**
     * Creates a new <code>{@link StreamTimelineParametersAssert}</code> to make assertions on actual
     * TimelineParameters.
     *
     * @param actual the TimelineParameters we want to make assertions on.
     */
    public StreamTimelineParametersAssert(StreamTimelineParameters actual) {
        super(actual, StreamTimelineParametersAssert.class);
    }

    /**
     * An entry point for TimelineParametersAssert to follow AssertJ standard <code>assertThat()</code>
     * statements.<br>
     * With a static import, one can write directly: <code>assertThat(myTimelineParameters)</code> and get
     * specific
     * assertion with code completion.
     *
     * @param actual the TimelineParameters we want to make assertions on.
     * @return a new <code>{@link StreamTimelineParametersAssert}</code>
     */
    public static StreamTimelineParametersAssert assertThat(StreamTimelineParameters actual) {
        return new StreamTimelineParametersAssert(actual);
    }

    /**
     * Verifies that the actual TimelineParameters's streamId is equal to the given one.
     *
     * @param streamId the given streamId to compare the actual TimelineParameters's streamId to.
     * @return this assertion object.
     * @throws AssertionError - if the actual TimelineParameters's streamId is not equal to the given one.
     */
    public StreamTimelineParametersAssert hasStreamId(String streamId) {
        // check that actual TimelineParameters we want to make assertions on is not null.
        isNotNull();

        // overrides the default error message with a more explicit one
        String assertjErrorMessage = "\nExpected streamId of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

        // null safe check
        String actualStreamId = actual.getStreamId();
        if (!Objects.areEqual(actualStreamId, streamId)) {
            failWithMessage(assertjErrorMessage, actual, streamId, actualStreamId);
        }

        // return the current assertion for method chaining
        return this;
    }

    public StreamTimelineParametersAssert hasNoStreamId() {
        isNotNull();

        String assertjErrorMessage = "\nExpected :\n  <%s>\nnot to have streamId but had :\n  <%s>";

        // check
        if (actual.getStreamId() != null) {
            failWithMessage(assertjErrorMessage, actual, actual.getStreamId());
        }

        // return the current assertion for method chaining
        return this;
    }

    /**
     * Verifies that the actual TimelineParameters's maxDate is equal to the given one.
     *
     * @param maxDate the given maxDate to compare the actual TimelineParameters's maxDate to.
     * @return this assertion object.
     * @throws AssertionError - if the actual TimelineParameters's maxDate is not equal to the given one.
     */
    public StreamTimelineParametersAssert hasMaxDate(java.util.Date maxDate) {
        // check that actual TimelineParameters we want to make assertions on is not null.
        isNotNull();

        // overrides the default error message with a more explicit one
        String assertjErrorMessage = "\nExpected maxDate of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

        // null safe check
        Long actualMaxDate = actual.getMaxDate();
        if (!Objects.areEqual(actualMaxDate, maxDate)) {
            failWithMessage(assertjErrorMessage, actual, maxDate, actualMaxDate);
        }

        // return the current assertion for method chaining
        return this;
    }

    /**
     * Verifies that the actual TimelineParameters's sinceDate is equal to the given one.
     *
     * @param sinceDate the given sinceDate to compare the actual TimelineParameters's sinceDate to.
     * @return this assertion object.
     * @throws AssertionError - if the actual TimelineParameters's sinceDate is not equal to the given one.
     */
    public StreamTimelineParametersAssert hasSinceDate(Long sinceDate) {
        // check that actual TimelineParameters we want to make assertions on is not null.
        isNotNull();

        // overrides the default error message with a more explicit one
        String assertjErrorMessage = "\nExpected sinceDate of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

        // null safe check
        Long actualSinceDate = actual.getSinceDate();
        if (!Objects.areEqual(actualSinceDate, sinceDate)) {
            failWithMessage(assertjErrorMessage, actual, sinceDate, actualSinceDate);
        }

        // return the current assertion for method chaining
        return this;
    }
}
