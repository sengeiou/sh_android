package com.shootr.android.ui.debug.debugactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shootr.android.data.api.exception.ErrorInfo;
import com.shootr.android.data.api.exception.ErrorResource;
import com.shootr.android.ui.activities.registro.EmailRegistrationActivity;
import com.shootr.android.ui.debug.ContextualDebugActions;
import com.shootr.okresponsefaker.JacksonFakeResponse;
import com.shootr.okresponsefaker.ResponseFaker;

public class FakeUsernameInUseDebugAction extends ContextualDebugActions.DebugAction<EmailRegistrationActivity> {

    private ObjectMapper objectMapper;

    public FakeUsernameInUseDebugAction(ObjectMapper objectMapper) {
        super(EmailRegistrationActivity.class);
        this.objectMapper = objectMapper;
    }

    @Override
    public String name() {
        return "Fake Username in use";
    }

    @Override
    public void run(EmailRegistrationActivity activity) {
        ErrorResource errorResource = new ErrorResource();
        errorResource.setCode(ErrorInfo.UserNameAlreadyExistsException.code());
        errorResource.setStatus(ErrorInfo.UserNameAlreadyExistsException.httpCode());

        ResponseFaker.setNextFakeResponse(new JacksonFakeResponse(objectMapper,
          errorResource,
          ErrorInfo.UserNameAlreadyExistsException.httpCode()));
    }
}
