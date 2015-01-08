package com.shootr.android.task.jobs.profile;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.service.PhotoService;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.profile.UploadProfilePhotoEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.mappers.UserEntityModelMapper;
import com.shootr.android.util.ImageResizer;
import com.shootr.android.util.TimeUtils;
import com.squareup.otto.Bus;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;
import org.json.JSONException;

public class UploadProfilePhotoJob extends ShootrBaseJob<UploadProfilePhotoEvent> {

    private static final int PRIORITY = 5;
    private final ShootrService shootrService;
    private final PhotoService photoService;
    private final UserManager userManager;
    private final SessionRepository sessionRepository;
    private final ImageResizer imageResizer;
    private final UserEntityModelMapper userModelMapper;
    private final TimeUtils timeUtils;
    private final UserEntityMapper userEntityMapper;

    private File photoFile;

    @Inject public UploadProfilePhotoJob(Application application, Bus bus, NetworkUtil networkUtil, ShootrService shootrService, PhotoService photoService,
      UserManager userManager, SessionRepository sessionRepository, ImageResizer imageResizer, UserEntityModelMapper userModelMapper,
      TimeUtils timeUtils, UserEntityMapper userEntityMapper) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.shootrService = shootrService;
        this.photoService = photoService;
        this.userManager = userManager;
        this.sessionRepository = sessionRepository;
        this.imageResizer = imageResizer;
        this.userModelMapper = userModelMapper;
        this.timeUtils = timeUtils;
        this.userEntityMapper = userEntityMapper;
    }

    public void init(File photoFile) {
        this.photoFile = photoFile;
    }

    @Override protected void run() throws SQLException, IOException, JSONException {
        File imageFile = getResizedImage(photoFile);
        String photoUrl = uploadPhoto(imageFile);
        UserEntity updatedUser = setCurrentUserPhoto(photoUrl);

        postSuccessfulEvent(new UploadProfilePhotoEvent(userModelMapper.toUserModel(updatedUser, null, true)));
    }

    private UserEntity setCurrentUserPhoto(String photoUrl) throws IOException {
        User currentUser = sessionRepository.getCurrentUser();
        UserEntity currentUserEntity = userEntityMapper.transform(currentUser);

        currentUserEntity.setPhoto(photoUrl);
        currentUserEntity.setCsysModified(timeUtils.getCurrentDate());
        userManager.saveUser(currentUserEntity);
        currentUserEntity = shootrService.saveUserProfile(currentUserEntity);
        userManager.saveUser(currentUserEntity);
        return currentUserEntity;
    }

    private String uploadPhoto(File imageFile) throws IOException, JSONException {
        return photoService.uploadProfilePhotoAndGetUrl(imageFile);
    }

    private File getResizedImage(File newPhotoFile) throws IOException {
        return imageResizer.getResizedCroppedImageFile(newPhotoFile);
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
