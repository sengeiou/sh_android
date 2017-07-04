package com.shootr.mobile.data.repository.datasource.user;

import com.shootr.mobile.data.api.entity.FollowingsEntity;
import com.shootr.mobile.data.entity.BlockEntity;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.data.repository.datasource.SyncableDataSource;
import com.shootr.mobile.domain.exception.FollowingBlockedUserException;
import java.util.List;

public interface FollowDataSource extends SyncableDataSource<FollowEntity> {

    List<FollowEntity> putFollows(List<FollowEntity> followEntities);

    FollowEntity putFollow(FollowEntity followEntity) throws FollowingBlockedUserException;

    void removeFollow(String idUser);

    void block(BlockEntity block);

    void removeBlock(String idUser);

    void removeAllBlocks();

    List<BlockEntity> getBlockeds();

    void putBlockeds(List<BlockEntity> blockeds);

    List<String> getMutuals();

    List<FollowEntity> getFollows(String idUser, Integer page, Long timestamp);

    FollowingsEntity getFollowings(String idUser, String[] type, Long maxTimestamp);
}
