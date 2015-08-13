package com.shootr.android.data.repository.remote;

import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.service.ShotApiService;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.repository.NiceShotRepository;
import java.io.IOException;
import javax.inject.Inject;

public class RemoteNiceShotRepository implements NiceShotRepository {

    private final ShotApiService shotApiService;

    @Inject public RemoteNiceShotRepository(ShotApiService shotApiService) {
        this.shotApiService = shotApiService;
    }

    @Override
    public void mark(String idShot) {
        try {
            shotApiService.markNice(idShot);
        } catch (ApiException | IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override
    public boolean isMarked(String idShot) {
        throw new IllegalStateException("Server doesn't allow checking nice status");
    }
    @Override
    public void unmark(String idShot) {
        try {
            shotApiService.unmarkNice(idShot);
        } catch (ApiException | IOException e) {
            throw new ServerCommunicationException(e);
        }
    }
}
