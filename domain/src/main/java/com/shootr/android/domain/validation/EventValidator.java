package com.shootr.android.domain.validation;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.exception.ShootrError;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventValidator {

    public static final int TITLE_MINIMUN_LENGTH = 3;
    public static final int TITLE_MAXIMUN_LENGTH = 50;
    public static final int TAG_MAXIMUM_LENGTH = 8;
    public static final String EMOJI_RANGE_REGEX = "[^\\u1f300-\\u1f64f]";
    public static final String ALPHANUMERIC_REGEX = "[^A-Za-z0-9 ]";

    private static final long ONE_YEAR_MILLIS = 365 * 24 * 60 * 60 * 1000;

    public static final int FIELD_TITLE = 1;
    public static final int FIELD_START_DATE = 2;
    public static final int FIELD_END_DATE = 3;

    private List<FieldValidationError> fieldValidationErrors;

    public EventValidator() {
        fieldValidationErrors = new ArrayList<>();
    }

    public List<FieldValidationError> validate(Event event) {
        validateTitle(event);
        validateStartDate(event);
        validateEndDate(event);
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

    //region Start Date
    private void validateStartDate(Event event) {
        validateStartDateNotMoreThanOneYear(event);
    }

    private void validateStartDateNotMoreThanOneYear(Event event) {
        if (event.getStartDate().after(oneYearFromNow())) {
            fieldValidationErrors.add(
              new FieldValidationError(ShootrError.ERROR_CODE_EVENT_START_DATE_TOO_LATE, FIELD_START_DATE));
        }
    }
    //endregion

    //region End date
    private void validateEndDate(Event event) {
        validateEndDateAfterNow(event);
        validateEndDateAfterStartDate(event);
        validateEndDateNotMoreThanOneYearAferStartDate(event);
    }

    private void validateEndDateAfterNow(Event event) {
        if (event.getEndDate().before(new Date())) {
            fieldValidationErrors.add(new FieldValidationError(ShootrError.ERROR_CODE_EVENT_END_DATE_BEFORE_NOW, FIELD_END_DATE));
        }
    }

    private void validateEndDateNotMoreThanOneYearAferStartDate(Event event) {
        if (event.getEndDate().after(oneyearFrom(event.getStartDate()))) {
            fieldValidationErrors.add(new FieldValidationError(ShootrError.ERROR_CODE_EVENT_END_DATE_TOO_LATE, FIELD_END_DATE));
        }
    }

    private void validateEndDateAfterStartDate(Event event) {
        if (event.getEndDate().before(event.getStartDate())) {
            fieldValidationErrors.add(new FieldValidationError(ShootrError.ERROR_CODE_EVENT_END_DATE_BEFORE_START, FIELD_END_DATE));
        }
    }
    //endregion

    private Date oneyearFrom(Date date) {
        return new Date(date.getTime() + ONE_YEAR_MILLIS);
    }

    private Date oneYearFromNow() {
        return oneyearFrom(new Date());
    }
}
