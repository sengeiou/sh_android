package gm.mobi.android.service.dataservice.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import gm.mobi.android.constant.Constants;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.mappers.ShotMapper;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.service.dataservice.generic.FilterDto;
import gm.mobi.android.service.dataservice.generic.FilterItemDto;
import gm.mobi.android.service.dataservice.generic.GenericDto;
import gm.mobi.android.service.dataservice.generic.MetadataDto;
import gm.mobi.android.service.dataservice.generic.OperationDto;

public class TimelineDtoFactory {

    public static final String ALIAS_GET_SHOTS =  "GET_SHOTS";
    public static final String ALIAS_GET_NEWER_SHOTS = "GET_NEWER_SHOTS";
    public static final String ALIAS_GET_OLDER_SHOTS = "GET_OLDER_SHOTS";

    private UtilityDtoFactory utilityDtoFactory;

    @Inject public TimelineDtoFactory(UtilityDtoFactory utilityDtoFactory) {
        this.utilityDtoFactory = utilityDtoFactory;
    }

    public GenericDto getShotsOperationDto(List<Integer> followingUserIds, Long date){
        OperationDto od = new OperationDto();
        FilterDto filter = new FilterDto(Constants.NEXUS_AND,new FilterItemDto[]{},getShotsFilter(followingUserIds,new Date(date)));
        MetadataDto md = new MetadataDto(Constants.OPERATION_RETRIEVE, GMContract.ShotTable.TABLE, true, null, 0L, 1000L, filter);
        od.setMetadata(md);
        Map<String,Object>[] map = new HashMap[1];
        map[0] = ShotMapper.toDto(null);
        od.setData(map);
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_SHOTS, od);
    }

    public FilterDto[] getShotsFilter(List<Integer> followingUserIds, Date date){
        FilterDto[] mFilterDto = new FilterDto[2];
        FilterItemDto[] mFilterItemDtos = new FilterItemDto[followingUserIds.size()];
        int i = 0;
        for(Integer userId: followingUserIds){
            mFilterItemDtos[i] = new FilterItemDto(Constants.COMPARATOR_EQUAL, GMContract.ShotTable.ID_USER,userId);
            i++;
        }
        mFilterDto[0] =
                new FilterDto(Constants.NEXUS_OR,
                        mFilterItemDtos
                        ,null);
        mFilterDto[1] = new FilterDto(Constants.NEXUS_OR,null,utilityDtoFactory.getTimeFilterDto(date));

        return mFilterDto;
    }


    public GenericDto getNewerShotsOperationDto(List<Integer> followingUserIds, Long date, Shot shot){
        OperationDto od = new OperationDto();
        FilterDto filter = new FilterDto(Constants.NEXUS_AND,
                new FilterItemDto[]{new FilterItemDto(Constants.COMPARATOR_GREAT_EQUAL_THAN,
                        GMContract.ShotTable.CSYS_MODIFIED,shot.getCsys_modified())}, getShotsFilter(followingUserIds,shot.getCsys_modified()));
        MetadataDto md = new MetadataDto(Constants.OPERATION_RETRIEVE, GMContract.ShotTable.TABLE, true,null,0L,1000L,filter);
        od.setMetadata(md);
        Map<String,Object>[] map = new HashMap[1];
        map[0] = ShotMapper.toDto(null);
        od.setData(map);
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_NEWER_SHOTS, od);
    }

    public GenericDto getOlderShotsOperationDto(List<Integer> followingUserIds, Long date, Shot shot){
        OperationDto od = new OperationDto();
        FilterDto filter = new FilterDto(Constants.NEXUS_AND,
                new FilterItemDto[]{new FilterItemDto(Constants.COMPARATOR_LESS_EQUAL_THAN, GMContract.ShotTable.CSYS_MODIFIED,shot.getCsys_modified())}, getShotsFilter(followingUserIds,new Date(date)));
        MetadataDto md = new MetadataDto(Constants.OPERATION_RETRIEVE, GMContract.ShotTable.TABLE, true,null,0L,1000L,filter);
        od.setMetadata(md);
        Map<String,Object>[] map = new HashMap[1];
        map[0] = ShotMapper.toDto(null);
        od.setData(map);
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_OLDER_SHOTS, od);
    }

}
