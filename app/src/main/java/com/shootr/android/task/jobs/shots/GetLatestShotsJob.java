package com.shootr.android.task.jobs.shots;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.bus.Main;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.shots.LatestShotsResultStream;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetLatestShotsJob extends ShootrBaseJob<LatestShotsResultStream> {

    private static final int PRIORITY = 5;
    public static final Integer LATEST_SHOTS_NUMBER = 3;

    private final ShootrService service;
    private final UserManager userManager;
    private final ShotRepository localShotRepository;
    private final ShotRepository remoteShotRepository;
    private final UserRepository userRepository;
    private final ShotModelMapper shotModelMapper;

    private String idUser;

    @Inject public GetLatestShotsJob(Application application, @Main Bus bus, NetworkUtil networkUtil,
      ShootrService service, UserManager userManager, @Local ShotRepository localShotRepository,
      @Remote ShotRepository remoteShotRepository, @Remote UserRepository userRepository, ShotModelMapper shotModelMapper) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.service = service;
        this.userManager = userManager;
        this.localShotRepository = localShotRepository;
        this.remoteShotRepository = remoteShotRepository;
        this.userRepository = userRepository;
        this.shotModelMapper = shotModelMapper;
    }

    public void init(String idUser){
        this.idUser = idUser;
    }

    @Override public void run() throws SQLException, IOException {
        //OfflineMode
        List<ShotModel> latestShotsOffline = getLatestShotsFromDatabase();
        postSuccessfulEvent(new LatestShotsResultStream(latestShotsOffline));

        if(hasInternetConnection()){
            //OnlineMode
            List<ShotModel> latestShots = getLatestsShotsFromService();
            postSuccessfulEvent(new LatestShotsResultStream(latestShots));
        }
    }

    public List<ShotModel> getLatestsShotsFromService() throws IOException {
        List<Shot> shotsFromUser = remoteShotRepository.getShotsFromUser(idUser, LATEST_SHOTS_NUMBER);
        List<String> followingIds = new ArrayList<>();
        for (User user : userRepository.getPeople()) {
            followingIds.add(user.getIdUser());
        }
        if(followingIds.contains(idUser)) {
            localShotRepository.putShots(shotsFromUser);
        }
        return shotModelMapper.transform(shotsFromUser);
    }

    public List<ShotModel> getLatestShotsFromDatabase(){
        List<Shot> shotsFromUser = localShotRepository.getShotsFromUser(idUser, LATEST_SHOTS_NUMBER);
        return shotModelMapper.transform(shotsFromUser);
    }

    @Override public boolean isNetworkRequired() {
        return false;
    }
}
