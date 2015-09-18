package com.shootr.android.data.repository.remote;

import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.service.ShotApiService;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShotRemovedException;
import com.shootr.android.domain.repository.NiceShotRepository;
import java.io.IOException;
import javax.inject.Inject;

public class RemoteNiceShotRepository implements NiceShotRepository {

    private final ShotApiService shotApiService;

    @Inject public RemoteNiceShotRepository(ShotApiService shotApiService) {
        this.shotApiService = shotApiService;
    }

    @Override
    public void mark(String idShot) throws ShotRemovedException {
        try {
            shotApiService.markNice(idShot);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        } catch (ApiException error) {
            throw new ShotRemovedException();
        }
    }

    @Override
    public boolean isMarked(String idShot) {
        throw new IllegalStateException("Server doesn't allow checking nice status");
    }
    @Override
    public void unmark(String idShot) throws ShotRemovedException {
        try {
            shotApiService.unmarkNice(idShot);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        } catch (ApiException error) {
            throw new ShotRemovedException();
        }
    }
}
