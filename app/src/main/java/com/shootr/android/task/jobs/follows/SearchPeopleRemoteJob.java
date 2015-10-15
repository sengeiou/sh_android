package com.shootr.android.task.jobs.follows;

import android.app.Application;
import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.service.UserApiService;
import com.shootr.android.data.bus.Main;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.task.events.follows.SearchPeopleRemoteResultEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserEntityModelMapper;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SearchPeopleRemoteJob extends ShootrBaseJob<SearchPeopleRemoteResultEvent> {

    UserApiService userApiService;

    private String searchString;
    private int pageOffset;

    private FollowManager followManager;
    private SessionRepository sessionRepository;

    private UserEntityModelMapper userModelMapper;

    @Inject
    public SearchPeopleRemoteJob(Application app,
      @Main Bus bus,
      UserApiService userApiService,
      FollowManager followManager,
      UserEntityModelMapper userModelMapper,
      SessionRepository sessionRepository) {
        super(app, bus);
        this.userApiService = userApiService;
        this.userModelMapper = userModelMapper;
        this.followManager = followManager;
        this.sessionRepository = sessionRepository;
    }

    public void init(String searchString, int pageOffset) {
        this.searchString = searchString;
        this.pageOffset = pageOffset;
    }

    @Override protected void run() throws SQLException, IOException {
        List<UserModel> searchResults = getSearchFromServer();
        if(searchResults!=null){
            postSuccessfulEvent(new SearchPeopleRemoteResultEvent(searchResults));
        }
    }

    public List<UserModel> getUserVOs(List<UserEntity> users){
        List<UserModel> userVOs = new ArrayList<>();
        for(UserEntity u:users){
            String idUser = u.getIdUser();
            String idUserSessionRepository = sessionRepository.getCurrentUser().getIdUser();
            FollowEntity follow = followManager.getFollowByUserIds(idUserSessionRepository, idUser);
            //before doing this UPDATE FOLLOWS
            boolean isMe = idUser.equals(sessionRepository.getCurrentUser().getIdUser());
            userVOs.add(userModelMapper.toUserModel(u,follow,isMe));
        }
        return userVOs;
    }

    private List<UserModel> getSearchFromServer() throws IOException {
        try {
            return getUserVOs(userApiService.search(searchString, pageOffset));
        } catch (ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }
}
