package com.shootr.android.service.dataservice.dto;

import android.app.Application;
import com.shootr.android.constant.Constants;
import com.shootr.android.service.dataservice.generic.GenericDto;
import com.shootr.android.service.dataservice.generic.OperationDto;
import com.shootr.android.util.VersionUtils;

public class UtilityDtoFactory {

    private final long currentVersion;

    public UtilityDtoFactory(Application application) {
        currentVersion = VersionUtils.getVersionCode(application);
    }

    public GenericDto getGenericDtoFromOperation(String alias, OperationDto op) {
        return getGenericDtoFromOperations(alias, new OperationDto[]{op});
    }

    public GenericDto getGenericDtoFromOperations(String alias, OperationDto[] ops) {
        GenericDto generic = new GenericDto();
        generic.setOps(ops);
        generic.setStatusCode(null);
        generic.setStatusMessage(null);

        //TODO Builder, injected or something else
        generic.setReq(null,
          null,
          Constants.ANDROID_PLATFORM,
          currentVersion,
          System.currentTimeMillis());
        generic.setAlias(alias);

        return generic;
    }
}
