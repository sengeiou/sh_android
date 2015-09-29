package com.shootr.android.data.repository.local;

import android.support.annotation.NonNull;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.repository.datasource.user.FollowDataSource;
import com.shootr.android.domain.repository.FollowRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.SessionRepository;
import java.util.Date;
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
        followDataSource.putFollow(followEntity);
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
}
