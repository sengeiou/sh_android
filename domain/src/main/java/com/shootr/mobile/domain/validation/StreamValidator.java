package com.shootr.mobile.domain.validation;

import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.exception.ShootrError;

import java.util.ArrayList;
import java.util.List;

public class StreamValidator {

    public static final int TITLE_MINIMUN_LENGTH = 3;
    public static final int TITLE_MAXIMUN_LENGTH = 50;
    public static final int SHORT_TITLE_MINIMUN_LENGTH = 3;
    public static final int SHORT_TITLE_MAXIMUM_LENGTH = 20;
    public static final int DESCRIPTION_MAXIMUM_LENGTH = 60;
    public static final String EMOJI_RANGE_REGEX = "[\\x{1F300}-\\x{1F64F}\\x{1f680}-\\x{1f6ff}\\x{2600}-\\x{27bf}]";

    public static final String ALPHANUMERIC_REGEX = "[^A-Za-z0-9 ]";
    public static final int FIELD_TITLE = 1;
    public static final int FIELD_SHORT_TITLE = 2;
    public static final int FIELD_DESCRIPTION = 3;

    private List<FieldValidationError> fieldValidationErrors;

    public StreamValidator() {
        fieldValidationErrors = new ArrayList<>();
    }

    public List<FieldValidationError> validate(Stream stream) {
        validateTitle(stream);
        validateShortTitle(stream);
        validateDescription(stream);
        return fieldValidationErrors;
    }

    private void validateDescription(Stream stream) {
        validateDescriptionTooLong(stream);
    }

    private void validateShortTitle(Stream stream) {
        validateShortTitleTooShort(stream);
        validateShortTitleTooLong(stream);
    }

    //region Title
    private void validateTitle(Stream stream) {
        validateTitleTooShort(stream);
        validateTitleTooLong(stream);
    }

    private void validateTitleTooShort(Stream stream) {
        String title = stream.getTitle();
        if (title == null || title.length() < TITLE_MINIMUN_LENGTH) {
            fieldValidationErrors.add(new FieldValidationError(ShootrError.ERROR_CODE_STREAM_TITLE_TOO_SHORT,
              FIELD_TITLE));
        }
    }

    private int alphanumericLength(String title) {
        return title.replaceAll(ALPHANUMERIC_REGEX, "").length();
    }

    private void validateTitleTooLong(Stream stream) {
        if (stream.getTitle() != null && alphanumericLength(stream.getTitle()) > TITLE_MAXIMUN_LENGTH) {
            fieldValidationErrors.add(new FieldValidationError(ShootrError.ERROR_CODE_STREAM_TITLE_TOO_LONG,
              FIELD_TITLE));
        }
    }

    //endregion

    //region Short Title
    private void validateShortTitleTooLong(Stream stream) {
        if (stream.getShortTitle() != null && alphanumericLength(stream.getShortTitle()) > SHORT_TITLE_MAXIMUM_LENGTH) {
            fieldValidationErrors.add(new FieldValidationError(ShootrError.ERROR_SUBCODE_SHORT_TITLE_TOO_LONG,
              FIELD_SHORT_TITLE));
        }
    }

    private void validateShortTitleTooShort(Stream stream) {
        String shortTitle = stream.getShortTitle();
        if (shortTitle == null || shortTitle.length() < SHORT_TITLE_MINIMUN_LENGTH) {
            fieldValidationErrors.add(new FieldValidationError(ShootrError.ERROR_SUBCODE_SHORT_TITLE_TOO_SHORT,
              FIELD_SHORT_TITLE));
        }
    }

    //endregion

    private void validateDescriptionTooLong(Stream stream) {
        if (stream.getDescription() != null
          && alphanumericLength(stream.getDescription()) > DESCRIPTION_MAXIMUM_LENGTH) {
            fieldValidationErrors.add(new FieldValidationError(ShootrError.ERROR_SUBCODE_DESCRIPTION_TOO_LONG,
              FIELD_DESCRIPTION));
        }
    }
}
