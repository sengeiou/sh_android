package com.shootr.mobile.data.repository.datasource.user;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.UserApiService;
import com.shootr.mobile.data.entity.SuggestedPeopleEntity;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceSuggestedPeopleDataSource implements SuggestedPeopleDataSource {

    private final UserApiService userApiService;

    @Inject public ServiceSuggestedPeopleDataSource(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    @Override public List<SuggestedPeopleEntity> getSuggestedPeople(String locale) {
        try {
            return userApiService.getSuggestedPeople(locale);
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public void putSuggestedPeople(List<SuggestedPeopleEntity> suggestedPeople) {
        throw new IllegalStateException("putSuggestedPeople cannot be done in remote");
    }
}
