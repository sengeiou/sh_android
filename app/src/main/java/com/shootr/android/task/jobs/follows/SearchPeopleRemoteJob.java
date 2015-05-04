package com.shootr.android.task.jobs.follows;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.bus.Main;
import com.shootr.android.ui.model.mappers.UserEntityModelMapper;
import com.squareup.otto.Bus;

import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.service.PaginatedResult;
import com.shootr.android.task.events.follows.SearchPeopleRemoteResultEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.UserModel;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SearchPeopleRemoteJob extends ShootrBaseJob<SearchPeopleRemoteResultEvent> {

    private static final int PRIORITY = 4;
    public static final String SEARCH_PEOPLE_GROUP = "searchpeople";

    ShootrService service;

    private String searchString;
    private int pageOffset;

    private FollowManager followManager;
    private SessionRepository sessionRepository;

    private UserEntityModelMapper userModelMapper;

    @Inject
    public SearchPeopleRemoteJob(Application app, @Main Bus bus, ShootrService service, NetworkUtil networkUtil,
      FollowManager followManager, UserEntityModelMapper userModelMapper, SessionRepository sessionRepository) {
        super(new Params(PRIORITY).groupBy(SEARCH_PEOPLE_GROUP), app, bus, networkUtil);
        this.service = service;
        this.userModelMapper = userModelMapper;
        this.followManager = followManager;
        this.sessionRepository = sessionRepository;
    }

    public void init(String searchString, int pageOffset) {
        this.searchString = searchString;
        this.pageOffset = pageOffset;
    }

    @Override protected void run() throws SQLException, IOException {
        PaginatedResult<List<UserEntity>> searchResults = getSearchFromServer();
        if(searchResults!=null){
            postSuccessfulEvent(new SearchPeopleRemoteResultEvent(new PaginatedResult<>(getUserVOs(searchResults.getResult())).setPageOffset(pageOffset).setTotalItems(searchResults.getTotalItems())));
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

    private PaginatedResult<List<UserEntity>> getSearchFromServer() throws IOException {
        return service.searchUsersByNameOrNickNamePaginated(searchString, pageOffset);
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
