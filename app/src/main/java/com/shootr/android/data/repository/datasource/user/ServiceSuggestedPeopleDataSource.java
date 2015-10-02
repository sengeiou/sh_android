package com.shootr.android.data.repository.datasource.user;

import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.service.UserApiService;
import com.shootr.android.data.entity.SuggestedPeopleEntity;
import com.shootr.android.domain.exception.ServerCommunicationException;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceSuggestedPeopleDataSource implements SuggestedPeopleDataSource {

    private final UserApiService userApiService;

    @Inject public ServiceSuggestedPeopleDataSource(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    @Override public List<SuggestedPeopleEntity> getSuggestedPeople() {
        try {
            return userApiService.getSuggestedPeople();
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public void putSuggestedPeople(List<SuggestedPeopleEntity> suggestedPeople) {
        throw new IllegalStateException("putSuggestedPeople cannot be done in remote");
    }
}
