package com.shootr.android.data.repository.datasource.shot;

import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.domain.TimelineParameters;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceShotDatasource implements ShotDataSource {

    private final ShootrService shootrService;

    @Inject public ServiceShotDatasource(ShootrService shootrService) {
        this.shootrService = shootrService;
    }

    @Override public ShotEntity putShot(ShotEntity shotEntity) {
        try {
            return shootrService.postNewShotWithImage(shotEntity);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<ShotEntity> getShotsForTimeline(TimelineParameters parameters) {
        try {
            return shootrService.getShotsByUserIdList(parameters.getAllUserIds(), parameters.getSinceDate()); //TODO filter by event
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }
}