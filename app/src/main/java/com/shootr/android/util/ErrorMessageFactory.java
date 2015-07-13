package com.shootr.android.util;

import android.app.Application;
import android.content.Context;
import android.support.annotation.StringRes;
import com.shootr.android.R;
import com.shootr.android.domain.exception.DeleteEventNotAllowedException;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrError;
import com.shootr.android.domain.exception.ShootrException;
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
        codeResourceMap.put(ShootrError.ERROR_CODE_SEARCH_TOO_SHORT, R.string.error_message_search_too_short);
        codeResourceMap.put(ShootrError.ERROR_CODE_EVENT_TITLE_TOO_SHORT, R.string.error_message_event_title_too_short);
        codeResourceMap.put(ShootrError.ERROR_CODE_EVENT_TITLE_TOO_LONG, R.string.error_message_event_title_too_long);
        codeResourceMap.put(ShootrError.ERROR_CODE_EVENT_START_DATE_TOO_LATE, R.string.error_message_event_start_date_too_late);
        codeResourceMap.put(ShootrError.ERROR_CODE_UPDATE_REQUIRED, R.string.error_message_update_required);
        codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_USERNAME_DUPLICATE, R.string.error_message_registration_username_duplicated);
        codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_USERNAME_NULL, R.string.error_message_registration_username_empty);
        codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_USERNAME_TOO_SHORT, R.string.error_message_registration_username_too_short);
        codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_USERNAME_TOO_LONG, R.string.error_message_registration_username_too_long);
        codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_USERNAME_INVALID_CHARACTERS, R.string.error_message_registration_username_invalid);
        codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_EMAIL_IN_USE, R.string.error_message_registration_email_existing);
        codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_EMAIL_INVALID_FORMAT, R.string.error_message_registration_email_invalid);
        codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_EMAIL_NULL, R.string.error_message_registration_email_empty);
        codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_NULL, R.string.error_message_registration_password_null);
        codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_TOO_SHORT, R.string.error_message_registration_password_too_short);
        codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_TOO_LONG, R.string.error_message_registration_password_too_long);
        codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_EQUALS_USERNAME, R.string.error_message_registration_password_equals_username);
        codeResourceMap.put(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_INVALID_CHARACTERS, R.string.error_message_registration_password_invalid);
    }

    public String getMessageForError(ShootrError shootrError) {
        return getMessageForCode(shootrError.getErrorCode());
    }

    public String getMessageForCode(String errorCode) {
        return context.getString(getResourceStringForCode(errorCode));
    }

    public String getUnknownErrorMessage() {
        return context.getString(R.string.error_message_unknown);
    }

    public String getCommunicationErrorMessage() {
        return context.getString(R.string.communication_error);
    }

    public String getConnectionNotAvailableMessage() {
        return context.getString(R.string.connection_lost);
    }

    public String getImageUploadErrorMessage() {
        return context.getString(R.string.error_image_upload);
    }

    @StringRes public Integer getResourceStringForCode(String errorCode) {
        Integer errorStringResource = codeResourceMap.get(errorCode);
        if (errorStringResource == null || errorStringResource <= 0) {
            errorStringResource = RESOURCE_ERROR_UNKNOWN;
            Timber.w("No string resource mapping found for code %s. Using default unknown message", errorCode);
        }
        return errorStringResource;
    }

    public String getMessageForError(ShootrException error) {
        if (error instanceof ServerCommunicationException || error.getCause() instanceof  ServerCommunicationException) {
            return getCommunicationErrorMessage();
        }else if(error instanceof DeleteEventNotAllowedException) {
            return context.getString(R.string.error_message_event_has_watchers);
        } else {
            return getUnknownErrorMessage();
        }
    }

    public String getLoginCredentialsError(){
        return context.getString(R.string.error_login_credentials_invalid);
    }
}
