package com.shootr.android.task.jobs.follows;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;

import com.shootr.android.data.SessionManager;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.objects.FollowEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.service.PaginatedResult;
import com.shootr.android.task.events.follows.SearchPeopleRemoteResultEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
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
    private SessionManager sessionManager;

    private Long currentUserId;

    private UserModelMapper userModelMapper;

    @Inject
    public SearchPeopleRemoteJob(Application app, Bus bus, ShootrService service, NetworkUtil networkUtil,
      FollowManager followManager, UserModelMapper userModelMapper, SQLiteOpenHelper openHelper, SessionManager sessionManager) {
        super(new Params(PRIORITY).groupBy(SEARCH_PEOPLE_GROUP), app, bus, networkUtil);
        this.service = service;
        this.userModelMapper = userModelMapper;
        this.followManager = followManager;
        this.sessionManager = sessionManager;
        setOpenHelper(openHelper);
    }

    public void init(String searchString, int pageOffset) {
        this.searchString = searchString;
        this.pageOffset = pageOffset;
        currentUserId = sessionManager.getCurrentUser().getIdUser();
    }

    @Override protected void run() throws SQLException, IOException {
        PaginatedResult<List<UserEntity>> searchResults = getSearchFromServer();
        postSuccessfulEvent(new SearchPeopleRemoteResultEvent(new PaginatedResult<>(getUserVOs(searchResults.getResult())).setPageOffset(pageOffset).setTotalItems(searchResults.getTotalItems())));
    }

    public List<UserModel> getUserVOs(List<UserEntity> users){
        List<UserModel> userVOs = new ArrayList<>();
        for(UserEntity u:users){
            Long idUser = u.getIdUser();
            FollowEntity follow = followManager.getFollowByUserIds(currentUserId, idUser);
            //before doing this UPDATE FOLLOWS
            boolean isMe = idUser.equals(currentUserId);
            userVOs.add(userModelMapper.toUserModel(u,follow,isMe));
        }
        return userVOs;
    }

    private PaginatedResult<List<UserEntity>> getSearchFromServer() throws IOException {
        return service.searchUsersByNameOrNickNamePaginated(searchString, pageOffset);
    }

    @Override protected void createDatabase() {
        /* no-op */
        createWritableDb();
    }

    @Override protected void setDatabaseToManagers(SQLiteDatabase db) {
        /* no-op */
        followManager.setDataBase(db);
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
