package com.shootr.android.service.dataservice;

import android.content.Context;
import com.shootr.android.constant.Constants;
import com.shootr.android.service.dataservice.generic.GenericDto;
import com.shootr.android.service.dataservice.generic.OperationDto;
import com.shootr.android.service.dataservice.generic.RequestorDto;
import com.shootr.android.util.VersionUtils;

public class GenericDtoBuilder {
    public static GenericDto getGenericDtoFromRetrieveDatas(int mEntity, Context mContext, Long offset) {
        GenericDto genericDto = new GenericDto();
        genericDto.setStatusCode(null);
        genericDto.setStatusMessage(null);
//        genericDto.setAlias(Utils.getRetrieveAliasByEntity(mEntity));
        genericDto.setRequestor(new RequestorDto(null, null, Constants.ANDROID_PLATFORM, (long) VersionUtils.getVersionCode(mContext), System.currentTimeMillis()));
        genericDto.setOps(new OperationDto[]{});
        return genericDto;
    }


}
