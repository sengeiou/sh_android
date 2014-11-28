package com.shootr.android.task.jobs.profile;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.SessionManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.profile.UploadProfilePhotoEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.util.TimeUtils;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;
import org.json.JSONException;

public class RemoveProfilePhotoJob extends ShootrBaseJob<UploadProfilePhotoEvent> {

    private static final int PRIORITY = 5;
    private final ShootrService shootrService;
    private final UserManager userManager;
    private final SessionManager sessionManager;
    private final UserModelMapper userModelMapper;
    private final TimeUtils timeUtils;

    @Inject public RemoveProfilePhotoJob(Application application, Bus bus, NetworkUtil networkUtil,
      ShootrService shootrService, UserManager userManager, SessionManager sessionManager, UserModelMapper userModelMapper, TimeUtils timeUtils) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.shootrService = shootrService;
        this.userManager = userManager;
        this.sessionManager = sessionManager;
        this.userModelMapper = userModelMapper;
        this.timeUtils = timeUtils;
    }

    @Override protected void run() throws SQLException, IOException, JSONException {
        UserEntity updatedUser = setCurrentUserWithoutPhoto();

        postSuccessfulEvent(new UploadProfilePhotoEvent(userModelMapper.toUserModel(updatedUser, null, true)));
    }

    private UserEntity setCurrentUserWithoutPhoto() throws IOException {
        UserEntity currentUser = sessionManager.getCurrentUser();
        currentUser.setPhoto(null);
        currentUser.setCsysModified(timeUtils.getCurrentDate());
        userManager.saveUser(currentUser);
        currentUser = shootrService.saveUserProfile(currentUser);
        userManager.saveUser(currentUser);
        return currentUser;
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
