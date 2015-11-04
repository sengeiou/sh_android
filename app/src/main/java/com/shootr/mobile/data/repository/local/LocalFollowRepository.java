package com.shootr.mobile.data.repository.local;

import android.support.annotation.NonNull;
import com.shootr.mobile.data.entity.BlockEntity;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.data.repository.datasource.user.FollowDataSource;
import com.shootr.mobile.domain.exception.FollowingBlockedUserException;
import com.shootr.mobile.domain.repository.FollowRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class LocalFollowRepository implements FollowRepository {

    private final SessionRepository sessionRepository;
    private final FollowDataSource followDataSource;

    @Inject public LocalFollowRepository(SessionRepository sessionRepository, @Local FollowDataSource followDataSource) {
        this.sessionRepository = sessionRepository;
        this.followDataSource = followDataSource;
    }

    @Override
    public void follow(String idUser) {
        FollowEntity followEntity = createFollow(idUser);
        try {
            followDataSource.putFollow(followEntity);
        } catch (FollowingBlockedUserException e) {
            throw new IllegalArgumentException("This exception should not happen");
        }
    }

    @Override
    public void unfollow(String idUser) {
        followDataSource.removeFollow(idUser);
    }

    @Override public void block(String idUser) {
        BlockEntity blockEntity = createBlock(idUser);
        followDataSource.block(blockEntity);
    }

    @Override public void unblock(String idUser) {
        followDataSource.removeBlock(idUser);
    }

    @Override public List<String> getBlockedIdUsers() {
        List<BlockEntity> blockeds = followDataSource.getBlockeds();
        List<String> blockedIds = new ArrayList<>();
        for (BlockEntity blocked : blockeds) {
            blockedIds.add(blocked.getIdBlockedUser());
        }
        return blockedIds;
    }

    @NonNull
    protected FollowEntity createFollow(String idUser) {
        FollowEntity followEntity = new FollowEntity();
        followEntity.setIdUser(sessionRepository.getCurrentUserId());
        followEntity.setFollowedUser(idUser);
        Date now = new Date();
        followEntity.setBirth(now);
        followEntity.setModified(now);
        return followEntity;
    }

    @NonNull
    protected BlockEntity createBlock(String idUser) {
        BlockEntity blockEntity = new BlockEntity();
        blockEntity.setIdUser(sessionRepository.getCurrentUserId());
        blockEntity.setIdBlockedUser(idUser);
        return blockEntity;
    }
}