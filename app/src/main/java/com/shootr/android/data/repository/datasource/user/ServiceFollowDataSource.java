package com.shootr.android.data.repository.datasource.user;

import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.service.UserApiService;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.repository.SessionRepository;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

import static com.shootr.android.domain.utils.Preconditions.checkArgument;

public class ServiceFollowDataSource implements FollowDataSource {

    private final SessionRepository sessionRepository;
    private final UserApiService userApiService;

    @Inject
    public ServiceFollowDataSource(SessionRepository sessionRepository, UserApiService userApiService) {
        this.sessionRepository = sessionRepository;
        this.userApiService = userApiService;
    }

    @Override
    public List<FollowEntity> putFollows(List<FollowEntity> followEntities) {
        throw new IllegalStateException("Method not allowed in service");
    }

    @Override
    public FollowEntity putFollow(FollowEntity followEntity) {
        checkArgument(followEntity.getIdUser().equals(sessionRepository.getCurrentUserId()),
          "Only follows from the current user are allowed in service");

        try {
            userApiService.follow(followEntity.getFollowedUser());
            return followEntity;
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override
    public void removeFollow(String idUser) {
        try {
            userApiService.unfollow(idUser);
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override
    public List<FollowEntity> getEntitiesNotSynchronized() {
        throw new IllegalStateException("Method not valid for service");
    }
}
