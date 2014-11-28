package com.shootr.android.util;

import android.app.Application;
import android.content.Context;
import android.support.annotation.StringRes;
import com.shootr.android.R;
import com.shootr.android.service.ShootrError;
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
    }

    public String getMessageForError(ShootrError shootrError) {
        return getMessageForCode(shootrError.errorCode);
    }

    public String getMessageForCode(String errorCode) {
        return context.getString(getResourceStringForCode(errorCode));
    }

    public String getCommunicationErrorMessage() {
        return context.getString(R.string.communication_error);
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
