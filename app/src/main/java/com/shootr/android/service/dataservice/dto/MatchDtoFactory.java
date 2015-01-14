package com.shootr.android.service.dataservice.dto;

import com.shootr.android.constant.Constants;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.mappers.MatchMapper;
import com.shootr.android.db.mappers.TeamMapper;
import com.shootr.android.db.mappers.WatchMapper;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.db.DatabaseContract.WatchTable;
import com.shootr.android.service.dataservice.generic.FilterDto;
import com.shootr.android.service.dataservice.generic.GenericDto;
import com.shootr.android.service.dataservice.generic.MetadataDto;
import com.shootr.android.service.dataservice.generic.OperationDto;
import com.shootr.android.util.TimeUtils;
import java.util.List;
import javax.inject.Inject;

import static com.shootr.android.service.dataservice.generic.FilterBuilder.and;
import static com.shootr.android.service.dataservice.generic.FilterBuilder.or;
import static com.shootr.android.service.dataservice.generic.FilterBuilder.orIsNotDeleted;

public class MatchDtoFactory {

    private static final String ENTITY_SEARCH_MATCH = "SearchMatch";
    private static final String ALIAS_GET_NEXT_MATCH_WHERE_MY_TEAM_PLAYS = "GET_NEXT_MATCH_WHERE_MY_TEAM_PLAYS";
    private static final String ALIAS_GET_WATCH_OF_MY_FOLLOWING = "GET_MY_FOLLOWING_WATCHES";
    private static final String ALIAS_GET_MATCHES_FROM_WATCH_FOLLOWING = "GET_MATCHES_FROM_WATCH_FOLLOWING";
    private static final String ALIAS_GET_TEAMS_BY_TEAM_IDS = "GET_TEAMS_BY_TEAM_IDS";
    private static final String ALIAS_SET_WATCH_STATUS = "SET_WATCH_STATUS";
    private static final String ALIAS_GET_WATCH_BY_KEYS = "GET_WATCH_BY_KEYS";
    private static final String ALIAS_GET_MATCH_BY_ID_MATCH = "GET_MATCH_BY_ID_MATCH";
    private static final String ALIAS_SEARCH_MATCH = "SEARCH_MATCH";
    private static final int SEARCH_RESULTS_MAX = 150;

    private UtilityDtoFactory utilityDtoFactory;
    private MatchMapper matchMapper;
    private WatchMapper watchMapper;
    private TeamMapper teamMapper;
    private TimeUtils timeUtils;

    @Inject public MatchDtoFactory(UtilityDtoFactory utilityDtoFactory, MatchMapper matchMapper, WatchMapper watchMapper, TeamMapper teamMapper,
      TimeUtils timeUtils){
        this.utilityDtoFactory = utilityDtoFactory;
        this.matchMapper = matchMapper;
        this.teamMapper = teamMapper;
        this.watchMapper = watchMapper;
        this.timeUtils = timeUtils;
    }

