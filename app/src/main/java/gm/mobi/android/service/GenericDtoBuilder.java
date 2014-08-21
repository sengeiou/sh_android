package gm.mobi.android.service;

import android.content.Context;

import gm.mobi.android.service.generic.GenericDto;
import gm.mobi.android.service.generic.OperationDto;
import gm.mobi.android.service.generic.RequestorDto;
import gm.mobi.android.constant.Constants;
import gm.mobi.android.util.Utils;


/**
 * Created by InmaculadaAlcon on 14/08/2014.
 */
public class GenericDtoBuilder {
    public static GenericDto getGenericDtoFromRetrieveDatas(int mEntity, Context mContext,Long offset){
        GenericDto genericDto = new GenericDto();
        genericDto.setStatusCode(null);
        genericDto.setStatusMessage(null);
        genericDto.setAlias(Utils.getRetrieveAliasByEntity(mEntity));
        genericDto.setRequestor(new RequestorDto(null,null, Constants.ANDROID_PLATFORM,Utils.getAppVersionForService(mContext),System.currentTimeMillis()));
        genericDto.setOps(new OperationDto[]{});
        return genericDto;
    }


}
