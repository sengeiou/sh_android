package com.shootr.android.domain.validation;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.exception.ShootrError;
import java.util.ArrayList;
import java.util.List;

public class EventValidator {

    public static final int TITLE_MINIMUN_LENGTH = 3;
    public static final int TITLE_MAXIMUN_LENGTH = 50;
    public static final int TAG_MINIMUN_LENGTH = 3;
    public static final int TAG_MAXIMUM_LENGTH = 15;
    public static final String EMOJI_RANGE_REGEX = "[\\x{1F300}-\\x{1F64F}\\x{1f680}-\\x{1f6ff}\\x{2600}-\\x{27bf}]";
    public static final String ALPHANUMERIC_REGEX = "[^A-Za-z0-9 ]";

    public static final int FIELD_TITLE = 1;
    public static final int FIELD_SHORT_TITLE = 2;

    private List<FieldValidationError> fieldValidationErrors;

    public EventValidator() {
        fieldValidationErrors = new ArrayList<>();
    }

    public List<FieldValidationError> validate(Event event) {
        validateTitle(event);
        validateShortTitle(event);
        return fieldValidationErrors;
    }

    private void validateShortTitle(Event event) {
        validateShortTitleTooShort(event);
        validateShortTitleTooLong(event);
    }

    //region Title
    private void validateTitle(Event event) {
        validateTitleTooShort(event);
        validateTitleTooLong(event);
    }

    private void validateTitleTooShort(Event event) {
        String title = event.getTitle();
        if (title == null || alphanumericLength(title) < TITLE_MINIMUN_LENGTH) {
            fieldValidationErrors.add(
              new FieldValidationError(ShootrError.ERROR_CODE_EVENT_TITLE_TOO_SHORT, FIELD_TITLE));
        }
    }

    private int alphanumericLength(String title) {
        return title.replaceAll(ALPHANUMERIC_REGEX, "").length();
    }

    private void validateTitleTooLong(Event event) {
        if (event.getTitle() != null && alphanumericLength(event.getTitle()) > TITLE_MAXIMUN_LENGTH) {
            fieldValidationErrors.add(
              new FieldValidationError(ShootrError.ERROR_CODE_EVENT_TITLE_TOO_LONG, FIELD_TITLE));
        }
    }
    //endregion

    //region Short Title
    private void validateShortTitleTooLong(Event event) {
        if (event.getTag() != null && alphanumericLength(event.getTag()) > TAG_MAXIMUM_LENGTH) {
            fieldValidationErrors.add(
              new FieldValidationError(ShootrError.ERROR_SUBCODE_TAG_TOO_LONG, FIELD_SHORT_TITLE));
        }
    }

    private void validateShortTitleTooShort(Event event) {
        String shortTitle = event.getTag();
        if (shortTitle == null || alphanumericLength(shortTitle) < TAG_MINIMUN_LENGTH) {
            fieldValidationErrors.add(
              new FieldValidationError(ShootrError.ERROR_SUBCODE_TAG_TOO_SHORT, FIELD_SHORT_TITLE));
        }
    }
    //endregion

}
