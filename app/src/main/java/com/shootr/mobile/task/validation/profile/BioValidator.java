package com.shootr.mobile.task.validation.profile;

import com.shootr.mobile.domain.exception.ShootrError;
import com.shootr.mobile.task.validation.FieldValidationError;
import com.shootr.mobile.task.validation.FieldValidator;
import com.shootr.mobile.ui.model.UserModel;

public class BioValidator extends FieldValidator {

    public static final int MAX_LENGTH = 150;

    private String bio;

    public BioValidator(UserModel userModel) {
        bio = userModel.getBio();
    }

    @Override protected int getField() {
        return FieldValidationError.FIELD_BIO;
    }

    @Override protected void performValidations() {
        validateBioTooLong();
    }

    private void validateBioTooLong() {
        if (bio != null && bio.length() > MAX_LENGTH) {
            addError(ShootrError.ERROR_CODE_BIO_TOO_LONG);
        }
    }
}
