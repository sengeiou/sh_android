package gm.mobi.android.service.dataservice.dto;


import android.support.v4.util.ArrayMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import gm.mobi.android.constant.Constants;
import gm.mobi.android.constant.ServiceConstants;
import gm.mobi.android.db.GMContract.UserTable;
import gm.mobi.android.db.mappers.UserMapper;
import gm.mobi.android.service.dataservice.generic.GenericDto;
import gm.mobi.android.service.dataservice.generic.MetadataDto;
import gm.mobi.android.service.dataservice.generic.OperationDto;
import gm.mobi.android.service.dataservice.generic.RequestorDto;

public class UserDtoFactory {

    private static final String ENTITY_LOGIN = "Login";

    private static final String ALIAS_LOGIN = "Login";


    public static GenericDto getLoginOperationDto(String id, String password) {
        Map<String, Object> keys = new ArrayMap<>(2);
        keys.put(id.contains("@") ? UserTable.EMAIL : UserTable.USER_NAME, id);
        keys.put(UserTable.PASSWORD, password);

        //TODO: Metadata Builder
        MetadataDto md = new MetadataDto(
                ServiceConstants.OPERATION_RETRIEVE,
                ENTITY_LOGIN,
                false, 1L, 0L, 1L, keys
        );

        OperationDto op = new OperationDto();
        op.setMetadata(md);

        Map<String, Object>[] data = new HashMap[1];
        data[0] = UserMapper.toDto(null);
        op.setData(data);

        return getGenericDtoFromOperation(ALIAS_LOGIN, op);
    }


    private static GenericDto getGenericDtoFromOperation(String alias, OperationDto[] ops) {
        GenericDto generic = new GenericDto();
        generic.setStatusCode(null);
        generic.setStatusMessage(null);

        //TODO Builder, injected or something else
        generic.setRequestor(new RequestorDto(null, null, Constants.ANDROID_PLATFORM, 0L, System.currentTimeMillis()));
        generic.setAlias(alias);
        generic.setOps(ops);
        return generic;
    }

    private static GenericDto getGenericDtoFromOperation(String alias, OperationDto op) {
        return getGenericDtoFromOperation(alias, new OperationDto[]{op});
    }

}
