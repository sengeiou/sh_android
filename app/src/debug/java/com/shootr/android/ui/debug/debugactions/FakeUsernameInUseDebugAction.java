package com.shootr.android.ui.debug.debugactions;

import com.shootr.android.data.api.exception.ErrorInfo;
import com.shootr.android.data.api.exception.ErrorResource;
import com.shootr.android.ui.activities.registro.EmailRegistrationActivity;
import com.shootr.android.ui.debug.ContextualDebugActions;
import com.sloydev.jsonadapters.JsonAdapter;
import com.sloydev.okresponsefaker.JsonFakeResponse;
import com.sloydev.okresponsefaker.ResponseFaker;

public class FakeUsernameInUseDebugAction extends ContextualDebugActions.DebugAction<EmailRegistrationActivity> {

    private JsonAdapter jsonAdapter;

    public FakeUsernameInUseDebugAction(JsonAdapter jsonAdapter) {
        super(EmailRegistrationActivity.class);
        this.jsonAdapter = jsonAdapter;
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

        ResponseFaker.setNextFakeResponse(new JsonFakeResponse(jsonAdapter,
          errorResource,
          ErrorInfo.UserNameAlreadyExistsException.httpCode()));
    }
}
