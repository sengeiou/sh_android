package com.shootr.mobile.ui.debug.debugactions;

import com.shootr.mobile.data.api.exception.ErrorInfo;
import com.shootr.mobile.data.api.exception.ErrorResource;
import com.shootr.mobile.ui.activities.registro.EmailRegistrationActivity;
import com.shootr.mobile.ui.debug.ContextualDebugActions;
import com.shootr.mobile.util.JsonAdapter;
import com.shootr.mobile.util.JsonFakeResponse;
import com.shootr.mobile.util.ResponseFaker;

public class FakeEmailInUseDebugAction extends ContextualDebugActions.DebugAction<EmailRegistrationActivity> {

    private JsonAdapter jsonAdapter;

    public FakeEmailInUseDebugAction(JsonAdapter jsonAdapter) {
        super(EmailRegistrationActivity.class);
        this.jsonAdapter = jsonAdapter;
    }

    @Override public String name() {
        return "Fake Email in use";
    }

    @Override public void run(EmailRegistrationActivity activity) {
        ErrorResource errorResource = new ErrorResource();
        errorResource.setCode(ErrorInfo.EmailAlreadyExistsException.code());
        errorResource.setStatus(ErrorInfo.EmailAlreadyExistsException.httpCode());

        ResponseFaker.setNextFakeResponse(new JsonFakeResponse(jsonAdapter,
          errorResource,
          ErrorInfo.EmailAlreadyExistsException.httpCode()));
    }
}
