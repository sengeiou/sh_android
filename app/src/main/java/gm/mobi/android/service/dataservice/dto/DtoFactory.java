package gm.mobi.android.service.dataservice.dto;

import android.content.Context;

import java.util.Date;

import gm.mobi.android.constant.Constants;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.service.dataservice.generic.FilterDto;
import gm.mobi.android.service.dataservice.generic.FilterItemDto;
import gm.mobi.android.service.dataservice.generic.GenericDto;
import gm.mobi.android.service.dataservice.generic.OperationDto;
import gm.mobi.android.service.dataservice.generic.RequestorDto;

public class DtoFactory {

    public static GenericDto getGenericDtoFromOperation(String alias, OperationDto[] ops) {
        GenericDto generic = new GenericDto();
        generic.setOps(ops);
        generic.setStatusCode(null);
        generic.setStatusMessage(null);

        //TODO Builder, injected or something else
        generic.setRequestor(new RequestorDto(null, null, Constants.ANDROID_PLATFORM, 0L, System.currentTimeMillis()));
        generic.setAlias(alias);

        return generic;
    }

    public static GenericDto getGenericDtoFromOperation(String alias, OperationDto op) {
        return getGenericDtoFromOperation(alias, new OperationDto[]{op});
    }

    public static FilterDto[] getTimeFilterDto(Date lastModifiedDate) {
        return new FilterDto[]{
                new FilterDto(Constants.NEXUS_OR,
                        new FilterItemDto[]{
                                new FilterItemDto(Constants.COMPARATOR_GREAT_EQUAL_THAN, GMContract.SyncColumns.CSYS_DELETED, lastModifiedDate),
                                new FilterItemDto(Constants.COMPARATOR_GREAT_EQUAL_THAN, GMContract.SyncColumns.CSYS_MODIFIED, lastModifiedDate)
                        },
                        null)
        };
    }
}