    public GenericDto getNextMatchWhereMyFavoriteTeamPlays(Long idFavoriteTeam){
        FilterDto matchFilter = and(orIsNotDeleted(),
          or(DatabaseContract.MatchTable.ID_LOCAL_TEAM).isEqualTo(idFavoriteTeam).
            or(DatabaseContract.MatchTable.ID_VISITOR_TEAM).isEqualTo(idFavoriteTeam),
          or(DatabaseContract.MatchTable.MATCH_DATE).isNotEqualTo(null),
          or(DatabaseContract.MatchTable.MATCH_FINISH_DATE).greaterThan(timeUtils.getCurrentDate()))
           .build();

        MetadataDto md = new MetadataDto.Builder().operation(Constants.OPERATION_RETRIEVE).entity(DatabaseContract.MatchTable.TABLE).includeDeleted(
          true).items(1).filter(matchFilter).build();
        OperationDto op = new OperationDto.Builder()
          .metadata(md)
          .putData(matchMapper.toDto(null))
          .build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_NEXT_MATCH_WHERE_MY_TEAM_PLAYS, op);
    }

    @Deprecated
    public GenericDto getWatchFromUsers(List<Long> userIds, Long idCurrentUser){
        FilterDto watchFollowingFilter =
          or(and(DatabaseContract.WatchTable.STATUS).isEqualTo(1)
          .and(
            or(DatabaseContract.WatchTable.ID_USER).isIn(userIds)
           )).or(WatchTable.ID_USER).isEqualTo(idCurrentUser)
          .build();

        MetadataDto md = new MetadataDto.Builder().operation(Constants.OPERATION_RETRIEVE).entity(
        DatabaseContract.WatchTable.TABLE)
          .includeDeleted(false).filter(watchFollowingFilter).items(1000).build();
        OperationDto op = new OperationDto.Builder().metadata(md).putData(watchMapper.toDto(null)).build();
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_WATCH_OF_MY_FOLLOWING, op);
    }

    public GenericDto getWatchFromUsersAndMatch(List<Long> userIds, Long idMatch) {
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("userIds cannot be null nor empty");
        }
        FilterDto watchFollowingFilter = and(WatchTable.ID_MATCH).isEqualTo(idMatch)
          .and(WatchTable.STATUS)
          .isEqualTo(WatchEntity.STATUS_WATCHING)
          .and(or(WatchTable.ID_USER).isIn(userIds))
          .build();

        MetadataDto md = new MetadataDto.Builder().operation(Constants.OPERATION_RETRIEVE)
          .entity(DatabaseContract.WatchTable.TABLE)
          .includeDeleted(false)
          .filter(watchFollowingFilter)
          .items(1000)
          .build();
        OperationDto op = new OperationDto.Builder().metadata(md).putData(watchMapper.toDto(null)).build();
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_WATCH_OF_MY_FOLLOWING, op);
    }

    public GenericDto setWatchStatusGenericDto(WatchEntity watchEntity){
        MetadataDto md = new MetadataDto.Builder()
          .operation(Constants.OPERATION_CREATEUPDATE)
          .entity(WatchTable.TABLE)
          .includeDeleted(true)
          .putKey(WatchTable.ID_USER, watchEntity.getIdUser())
          .putKey(WatchTable.ID_MATCH, watchEntity.getIdMatch())
          .build();

        OperationDto op = new OperationDto.Builder()
          .metadata(md)
          .putData(watchMapper.toDto(watchEntity))
          .build();
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_SET_WATCH_STATUS,op);
    }

    public GenericDto getMatchesNotEndedByIds(List<Long> matchIds){
        FilterDto matchesWatchFollowingFilter = and(
          orIsNotDeleted(),
          or(DatabaseContract.MatchTable.ID_MATCH).isIn(matchIds),
          or(DatabaseContract.MatchTable.MATCH_FINISH_DATE).greaterThan(timeUtils.getCurrentDate()),
          or(DatabaseContract.MatchTable.MATCH_DATE).isNotEqualTo(null))
          .build();

        MetadataDto md = new MetadataDto.Builder().operation(Constants.OPERATION_RETRIEVE)
          .entity(DatabaseContract.MatchTable.TABLE)
          .includeDeleted(false)
          .filter(matchesWatchFollowingFilter)
          .items(1000)
          .build();

        OperationDto op = new OperationDto.Builder().metadata(md).putData(matchMapper.toDto(null)).build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_MATCHES_FROM_WATCH_FOLLOWING, op);
    }

    public GenericDto getTeamsFromTeamIds(List<Long> teamIds){
        FilterDto teamsFilter = and(orIsNotDeleted(),or(DatabaseContract.TeamTable.ID_TEAM).isIn(teamIds)).build();
        MetadataDto md = new MetadataDto.Builder().operation(Constants.OPERATION_RETRIEVE)
          .entity(DatabaseContract.TeamTable.TABLE)
          .includeDeleted(false)
          .filter(teamsFilter)
          .items(1000)
          .build();
        OperationDto op = new OperationDto.Builder().metadata(md).putData(teamMapper.toDto(null)).build();
       return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_TEAMS_BY_TEAM_IDS, op);
    }

    public GenericDto getWatchByKeys(Long idUser, Long idMatch) {
        MetadataDto md = new MetadataDto.Builder()
          .operation(Constants.OPERATION_RETRIEVE)
          .entity(WatchTable.TABLE)
          .putKey(WatchTable.ID_USER, idUser)
          .putKey(WatchTable.ID_MATCH, idMatch)
          .items(1).build();
        OperationDto op = new OperationDto.Builder().metadata(md).putData(watchMapper.toDto(null)).build();
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_WATCH_BY_KEYS,op);
    }

    public GenericDto getMatchByIdMatch(Long idMatch) {
        MetadataDto md = new MetadataDto.Builder()
          .operation(Constants.OPERATION_RETRIEVE)
          .entity(DatabaseContract.MatchTable.TABLE)
          .putKey(WatchTable.ID_MATCH, idMatch)
          .items(1).build();
        OperationDto op = new OperationDto.Builder().metadata(md).putData(matchMapper.toDto(null)).build();
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_MATCH_BY_ID_MATCH,op);

    }

    public GenericDto searchMatches(String queryText) {
        MetadataDto md = new MetadataDto.Builder()
          .operation(Constants.OPERATION_RETRIEVE)
          .entity(ENTITY_SEARCH_MATCH)
          .putKey("pattern", queryText)
          .items(SEARCH_RESULTS_MAX)
          .build();

        OperationDto op = new OperationDto.Builder().metadata(md).build();
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_SEARCH_MATCH, op);
    }

    public GenericDto getWatchVisible(Long currentUserId) {
        FilterDto watchFollowingFilter =
          and(WatchTable.ID_USER).isEqualTo(currentUserId)
          .and(WatchTable.VISIBLE).isEqualTo(WatchEntity.VISIBLE)
          .and(WatchTable.STATUS).isNotEqualTo(null)
          .build();

        MetadataDto md = new MetadataDto.Builder().operation(Constants.OPERATION_RETRIEVE).entity(
          DatabaseContract.WatchTable.TABLE)
          .includeDeleted(false).filter(watchFollowingFilter).items(1000).build();
        OperationDto op = new OperationDto.Builder().metadata(md).putData(watchMapper.toDto(null)).build();
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_WATCH_OF_MY_FOLLOWING, op);
    }
}
