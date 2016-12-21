package com.shootr.mobile.domain.asserts;

import com.shootr.mobile.domain.model.shot.BaseMessage;
import com.shootr.mobile.domain.model.shot.QueuedShot;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.util.Objects;

/**
 * {@link QueuedShot} specific assertions - Generated by CustomAssertionGenerator.
 */
public class QueuedShotAssert extends AbstractAssert<QueuedShotAssert, QueuedShot> {

    /**
     * Creates a new <code>{@link QueuedShotAssert}</code> to make assertions on actual QueuedShot.
     *
     * @param actual the QueuedShot we want to make assertions on.
     */
    public QueuedShotAssert(QueuedShot actual) {
        super(actual, QueuedShotAssert.class);
    }

    /**
     * An entry point for QueuedShotAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
     * With a static import, one can write directly: <code>assertThat(myQueuedShot)</code> and get specific
     * assertion
     * with code completion.
     *
     * @param actual the QueuedShot we want to make assertions on.
     * @return a new <code>{@link QueuedShotAssert}</code>
     */
    public static QueuedShotAssert assertThat(QueuedShot actual) {
        return new QueuedShotAssert(actual);
    }

    /**
     * Verifies that the actual QueuedShot is failed.
     *
     * @return this assertion object.
     * @throws AssertionError - if the actual QueuedShot is not failed.
     */
    public QueuedShotAssert isFailed() {
        // check that actual QueuedShot we want to make assertions on is not null.
        isNotNull();

        // check
        if (!actual.isFailed()) {
            failWithMessage("\nExpected actual QueuedShot to be failed but was not.");
        }

        // return the current assertion for method chaining
        return this;
    }

    /**
     * Verifies that the actual QueuedShot is not failed.
     *
     * @return this assertion object.
     * @throws AssertionError - if the actual QueuedShot is failed.
     */
    public QueuedShotAssert isNotFailed() {
        // check that actual QueuedShot we want to make assertions on is not null.
        isNotNull();

        // check
        if (actual.isFailed()) {
            failWithMessage("\nExpected actual QueuedShot not to be failed but was.");
        }

        // return the current assertion for method chaining
        return this;
    }

    /**
     * Verifies that the actual QueuedShot's idQueue is equal to the given one.
     *
     * @param idQueue the given idQueue to compare the actual QueuedShot's idQueue to.
     * @return this assertion object.
     * @throws AssertionError - if the actual QueuedShot's idQueue is not equal to the given one.
     */
    public QueuedShotAssert hasIdQueue(Long idQueue) {
        // check that actual QueuedShot we want to make assertions on is not null.
        isNotNull();

        // overrides the default error message with a more explicit one
        String assertjErrorMessage = "\nExpected idQueue of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

        // null safe check
        Long actualIdQueue = actual.getIdQueue();
        if (!Objects.areEqual(actualIdQueue, idQueue)) {
            failWithMessage(assertjErrorMessage, actual, idQueue, actualIdQueue);
        }

        // return the current assertion for method chaining
        return this;
    }

    /**
     * Verifies that the actual QueuedShot's imageFile is equal to the given one.
     *
     * @param imageFile the given imageFile to compare the actual QueuedShot's imageFile to.
     * @return this assertion object.
     * @throws AssertionError - if the actual QueuedShot's imageFile is not equal to the given one.
     */
    public QueuedShotAssert hasImageFile(java.io.File imageFile) {
        // check that actual QueuedShot we want to make assertions on is not null.
        isNotNull();

        // overrides the default error message with a more explicit one
        String assertjErrorMessage = "\nExpected imageFile of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

        // null safe check
        java.io.File actualImageFile = actual.getImageFile();
        if (!Objects.areEqual(actualImageFile, imageFile)) {
            failWithMessage(assertjErrorMessage, actual, imageFile, actualImageFile);
        }

        // return the current assertion for method chaining
        return this;
    }

    /**
     * Verifies that the actual QueuedShot's shot is equal to the given one.
     *
     * @param shot the given shot to compare the actual QueuedShot's shot to.
     * @return this assertion object.
     * @throws AssertionError - if the actual QueuedShot's shot is not equal to the given one.
     */
    public QueuedShotAssert hasShot(BaseMessage shot) {
        // check that actual QueuedShot we want to make assertions on is not null.
        isNotNull();

        // overrides the default error message with a more explicit one
        String assertjErrorMessage = "\nExpected shot of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";

        // null safe check
        BaseMessage actualShot = actual.getBaseMessage();
        if (!Objects.areEqual(actualShot, shot)) {
            failWithMessage(assertjErrorMessage, actual, shot, actualShot);
        }

        // return the current assertion for method chaining
        return this;
    }
}
