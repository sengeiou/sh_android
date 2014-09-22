package gm.mobi.android.service.dataservice.dto;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.ArrayMap;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import gm.mobi.android.constant.Constants;
import gm.mobi.android.constant.ServiceConstants;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.GMContract.UserTable;
import gm.mobi.android.db.mappers.FollowMapper;
import gm.mobi.android.db.mappers.UserMapper;
import gm.mobi.android.service.dataservice.generic.FilterDto;
import gm.mobi.android.service.dataservice.generic.FilterItemDto;
import gm.mobi.android.service.dataservice.generic.GenericDto;
import gm.mobi.android.service.dataservice.generic.MetadataDto;
import gm.mobi.android.service.dataservice.generic.OperationDto;
import gm.mobi.android.service.dataservice.generic.RequestorDto;
import gm.mobi.android.db.GMContract.FollowTable;
import gm.mobi.android.util.TimeUtils;

public class UserDtoFactory {

    static final Integer NUMBER_OF_DAYS_AGO = 7;
    public static final int GET_FOLLOWERS = 0;
    public static final int GET_FOLLOWING = 1;
    public static final int GET_ALL_FOLLOW = 2;
    public static final int GET_JUST_BOTHFOLLOW = 3;

    private static final String ENTITY_LOGIN = "Login";
    private static final String ALIAS_LOGIN = "Login";
    private static final String ALIAS_GET_FOLLOW =  "ALIAS_GET_FOLLOWING";

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

    public static GenericDto getFollowOperationDto(Integer idUser, Long offset, Context context, int relationship, Long date) {

        OperationDto od = new OperationDto();

        FilterDto filter = getFollowsByIdUserAndRelationship(idUser, relationship, new Date(date), context);
        MetadataDto md = new MetadataDto(Constants.OPERATION_RETRIEVE, FollowTable.TABLE, true, null, 0l, 100L, filter);
        od.setMetadata(md);

        Map<String, Object>[] array = new HashMap[1];
        array[0] = FollowMapper.toDto(null);
        od.setData(array);

        return getGenericDtoFromOperation(ALIAS_GET_FOLLOW,od);
    }

    public static FilterDto[] getTimeFilterDto(Date lastModifiedDate, Context context) {
        return new FilterDto[]{
                new FilterDto(Constants.NEXUS_OR,
                        new FilterItemDto[]{
                                new FilterItemDto(Constants.COMPARATOR_GREAT_EQUAL_THAN, GMContract.SyncColumns.CSYS_DELETED, lastModifiedDate),
                                new FilterItemDto(Constants.COMPARATOR_GREAT_EQUAL_THAN, GMContract.SyncColumns.CSYS_MODIFIED, lastModifiedDate)
                        },
                        null)
        };
    }

    public static FilterDto getFollowsByIdUserAndRelationship(Integer idUser, int relationship, Date lastModifiedDate, Context context) {
        FilterDto filterDto = null;
        switch (relationship) {
            case GET_FOLLOWERS:
                filterDto = new FilterDto(Constants.NEXUS_AND,
                        new FilterItemDto[]{
                                new FilterItemDto(Constants.COMPARATOR_EQUAL, FollowTable.ID_USER, idUser)
                        }, getTimeFilterDto(lastModifiedDate, context)
                );
                break;
            case GET_FOLLOWING:
                filterDto = new FilterDto(Constants.NEXUS_AND,
                        new FilterItemDto[]{
                                new FilterItemDto(Constants.COMPARATOR_NOT_EQUAL, FollowTable.ID_FOLLOWED_USER, null),
                                new FilterItemDto(Constants.COMPARATOR_EQUAL,FollowTable.ID_USER, idUser)
                        }, getTimeFilterDto(lastModifiedDate, context)
                );
                break;
            case GET_ALL_FOLLOW:
                filterDto = new FilterDto(Constants.NEXUS_AND,
                        new FilterItemDto[]{
                                new FilterItemDto(Constants.COMPARATOR_EQUAL, FollowTable.ID_FOLLOWED_USER, idUser),
                                new FilterItemDto(Constants.COMPARATOR_EQUAL, FollowTable.ID_USER, idUser)
                        }, getTimeFilterDto(lastModifiedDate, context)
                );
                break;
            case GET_JUST_BOTHFOLLOW:
                filterDto = new FilterDto(Constants.NEXUS_AND,
                        new FilterItemDto[]{
                                new FilterItemDto(Constants.COMPARATOR_EQUAL, FollowTable.ID_FOLLOWED_USER, idUser),
                                new FilterItemDto(Constants.COMPARATOR_EQUAL, FollowTable.ID_USER, idUser)
                        }, getTimeFilterDto(lastModifiedDate, context)
                );
                break;
        }
        return filterDto;
    }

    private static GenericDto getGenericDtoFromOperation(String alias, OperationDto[] ops) {
        GenericDto generic = new GenericDto();
        generic.setOps(ops);
        generic.setStatusCode(null);
        generic.setStatusMessage(null);

        //TODO Builder, injected or something else
        generic.setRequestor(new RequestorDto(null, null, Constants.ANDROID_PLATFORM, 0L, System.currentTimeMillis()));
        generic.setAlias(alias);

        return generic;
    }

    private static GenericDto getGenericDtoFromOperation(String alias, OperationDto op) {
        return getGenericDtoFromOperation(alias, new OperationDto[]{op});
    }

}
