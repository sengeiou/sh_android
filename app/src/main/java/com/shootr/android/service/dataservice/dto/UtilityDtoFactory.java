package com.shootr.android.service.dataservice.dto;

import com.shootr.android.constant.Constants;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.service.dataservice.generic.FilterDto;
import com.shootr.android.service.dataservice.generic.FilterItemDto;
import com.shootr.android.service.dataservice.generic.GenericDto;
import com.shootr.android.service.dataservice.generic.OperationDto;
import com.shootr.android.service.dataservice.generic.RequestorDto;

public class UtilityDtoFactory {


    public GenericDto getGenericDtoFromOperation(String alias, OperationDto op) {
        return getGenericDtoFromOperations(alias, new OperationDto[]{op});
    }

    public GenericDto getGenericDtoFromOperations(String alias, OperationDto[] ops) {
        GenericDto generic = new GenericDto();
        generic.setOps(ops);
        generic.setStatusCode(null);
        generic.setStatusMessage(null);

        //TODO Builder, injected or something else
        generic.setRequestor(new RequestorDto(null, null, Constants.ANDROID_PLATFORM, 0L, System.currentTimeMillis()));
        generic.setAlias(alias);

        return generic;
    }

    public FilterDto[] getTimeFilterDto(Long lastModifiedDate) {
        return new FilterDto[]{
                new FilterDto(Constants.NEXUS_OR,
                        new FilterItemDto[]{
                                new FilterItemDto(Constants.COMPARATOR_GREAT_EQUAL_THAN, DatabaseContract.SyncColumns.CSYS_DELETED, lastModifiedDate),
                                new FilterItemDto(Constants.COMPARATOR_GREAT_EQUAL_THAN, DatabaseContract.SyncColumns.CSYS_MODIFIED, lastModifiedDate)
                        },
                        null)
        };
    }
}
