package com.shootr.android.service.dataservice.dto;

import com.shootr.android.constant.Constants;
import com.shootr.android.db.DatabaseContract.TeamTable;
import com.shootr.android.db.mappers.TeamMapper;
import com.shootr.android.service.dataservice.generic.FilterDto;
import com.shootr.android.service.dataservice.generic.GenericDto;
import com.shootr.android.service.dataservice.generic.MetadataDto;
import com.shootr.android.service.dataservice.generic.OperationDto;
import javax.inject.Inject;

import static com.shootr.android.service.dataservice.generic.FilterBuilder.and;
import static com.shootr.android.service.dataservice.generic.FilterBuilder.or;

public class TeamDtoFactory {

    static final int NUMBER_OF_DAYS_AGO = 7;

    public static final Integer GET_FOLLOWERS = 0;
    public static final Integer GET_FOLLOWING = 1;

    public static final int FOLLOW_TYPE = 0;
    public static final int UNFOLLOW_TYPE = 1;

    private static final String ALIAS_SEARCH_TEAMS = " SEARCH_TEAM";
    private static final int TEAM_SEARCH_LIMIT = 50;

    private final UtilityDtoFactory utilityDtoFactory;
    private final TeamMapper teamMapper;

    @Inject public TeamDtoFactory(UtilityDtoFactory utilityDtoFactory, TeamMapper teamMapper) {
        this.utilityDtoFactory = utilityDtoFactory;
        this.teamMapper = teamMapper;
    }

    public GenericDto searchTeamsOperation(String searchString) {
        FilterDto filter = and(or(TeamTable.CLUB_NAME).contains(searchString)
            .or(TeamTable.OFFICIAL_NAME)
            .contains(searchString)
            .or(TeamTable.SHORT_NAME)
            .contains(searchString)
            .or(TeamTable.TLA_NAME)
            .contains(searchString)).build();

        MetadataDto md = new MetadataDto.Builder().operation(Constants.OPERATION_RETRIEVE)
          .entity(TeamTable.TABLE)
          .items(TEAM_SEARCH_LIMIT)
          .includeDeleted(false)
          .filter(filter)
          .build();

        OperationDto od = new OperationDto.Builder().metadata(md).putData(teamMapper.toDto(null)).build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_SEARCH_TEAMS, od);
    }
}