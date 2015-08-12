package com.shootr.android.data.repository.datasource.user;

import com.shootr.android.data.entity.SuggestedPeopleEntity;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceSuggestedPeopleDataSource implements SuggestedPeopleDataSource {

    private final ShootrService service;

    @Inject public ServiceSuggestedPeopleDataSource(ShootrService service) {
        this.service = service;
    }

    @Override public List<SuggestedPeopleEntity> getSuggestedPeople(String currentUserId) {
        try {
            return service.getSuggestedPeople(currentUserId);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public void putSuggestedPeople(List<SuggestedPeopleEntity> suggestedPeople) {
        throw new IllegalStateException("putSuggestedPeople cannot be done in remote");
    }
}
