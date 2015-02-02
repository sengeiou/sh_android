package com.shootr.android.task.jobs.shots;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.bus.Main;
import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.shots.LatestShotsResultEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class GetLatestShotsJob extends ShootrBaseJob<LatestShotsResultEvent> {

    private static final int PRIORITY = 5;
    public static final Long LATEST_SHOTS_NUMBER = 10L;

    private final ShootrService service;
    private final ShotManager shotManager;
    private final UserManager userManager;
    private final ShotModelMapper shotModelMapper;

    private Long idUser;
    private UserEntity user;

    @Inject public GetLatestShotsJob(Application application, @Main Bus bus, NetworkUtil networkUtil, ShootrService service,
      ShotManager shotManager, UserManager userManager, ShotModelMapper shotModelMapper) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.service = service;
        this.shotManager = shotManager;
        this.userManager = userManager;
        this.shotModelMapper = shotModelMapper;
    }

    public void init(Long idUser){
        this.idUser = idUser;
    }

    @Override public void run() throws SQLException, IOException {
        user = getUserByIdFromDatabase();
        if(user == null){
            user = getUserByIdFromService();
        }
        //OfflineMode
        Timber.e("Sending Offline Shots");
        List<ShotEntity> latestShotsOffline = getLatestShotsFromDatabase();
        postSuccessfulEvent(new LatestShotsResultEvent(getLatestShotModels(latestShotsOffline)));

        if(hasInternetConnection()){
            //OnlineMode
            Timber.e("Sending Shots Updated from Service");
            List<ShotEntity> latestShots = getLatestsShotsFromService();
            postSuccessfulEvent(new LatestShotsResultEvent(getLatestShotModels(latestShots)));
        }
    }

    public List<ShotModel> getLatestShotModels(List<ShotEntity> shotEntities){
        List<ShotModel> shotModels = new ArrayList<>(shotEntities.size());
        for(ShotEntity shot:shotEntities) {
            if (shot.getType() != ShotEntity.TYPE_WATCH_NEGATIVE) {
                shotModels.add(shotModelMapper.toShotModel(user, shot));
            }
        }
        return shotModels;
    }

    public List<ShotEntity> getLatestsShotsFromService() throws IOException {
        List<ShotEntity> shotEntities = service.getLatestsShotsFromIdUser(idUser, LATEST_SHOTS_NUMBER);
        shotManager.saveShots(shotEntities);
        return getLatestShotsFromDatabase();
    }

    public List<ShotEntity> getLatestShotsFromDatabase(){
        return shotManager.getLatestShotsFromIdUser(idUser,LATEST_SHOTS_NUMBER);
    }


    public UserEntity getUserByIdFromDatabase(){

        return userManager.getUserByIdUser(idUser);
    }

    public UserEntity getUserByIdFromService() throws IOException {
        return service.getUserByIdUser(idUser);
    }


    @Override public boolean isNetworkRequired() {
        return false;
    }
}
