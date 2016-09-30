package com.shootr.mobile.task.validation.profile;

import com.shootr.mobile.domain.exception.ShootrError;
import com.shootr.mobile.task.validation.FieldValidationError;
import com.shootr.mobile.task.validation.FieldValidator;
import com.shootr.mobile.ui.model.UserModel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebsiteValidator extends FieldValidator {

    public static final int MAX_LENGTH = 100;
    private static final String WEBSITE_FORMAT_REGEX =
      "^(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.\\-\\?\\=_]*)*";

    private String website;

    public WebsiteValidator(UserModel userModel) {
        website = userModel.getWebsite();
    }

    @Override protected int getField() {
        return FieldValidationError.FIELD_WEBSITE;
    }

    @Override protected void performValidations() {
        validateWebsiteTooLong();
        validateWebsiteInvalidCharacters();
    }

    private void validateWebsiteTooLong() {
        if (website != null && website.length() > MAX_LENGTH) {
            addError(ShootrError.ERROR_CODE_WEBSITE_TOO_LONG);
        }
    }

    private void validateWebsiteInvalidCharacters() {
        if (website != null) {
            Pattern pattern = Pattern.compile(WEBSITE_FORMAT_REGEX);
            Matcher matcher = pattern.matcher(website);
            if (!matcher.matches()) {
                addError(ShootrError.ERROR_CODE_WEBSITE_WRONG_URI);
            }
        }
    }
}
