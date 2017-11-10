package com.shootr.mobile.data.repository.datasource.user;

import com.shootr.mobile.data.api.entity.FollowsEntity;
import com.shootr.mobile.data.entity.BlockEntity;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.data.repository.datasource.SyncableDataSource;
import com.shootr.mobile.domain.exception.FollowingBlockedUserException;
import java.util.List;

public interface FollowDataSource extends SyncableDataSource<FollowEntity> {

    void putFollow(String idUser) throws FollowingBlockedUserException;

    void removeFollow(String idUser);

    void block(BlockEntity block);

    void removeBlock(String idUser);

    void removeAllBlocks();

    List<BlockEntity> getBlockeds();

    void putBlockeds(List<BlockEntity> blockeds);

    List<FollowEntity> getFollows(String idUser, Integer page, Long timestamp);

    FollowsEntity getFollowings(String idUser, String[] type, Long maxTimestamp);

    FollowsEntity getFollowers(String idUser, String[] type, Long maxTimestamp);

    FollowsEntity getStreamFollowers(String idStream, String[] type, Long maxTimestamp);

    void putFailedFollow(FollowEntity followEntity);

    void deleteFailedFollows();
}
