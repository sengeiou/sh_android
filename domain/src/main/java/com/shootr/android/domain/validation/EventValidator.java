package com.shootr.android.domain.validation;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.exception.ShootrError;
import java.util.ArrayList;
import java.util.List;

public class EventValidator {

    public static final int TITLE_MINIMUN_LENGTH = 3;
    public static final int TITLE_MAXIMUN_LENGTH = 50;
    public static final int TAG_MAXIMUM_LENGTH = 8;
    public static final String EMOJI_RANGE_REGEX = "[\\x{1F300}-\\x{1F64F}\\x{1f680}-\\x{1f6ff}\\x{2600}-\\x{27bf}]";
    public static final String ALPHANUMERIC_REGEX = "[^A-Za-z0-9 ]";

    public static final int FIELD_TITLE = 1;

    private List<FieldValidationError> fieldValidationErrors;

    public EventValidator() {
        fieldValidationErrors = new ArrayList<>();
    }

    public List<FieldValidationError> validate(Event event) {
        validateTitle(event);
        return fieldValidationErrors;
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
}
