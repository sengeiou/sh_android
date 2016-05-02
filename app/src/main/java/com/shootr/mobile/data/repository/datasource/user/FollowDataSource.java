package com.shootr.mobile.data.repository.datasource.user;

import com.shootr.mobile.data.entity.BanEntity;
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

    List<BlockEntity> getBlockeds();

    void putBlockeds(List<BlockEntity> blockeds);

    void ban(BanEntity banEntity);

    List<BanEntity> getBanneds();

    void putBanneds(List<BanEntity> banneds);

    void unban(String idUser);

    List<String> getMutuals();
}
