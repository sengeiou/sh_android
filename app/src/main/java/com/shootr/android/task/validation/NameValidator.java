package com.shootr.android.task.validation;

import com.shootr.android.exception.ShootrError;
import com.shootr.android.ui.model.UserModel;

public class NameValidator extends FieldValidator {

    public static final int MAX_LENGTH = 20;

    private String name;

    public NameValidator(UserModel userModel) {
        name = userModel.getName();
    }

    @Override protected int getField() {
        return FieldValidationError.FIELD_NAME;
    }

    @Override protected void performValidations() {
        validateNameTooLong();
    }

    private void validateNameTooLong() {
        if (name != null && name.length() > MAX_LENGTH) {
            addError(ShootrError.ERROR_CODE_NAME_TOO_LONG);
        }
    }
}
