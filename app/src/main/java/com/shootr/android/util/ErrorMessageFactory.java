package com.shootr.android.util;

import android.app.Application;
import android.content.Context;
import android.support.annotation.StringRes;
import com.shootr.android.R;
import com.shootr.android.exception.ShootrError;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

public class ErrorMessageFactory {

    public static final int RESOURCE_ERROR_UNKNOWN = R.string.error_message_unknown;

    private Map<String, Integer> codeResourceMap;
    private Context context;

    @Inject public ErrorMessageFactory(Application context) {
        this.context = context.getApplicationContext();
        generateCodeResourceMap();
    }

    private void generateCodeResourceMap() {
        codeResourceMap = new HashMap<>();
        codeResourceMap.put(ShootrError.ERROR_CODE_UNKNOWN_ERROR, RESOURCE_ERROR_UNKNOWN);
        codeResourceMap.put(ShootrError.ERROR_CODE_INVALID_IMAGE, R.string.error_message_invalid_image);
        codeResourceMap.put(ShootrError.ERROR_CODE_INVALID_SESSION_TOKEN, R.string.error_message_invalid_session_token);
        codeResourceMap.put(ShootrError.ERROR_CODE_USERNAME_DUPLICATE, R.string.error_message_username_duplicate);
        codeResourceMap.put(ShootrError.ERROR_CODE_USERNAME_NULL, R.string.error_message_username_empty);
        codeResourceMap.put(ShootrError.ERROR_CODE_USERNAME_TOO_SHORT, R.string.error_message_username_short);
        codeResourceMap.put(ShootrError.ERROR_CODE_USERNAME_TOO_LONG, R.string.error_message_username_long);
        codeResourceMap.put(ShootrError.ERROR_CODE_USERNAME_INVALID_CHARACTERS, R.string.error_message_username_invalid);
        codeResourceMap.put(ShootrError.ERROR_CODE_NAME_TOO_LONG, R.string.error_message_name_long);
        codeResourceMap.put(ShootrError.ERROR_CODE_NAME_INVALID_CHARACTERS, R.string.error_message_name_invalid);
        codeResourceMap.put(ShootrError.ERROR_CODE_WEBSITE_WRONG_URI, R.string.error_message_website_invalid);
        codeResourceMap.put(ShootrError.ERROR_CODE_WEBSITE_TOO_LONG, R.string.error_message_website_too_long);
        codeResourceMap.put(ShootrError.ERROR_CODE_BIO_TOO_LONG, R.string.error_message_bio_too_long);
        codeResourceMap.put(ShootrError.ERROR_CODE_SHOT_TEXT_DUPLICATED, R.string.new_shot_repeated);
    }

    public String getMessageForError(ShootrError shootrError) {
        return getMessageForCode(shootrError.getErrorCode());
    }

    public String getMessageForCode(String errorCode) {
        return context.getString(getResourceStringForCode(errorCode));
    }

    public String getCommunicationErrorMessage() {
        return context.getString(R.string.communication_error);
    }

    public String getConnectionNotAvailableMessage() {
        return context.getString(R.string.connection_lost);
    }

    @StringRes public Integer getResourceStringForCode(String errorCode) {
        Integer errorStringResource = codeResourceMap.get(errorCode);
        if (errorStringResource == null || errorStringResource <= 0) {
            errorStringResource = RESOURCE_ERROR_UNKNOWN;
            Timber.w("No string resource mapping found for code %s. Using default unknown message", errorCode);
        }
        return errorStringResource;
    }
}
