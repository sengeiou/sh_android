package gm.mobi.android.service.dataservice.dto;

import gm.mobi.android.constant.Constants;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.mappers.MatchMapper;
import gm.mobi.android.db.mappers.TeamMapper;
import gm.mobi.android.db.mappers.WatchMapper;
import gm.mobi.android.service.dataservice.generic.FilterDto;
import gm.mobi.android.service.dataservice.generic.GenericDto;
import gm.mobi.android.service.dataservice.generic.MetadataDto;
import gm.mobi.android.service.dataservice.generic.OperationDto;
import java.util.List;
import javax.inject.Inject;

import static gm.mobi.android.service.dataservice.generic.FilterBuilder.and;
import static gm.mobi.android.service.dataservice.generic.FilterBuilder.or;
import static gm.mobi.android.service.dataservice.generic.FilterBuilder.orIsNotDeleted;

public class MatchDtoFactory {

    private static final String ALIAS_GET_NEXT_MATCH_WHERE_MY_TEAM_PLAYS = "GET_NEXT_MATCH_WHERE_MY_TEAM_PLAYS";
    private static final String ALIAS_GET_WATCH_OF_MY_FOLLOWING = "GET_MY_FOLLOWING_WATCHES";
    private static final String ALIAS_GET_MATCHES_FROM_WATCH_FOLLOWING = "GET_MATCHES_FROM_WATCH_FOLLOWING";
    private static final String ALIAS_GET_TEAMS_BY_TEAM_IDS = "GET_TEAMS_BY_TEAM_IDS";
    public static final int STATUS_NOT_STARTED = 0;
    public static final int STATUS_STARTED = 1;

    private UtilityDtoFactory utilityDtoFactory;
    private MatchMapper matchMapper;
    private WatchMapper watchMapper;
    private TeamMapper teamMapper;

    @Inject public MatchDtoFactory(UtilityDtoFactory utilityDtoFactory, MatchMapper matchMapper, WatchMapper watchMapper, TeamMapper teamMapper){
        this.utilityDtoFactory = utilityDtoFactory;
        this.matchMapper = matchMapper;
        this.teamMapper = teamMapper;
        this.watchMapper = watchMapper;
    }

    public GenericDto getLastMatchWhereMyFavoriteTeamPlays(Long idFavoriteTeam){
        FilterDto matchFilter = and(
          orIsNotDeleted(),
          or(GMContract.MatchTable.ID_LOCAL_TEAM).isEqualTo(idFavoriteTeam).
            or(GMContract.MatchTable.ID_VISITOR_TEAM).isEqualTo(idFavoriteTeam),
          or(GMContract.MatchTable.MATCH_DATE).isNotEqualTo(null),
          or(GMContract.MatchTable.STATUS).isEqualTo(0).or(GMContract.MatchTable.STATUS).isEqualTo(1)).build();

        MetadataDto md = new MetadataDto.Builder().operation(Constants.OPERATION_RETRIEVE).entity(GMContract.MatchTable.TABLE).includeDeleted(true).items(1).filter(matchFilter).build();

        OperationDto op = new OperationDto.Builder()
          .metadata(md)
          .putData(matchMapper.toDto(null))
          .build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_NEXT_MATCH_WHERE_MY_TEAM_PLAYS, op);
    }

    public GenericDto getWatchFromUsers(List<Long> userIds, Long date){
        FilterDto watchFollowingFilter = and(
          orIsNotDeleted(),
          or(GMContract.WatchTable.CSYS_MODIFIED).greaterThan(date),
          or(GMContract.WatchTable.ID_USER).isIn(userIds),
          or( GMContract.WatchTable.STATUS).isEqualTo(1))
          .build();

        MetadataDto md = new MetadataDto.Builder().operation(Constants.OPERATION_RETRIEVE).entity(
          GMContract.WatchTable.TABLE)
          .includeDeleted(true).filter(watchFollowingFilter).items(1000).build();

        OperationDto op = new OperationDto.Builder().metadata(md).putData(watchMapper.toDto(null)).build();
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_WATCH_OF_MY_FOLLOWING, op);
    }


    public GenericDto getMatchesNotEndedByIds(List<Long> matchIds){
        FilterDto matchesWatchFollowingFilter = and(
          orIsNotDeleted(),
          or(GMContract.MatchTable.ID_MATCH).isIn(matchIds),
          or(GMContract.MatchTable.STATUS).isEqualTo(STATUS_NOT_STARTED).or(GMContract.MatchTable.STATUS).isEqualTo(STATUS_STARTED),
          or(GMContract.MatchTable.MATCH_DATE).isNotEqualTo(null))
          .build();

        MetadataDto md = new MetadataDto.Builder().operation(Constants.OPERATION_RETRIEVE)
          .entity(GMContract.MatchTable.TABLE)
          .includeDeleted(false)
          .filter(matchesWatchFollowingFilter)
          .items(1000)
          .build();

        OperationDto op = new OperationDto.Builder().metadata(md).putData(matchMapper.toDto(null)).build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_MATCHES_FROM_WATCH_FOLLOWING, op);
    }


    public GenericDto getTeamsFromTeamIds(List<Long> teamIds){
        FilterDto teamsFilter = and(orIsNotDeleted(),or(GMContract.TeamTable.ID_TEAM).isIn(teamIds)).build();
        MetadataDto md = new MetadataDto.Builder().operation(Constants.OPERATION_RETRIEVE)
          .entity(GMContract.TeamTable.TABLE)
          .includeDeleted(false)
          .filter(teamsFilter)
          .items(1000)
          .build();
        OperationDto op = new OperationDto.Builder().metadata(md).putData(teamMapper.toDto(null)).build();
       return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_TEAMS_BY_TEAM_IDS, op);
    }

}
