package com.shootr.mobile.data.repository.datasource.nicer;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.NicerApiService;
import com.shootr.mobile.data.entity.NicerEntity;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceNicerDatasource implements NicerDataSource {

    private final NicerApiService nicerApiService;

    @Inject public ServiceNicerDatasource(NicerApiService nicerApiService) {
        this.nicerApiService = nicerApiService;
    }

    @Override public List<NicerEntity> getNicers(String idShot) {
        try {
            return nicerApiService.getNicers(idShot);
        } catch (ApiException | IOException cause) {
            throw new ServerCommunicationException(cause);
        }
    }

    @Override public List<NicerEntity> getNicersWithUser(String idShot) {
        try {
            return nicerApiService.getNicersWithUser(idShot);
        } catch (ApiException | IOException cause) {
            throw new ServerCommunicationException(cause);
        }
    }

    @Override public List<NicerEntity> getNices(String idUser) {
        try {
            return nicerApiService.getNices(idUser);
        } catch (ApiException | IOException cause) {
            throw new ServerCommunicationException(cause);
        }
    }
}
