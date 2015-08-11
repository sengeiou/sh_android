package com.shootr.android.task.jobs.profile;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.bus.Main;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.data.repository.sync.SyncableUserEntityFactory;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.utils.TimeUtils;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.profile.UploadProfilePhotoStream;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.mappers.UserEntityModelMapper;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;
import org.json.JSONException;

public class RemoveProfilePhotoJob extends ShootrBaseJob<UploadProfilePhotoStream> {

    private static final int PRIORITY = 5;
    private final ShootrService shootrService;
    private final UserManager userManager;
    private final SessionRepository sessionRepository;
    private final UserEntityModelMapper userModelMapper;
    private final TimeUtils timeUtils;
    private final UserEntityMapper userEntityMapper;
    private final SyncableUserEntityFactory syncableUserEntityFactory;

    @Inject public RemoveProfilePhotoJob(Application application, @Main Bus bus, NetworkUtil networkUtil,
      ShootrService shootrService, UserManager userManager, SessionRepository sessionRepository, UserEntityModelMapper userModelMapper, TimeUtils timeUtils,
      UserEntityMapper userEntityMapper, SyncableUserEntityFactory syncableUserEntityFactory) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.shootrService = shootrService;
        this.userManager = userManager;
        this.sessionRepository = sessionRepository;
        this.userModelMapper = userModelMapper;
        this.timeUtils = timeUtils;
        this.userEntityMapper = userEntityMapper;
        this.syncableUserEntityFactory = syncableUserEntityFactory;
    }

    @Override protected void run() throws SQLException, IOException, JSONException {
        UserEntity updatedUser = setCurrentUserWithoutPhoto();

        postSuccessfulEvent(new UploadProfilePhotoStream(userModelMapper.toUserModel(updatedUser, null, true)));
    }

    private UserEntity setCurrentUserWithoutPhoto() throws IOException {
        User currentUser = sessionRepository.getCurrentUser();
        UserEntity currentUserEntity = syncableUserEntityFactory.updatedOrNewEntity(currentUser);
        currentUserEntity.setPhoto(null);
        currentUserEntity.setModified(timeUtils.getCurrentDate());
        storeUpdatedUser(currentUserEntity);
        UserEntity remoteUpdatedUserEntity = shootrService.saveUserProfile(currentUserEntity);
        storeUpdatedUser(remoteUpdatedUserEntity);
        return currentUserEntity;
    }

    private void storeUpdatedUser(UserEntity updatedUserEntity) {
        userManager.saveUser(updatedUserEntity);
        sessionRepository.setCurrentUser(userEntityMapper.transform(updatedUserEntity));
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
