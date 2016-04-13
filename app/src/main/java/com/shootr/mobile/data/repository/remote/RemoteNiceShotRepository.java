package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.ShotApiService;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.repository.NiceShotRepository;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class RemoteNiceShotRepository implements NiceShotRepository {

    private final ShotApiService shotApiService;

    @Inject public RemoteNiceShotRepository(ShotApiService shotApiService) {
        this.shotApiService = shotApiService;
    }

    @Override public void mark(String idShot) {
        try {
            shotApiService.markNice(idShot);
        } catch (ApiException | IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public boolean isMarked(String idShot) {
        throw new IllegalStateException("Server doesn't allow checking nice status");
    }

    @Override public void unmark(String idShot) {
        try {
            shotApiService.unmarkNice(idShot);
        } catch (ApiException | IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public void markAll(List<String> nicedIdShots) {
        throw new IllegalStateException("Method not allowed");
    }
}
